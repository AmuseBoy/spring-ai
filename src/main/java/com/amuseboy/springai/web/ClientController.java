package com.amuseboy.springai.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * mcp例子
 */
@RestController
@Slf4j
public class ClientController {

    @Autowired(required = false)
    private OllamaChatModel ollamaChatModel;

    @Autowired(required = false)
    private OpenAiChatModel openAiChatModel;

    @Autowired(required = false)
    private MethodToolCallbackProvider methodToolCallbackProvider;

    @Autowired(required = false)
    private SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;

    //McpClientAutoConfiguration根据配置中自动注入同步还是异步的，会和MethodToolCallbackProvider冲突
//    @Autowired
//    private ToolCallbackProvider toolCallbackProvider;

    @RequestMapping("/api/client")
    public String chat(@RequestParam(value = "message", defaultValue = "今天北京天气如何？") String message){
        ChatClient chatClient = null;
        if(ollamaChatModel != null){
            chatClient = ChatClient.builder(ollamaChatModel).defaultTools("getWeatherByCity").build();
        }
        if(openAiChatModel != null){
            //chatClient = ChatClient.builder(openAiChatModel).defaultTools("getWeatherByCity").build();

            //System.out.println(toolCallbackProvider.getToolCallbacks().length);
            System.out.println(syncMcpToolCallbackProvider.getToolCallbacks().length);
            System.out.println(methodToolCallbackProvider.getToolCallbacks().length);

            chatClient = ChatClient.builder(openAiChatModel)
                    .defaultSystem("你可以使用mcp的工具来处理消息")
                    .defaultTools(methodToolCallbackProvider.getToolCallbacks())
                    //.defaultTools(syncMcpToolCallbackProvider.getToolCallbacks())
                    .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                    .build();
        }
        String response = chatClient.prompt().user(message).call().content();
        log.info("响应结果:" + response);
        return response;
    }


}
