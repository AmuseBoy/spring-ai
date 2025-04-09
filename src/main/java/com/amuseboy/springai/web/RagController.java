package com.amuseboy.springai.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class RagController {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;



    //RAG例子，chatClient已经增加QuestionAnswerAdvisor
    @GetMapping("/rag")
    public String ragChat(@RequestParam(value = "message", defaultValue = "谁最帅？") String message){

        return chatClient.prompt().user(message).call().content();
    }
}
