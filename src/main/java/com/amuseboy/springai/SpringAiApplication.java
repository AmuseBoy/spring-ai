package com.amuseboy.springai;

import com.amuseboy.springai.service.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class SpringAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiApplication.class, args);
    }

    //注册mcp工具
    @Bean
    public MethodToolCallbackProvider weatherTools(WeatherService weatherService){
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, MethodToolCallbackProvider methodToolCallbackProvider,
                                 SyncMcpToolCallbackProvider syncMcpToolCallbackProvider, VectorStore vectorStore) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你可以使用mcp的工具来处理消息")
                .defaultTools(methodToolCallbackProvider.getToolCallbacks())
                .defaultTools(syncMcpToolCallbackProvider.getToolCallbacks())
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().similarityThreshold(0.7d).topK(6).build()),//这个配置会在向量数据库中查询文档
                        new SimpleLoggerAdvisor())
                .build();
    }


}
