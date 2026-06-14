/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 官网产品功能矩阵图标枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PortalProductFeatureIconEnum implements SkyeyeEnumClass {

    FILE_OUTLINED(1, "文档", "FileOutlined", true, false),
    APPSTORE_OUTLINED(2, "应用", "AppstoreOutlined", true, true),
    TEAM_OUTLINED(3, "团队", "TeamOutlined", true, false),
    CUSTOMER_SERVICE_OUTLINED(4, "客服", "CustomerServiceOutlined", true, false),
    SETTING_OUTLINED(5, "设置", "SettingOutlined", true, false),
    ROCKET_OUTLINED(6, "扩展", "RocketOutlined", true, false),
    BANK_OUTLINED(7, "财务", "BankOutlined", true, false),
    PROJECT_OUTLINED(8, "项目", "ProjectOutlined", true, false),
    USER_OUTLINED(9, "用户", "UserOutlined", true, false),
    SHOP_OUTLINED(10, "商城", "ShopOutlined", true, false),
    MOBILE_OUTLINED(11, "移动", "MobileOutlined", true, false),
    CLOCK_CIRCLE_OUTLINED(12, "排班", "ClockCircleOutlined", true, false),
    DEPLOYMENT_UNIT_OUTLINED(13, "MES", "DeploymentUnitOutlined", true, false),
    CODE_OUTLINED(14, "代码", "CodeOutlined", true, false),
    APARTMENT_OUTLINED(15, "组织", "ApartmentOutlined", true, false),
    LAYOUT_OUTLINED(16, "布局", "LayoutOutlined", true, false),
    NODE_INDEX_OUTLINED(17, "流程", "NodeIndexOutlined", true, false),
    BARCODE_OUTLINED(18, "编码", "BarcodeOutlined", true, false),
    PRINTER_OUTLINED(19, "打印", "PrinterOutlined", true, false),
    SAFETY_CERTIFICATE_OUTLINED(20, "安全", "SafetyCertificateOutlined", true, false),
    BUILD_OUTLINED(21, "构建", "BuildOutlined", true, false),
    CLUSTER_OUTLINED(22, "集群", "ClusterOutlined", true, false),
    SECURITY_SCAN_OUTLINED(23, "认证", "SecurityScanOutlined", true, false),
    CODE_SANDBOX_OUTLINED(24, "沙箱", "CodeSandboxOutlined", true, false),
    DASHBOARD_OUTLINED(25, "大屏", "DashboardOutlined", true, false),
    THUNDERBOLT_OUTLINED(26, "性能", "ThunderboltOutlined", true, false),
    API_OUTLINED(27, "接口", "ApiOutlined", true, false);

    private Integer key;

    private String value;

    /** Ant Design Vue 图标组件名，供官网展示 */
    private String iconCode;

    private Boolean show;

    private Boolean isDefault;

    public static String getIconCodeByKey(Integer key) {
        if (key == null) {
            return getDefaultIconCode();
        }
        for (PortalProductFeatureIconEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item.getIconCode();
            }
        }
        return getDefaultIconCode();
    }

    public static Integer getDefaultKey() {
        for (PortalProductFeatureIconEnum item : values()) {
            if (Boolean.TRUE.equals(item.getIsDefault())) {
                return item.getKey();
            }
        }
        return APPSTORE_OUTLINED.getKey();
    }

    public static String getDefaultIconCode() {
        return getIconCodeByKey(getDefaultKey());
    }

    /**
     * 兼容历史字符串 icon 值（如 FileOutlined）转枚举 key
     */
    public static Integer resolveKey(Object icon) {
        if (icon == null) {
            return getDefaultKey();
        }
        if (icon instanceof Number) {
            return ((Number) icon).intValue();
        }
        String iconStr = String.valueOf(icon);
        if (StrUtil.isBlank(iconStr)) {
            return getDefaultKey();
        }
        try {
            return Integer.parseInt(iconStr);
        } catch (NumberFormatException ignored) {
            for (PortalProductFeatureIconEnum item : values()) {
                if (StrUtil.equals(item.getIconCode(), iconStr) || StrUtil.equals(item.name(), iconStr)) {
                    return item.getKey();
                }
            }
        }
        return getDefaultKey();
    }
}
