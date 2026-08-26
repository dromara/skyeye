/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.framework.file.core.client.FileClient;
import com.skyeye.framework.file.core.client.FileClientConfig;
import com.skyeye.framework.file.core.client.FileClientFactory;
import com.skyeye.upload.dao.FileConfigDao;
import com.skyeye.upload.entity.FileConfig;
import com.skyeye.upload.enums.FileStorageEnum;
import com.skyeye.upload.service.FileConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName: FileConfigServiceImpl
 * @Description: 文件配置服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 17:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "文件配置", groupName = "文件配置", tenant = TenantEnum.PLATE)
public class FileConfigServiceImpl extends SkyeyeBusinessServiceImpl<FileConfigDao, FileConfig> implements FileConfigService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private FileClientFactory fileClientFactory;

    @Autowired
    private Validator validator;

    private static final String FILE_CONFIG_IS_DEFAULT_CACHE_KEY = "skyeye:fileConfig:isDefault";

    @Override
    public void queryFileConfigSelectList(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<FileConfig> wrapper = new QueryWrapper<>();
        if (tableSelectInfo != null && StrUtil.isNotBlank(tableSelectInfo.getKeyword())) {
            wrapper.like("name", tableSelectInfo.getKeyword().trim());
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(FileConfig::getIsDefault));
        wrapper.orderByDesc(CommonConstants.CREATE_TIME_KEY);
        List<FileConfig> list = list(wrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        // 下拉列表不返回 config（含密钥等敏感信息）
        beans.forEach(bean -> bean.remove("config"));
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        iAuthUserService.setNameForMap(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void validatorEntity(FileConfig entity) {
        super.validatorEntity(entity);
        // 解析配置
        Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(entity.getStorage()).getConfigClass();
        if (ObjectUtil.isNull(configClass)) {
            throw new CustomException("文件存储类型的配置不存在");
        }
        FileClientConfig config = JSONUtil.toBean(entity.getConfig(), configClass);
        entity.setConfigMation(config);
        Assert.notNull(entity.getConfigMation());
        // 验证参数
        entity.getConfigMation().validate(validator);
    }

    @Override
    public void createPrepose(FileConfig entity) {
        clearOtherDefaults(entity);
    }

    @Override
    public void updatePrepose(FileConfig entity) {
        clearOtherDefaults(entity);
    }

    /**
     * 设为默认时，把其它配置的默认标记清掉（新增/编辑都要处理），并同步清理相关缓存。
     */
    private void clearOtherDefaults(FileConfig entity) {
        if (!Objects.equals(entity.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())) {
            return;
        }
        QueryWrapper<FileConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileConfig::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
        if (StrUtil.isNotBlank(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        List<FileConfig> previousDefaults = list(queryWrapper);

        UpdateWrapper<FileConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(MybatisPlusUtil.toColumns(FileConfig::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
        if (StrUtil.isNotBlank(entity.getId())) {
            updateWrapper.ne(CommonConstants.ID, entity.getId());
        }
        update(updateWrapper);

        if (previousDefaults != null && !previousDefaults.isEmpty()) {
            List<String> previousDefaultIds = new ArrayList<>();
            for (FileConfig previousDefault : previousDefaults) {
                if (StrUtil.isNotBlank(previousDefault.getId())) {
                    previousDefaultIds.add(previousDefault.getId());
                    fileClientFactory.removeFileClient(previousDefault.getId());
                }
            }
            if (!previousDefaultIds.isEmpty()) {
                clearCache(previousDefaultIds);
            }
        }
        jedisClientService.del(FILE_CONFIG_IS_DEFAULT_CACHE_KEY);
    }

    @Override
    public void updatePostpose(FileConfig entity, String userId) {
        // 移除文件客户端
        fileClientFactory.removeFileClient(entity.getId());
    }

    @Override
    public void writePostpose(FileConfig entity, String userId) {
        // 默认配置可能变化，统一清缓存（含「取消默认」场景）
        jedisClientService.del(FILE_CONFIG_IS_DEFAULT_CACHE_KEY);
    }

    @Override
    public FileConfig selectById(String id) {
        FileConfig fileConfig = super.selectById(id);
        Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(fileConfig.getStorage()).getConfigClass();
        FileClientConfig config = JSONUtil.toBean(fileConfig.getConfig(), configClass);
        fileConfig.setConfigMation(config);
        return fileConfig;
    }

    @Override
    public void deletePostpose(FileConfig entity) {
        // 删除后默认缓存一律失效，避免残留指向已删配置
        jedisClientService.del(FILE_CONFIG_IS_DEFAULT_CACHE_KEY);
        // 移除文件客户端
        fileClientFactory.removeFileClient(entity.getId());
    }

    @Override
    public FileClient getMasterFileClient() {
        FileConfig fileConfig = redisCache.getBean(FILE_CONFIG_IS_DEFAULT_CACHE_KEY, key -> {
            QueryWrapper<FileConfig> wrapper = new QueryWrapper<>();
            wrapper.eq(MybatisPlusUtil.toColumns(FileConfig::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            FileConfig bean = getOne(wrapper, false);
            Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(bean.getStorage()).getConfigClass();
            FileClientConfig config = JSONUtil.toBean(bean.getConfig(), configClass);

            if (bean != null) {
                fileClientFactory.createOrUpdateFileClient(bean.getId(), bean.getStorage(), config);
            }
            return bean;
        }, RedisConstants.THIRTY_DAY_SECONDS, FileClient.class);
        if (fileConfig == null) {
            throw new CustomException("没有设置默认文件存储");
        }

        return fileClientFactory.getFileClient(fileConfig.getId());
    }

    @Override
    public FileClient getFileClient(String configId) {
        FileClient fileClient = fileClientFactory.getFileClient(configId);
        if (fileClient == null) {
            FileConfig fileConfig = selectById(configId);
            if (fileConfig == null) {
                throw new CustomException("文件存储配置不存在");
            }
            fileClientFactory.createOrUpdateFileClient(fileConfig.getId(), fileConfig.getStorage(), fileConfig.getConfigMation());
            fileClient = fileClientFactory.getFileClient(configId);
        }
        return fileClient;
    }

    @Override
    public FileConfig getFileConfigByStorage(Integer storage) {
        if (storage == null) {
            QueryWrapper<FileConfig> wrapper = new QueryWrapper<>();
            wrapper.eq(MybatisPlusUtil.toColumns(FileConfig::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            FileConfig bean = getOne(wrapper, false);
            if (bean == null) {
                return null;
            }
            return selectById(bean.getId());
        }
        FileStorageEnum storageEnum = FileStorageEnum.getByStorage(storage);
        if (storageEnum == null) {
            return null;
        }
        // 优先取该类型下的默认配置，否则取第一条
        QueryWrapper<FileConfig> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(FileConfig::getStorage), storage);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(FileConfig::getIsDefault));
        FileConfig bean = getOne(wrapper, false);
        if (bean == null) {
            return null;
        }
        return selectById(bean.getId());
    }

    @Override
    public FileClient getFileClientByStorage(Integer storage) {
        FileConfig fileConfig = getFileConfigByStorage(storage);
        if (fileConfig == null) {
            return null;
        }
        return getFileClient(fileConfig.getId());
    }
}
