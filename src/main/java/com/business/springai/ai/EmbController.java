package com.business.springai.ai;

import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("embedding")
public class EmbController {
    @Autowired
    OpenAiEmbeddingModel embeddingModel;
    @Autowired
    VectorStore vectorStore;
    @GetMapping("query")
    public Object query(@RequestParam(defaultValue = "我喜欢看电影") String question){
        SearchRequest buildSearchRequest = SearchRequest.builder().query(question).similarityThreshold(0.7).build();
        return vectorStore.similaritySearch(buildSearchRequest);
    }
    @GetMapping("add")
    public Object add(@RequestParam(defaultValue = "我喜欢看电影") String question){
        vectorStore.add(List.of(Document.builder()
                .id(UUID.randomUUID().toString())
                .text(question)
                .metadata(java.util.Map.of("id", UUID.randomUUID().toString(),"question",question))
                .build()));
        return "success";
    }
}
