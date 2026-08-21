package com.skyeye.knowledge.controller;

import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.service.KnowledgeService;
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
 * AI知识库控制类
 */
@RestController
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @RequestMapping("/post/KnowledgeController/writeKnowledge")
    public KnowledgeResult writeKnowledge(@RequestBody Knowledge knowledge) {
        return KnowledgeResult.ok(knowledgeService.saveOrUpdate(knowledge));
    }

    @RequestMapping("/post/KnowledgeController/queryKnowledgePageList")
    public KnowledgeResult queryKnowledgePageList(@RequestBody(required = false) Map<String, Object> params) {
        int page = intValue(params, "page", 1);
        int limit = intValue(params, "limit", 10);
        String keyword = stringValue(params, "keyword");
        List<Knowledge> list = knowledgeService.queryPageList(page, limit, keyword);
        return KnowledgeResult.list(list, knowledgeService.count(keyword));
    }

    @RequestMapping("/post/KnowledgeController/queryKnowledgeList")
    public KnowledgeResult queryKnowledgeList() {
        List<Knowledge> list = knowledgeService.queryList();
        return KnowledgeResult.list(list, list.size());
    }

    @RequestMapping(value = "/post/KnowledgeController/selectKnowledgeById", method = RequestMethod.GET)
    public KnowledgeResult selectKnowledgeById(@RequestParam("id") String id) {
        return KnowledgeResult.ok(knowledgeService.selectById(id));
    }

    @RequestMapping(value = "/post/KnowledgeController/deleteKnowledgeById", method = RequestMethod.DELETE)
    public KnowledgeResult deleteKnowledgeById(@RequestParam("id") String id) {
        knowledgeService.deleteById(id);
        return KnowledgeResult.ok();
    }

    @RequestMapping("/post/KnowledgeController/rebuildKnowledge")
    public KnowledgeResult rebuildKnowledge(@RequestBody Map<String, Object> params) {
        knowledgeService.rebuildKnowledge(stringValue(params, "knowIds"));
        return KnowledgeResult.ok();
    }

    @RequestMapping("/post/KnowledgeController/knowledgeHitTest")
    public KnowledgeResult knowledgeHitTest(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> result = knowledgeService.hitTest(
            stringValue(params, "knowId"),
            stringValue(params, "queryText"),
            intValue(params, "topNumber", 5),
            doubleValue(params, "similarity"));
        return KnowledgeResult.list(result, result.size());
    }

    @RequestMapping("/post/KnowledgeController/knowledgeEmbeddingSearch")
    public KnowledgeResult knowledgeEmbeddingSearch(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> result = knowledgeService.embeddingSearch(
            stringValue(params, "knowIds"),
            stringValue(params, "queryText"),
            intValue(params, "topNumber", 5),
            doubleValue(params, "similarity"));
        return KnowledgeResult.list(result, result.size());
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

    private Double doubleValue(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return null;
        }
        try {
            return Double.parseDouble(String.valueOf(params.get(key)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String stringValue(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return null;
        }
        return String.valueOf(params.get(key));
    }
}
