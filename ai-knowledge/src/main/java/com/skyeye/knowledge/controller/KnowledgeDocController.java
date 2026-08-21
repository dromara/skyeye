package com.skyeye.knowledge.controller;

import com.skyeye.knowledge.entity.KnowledgeDoc;
import com.skyeye.knowledge.service.KnowledgeDocService;
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
 * AI知识库文档控制类
 */
@RestController
public class KnowledgeDocController {

    @Autowired
    private KnowledgeDocService knowledgeDocService;

    @RequestMapping("/post/KnowledgeDocController/writeKnowledgeDoc")
    public KnowledgeResult writeKnowledgeDoc(@RequestBody KnowledgeDoc knowledgeDoc) {
        return KnowledgeResult.ok(knowledgeDocService.editDocument(knowledgeDoc));
    }

    @RequestMapping("/post/KnowledgeDocController/queryKnowledgeDocPageList")
    public KnowledgeResult queryKnowledgeDocPageList(@RequestBody(required = false) Map<String, Object> params) {
        int page = intValue(params, "page", 1);
        int limit = intValue(params, "limit", 10);
        String keyword = stringValue(params, "keyword");
        String objectId = stringValue(params, "objectId");
        List<KnowledgeDoc> list = knowledgeDocService.queryPageList(page, limit, keyword, objectId);
        return KnowledgeResult.list(list, knowledgeDocService.count(keyword, objectId));
    }

    @RequestMapping(value = "/post/KnowledgeDocController/selectKnowledgeDocById", method = RequestMethod.GET)
    public KnowledgeResult selectKnowledgeDocById(@RequestParam("id") String id) {
        return KnowledgeResult.ok(knowledgeDocService.selectById(id));
    }

    @RequestMapping("/post/KnowledgeDocController/rebuildKnowledgeDoc")
    public KnowledgeResult rebuildKnowledgeDoc(@RequestBody Map<String, Object> params) {
        knowledgeDocService.rebuildDocument(stringValue(params, "docIds"));
        return KnowledgeResult.ok();
    }

    @RequestMapping(value = "/post/KnowledgeDocController/deleteKnowledgeDocByIds", method = RequestMethod.DELETE)
    public KnowledgeResult deleteKnowledgeDocByIds(@RequestParam("ids") String ids) {
        knowledgeDocService.deleteDocByIds(ids);
        return KnowledgeResult.ok();
    }

    @RequestMapping(value = "/post/KnowledgeDocController/deleteAllKnowledgeDocByKnowId", method = RequestMethod.DELETE)
    public KnowledgeResult deleteAllKnowledgeDocByKnowId(@RequestParam("knowId") String knowId) {
        knowledgeDocService.deleteAllByKnowId(knowId);
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
