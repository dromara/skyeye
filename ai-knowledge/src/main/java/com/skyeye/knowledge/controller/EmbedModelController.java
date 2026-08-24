package com.skyeye.knowledge.controller;

import com.skyeye.knowledge.entity.EmbedModel;
import com.skyeye.knowledge.service.EmbedModelService;
import com.skyeye.knowledge.web.KnowledgeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 向量模型配置控制类
 */
@RestController
public class EmbedModelController {

    @Autowired
    private EmbedModelService embedModelService;

    @RequestMapping("/post/EmbedModelController/writeEmbedModel")
    public KnowledgeResult writeEmbedModel(@RequestBody EmbedModel embedModel) {
        return KnowledgeResult.ok(embedModelService.saveOrUpdate(embedModel));
    }

    @RequestMapping("/post/EmbedModelController/queryEmbedModelPageList")
    public KnowledgeResult queryEmbedModelPageList(@RequestBody(required = false) Map<String, Object> params) {
        int page = intValue(params, "page", 1);
        int limit = intValue(params, "limit", 10);
        String keyword = stringValue(params, "keyword");
        List<EmbedModel> list = embedModelService.queryPageList(page, limit, keyword);
        return KnowledgeResult.list(list, embedModelService.count(keyword));
    }

    @RequestMapping("/post/EmbedModelController/queryEmbedModelList")
    public KnowledgeResult queryEmbedModelList() {
        List<EmbedModel> list = embedModelService.queryList();
        return KnowledgeResult.list(list, list.size());
    }

    @RequestMapping(value = "/post/EmbedModelController/selectEmbedModelById", method = RequestMethod.GET)
    public KnowledgeResult selectEmbedModelById(@RequestParam("id") String id) {
        return KnowledgeResult.ok(embedModelService.selectById(id));
    }

    @RequestMapping(value = "/post/EmbedModelController/deleteEmbedModelById", method = RequestMethod.DELETE)
    public KnowledgeResult deleteEmbedModelById(@RequestParam("id") String id) {
        embedModelService.deleteById(id);
        return KnowledgeResult.ok();
    }

    private int intValue(Map<String, Object> params, String key, int defaultValue) {
        if (params == null || params.get(key) == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(params.get(key)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String stringValue(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return null;
        }
        return String.valueOf(params.get(key));
    }
}
