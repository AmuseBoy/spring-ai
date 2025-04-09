package com.amuseboy.springai.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatClient chatClient;

//    public ChatController(ChatClient.Builder chatClient) {
//        this.chatClient = chatClient.build();
//    }


    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "给我讲一个笑话") String message) {
        SystemMessage systemMessage = new SystemMessage("你是一个会讲笑话的智能助手，你可以讲各种风格的笑话");
        UserMessage userMessage = new UserMessage(message);
        List<Message> messages = List.of(systemMessage, userMessage);
        return this.chatClient.prompt().messages(messages).call().content();

        //return chatClient.prompt().user(message).call().content();
        //return chatClient.prompt(new Prompt(message)).call().content();
        //return chatClient.prompt(message).call().content();
    }

    @GetMapping("/ai/generateStream")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.prompt(prompt).stream().content();
    }


    //提示词模板
    @GetMapping("/ai/prompt")
    public String prompt(@RequestParam(value = "adjective", defaultValue = "冷笑话") String adjective,
                           @RequestParam(value = "topic", defaultValue = "小明") String topic) {
        System.out.println("请求进来了");
        PromptTemplate promptTemplate = new PromptTemplate("给我讲一个{adjective}笑话，主题是{topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic));

        return chatClient.prompt(prompt).call().content();
    }

    //多角色提示词
    @GetMapping("/ai/promptRoles")
    public String promptRoles() {
        String systemText = "你是一个三国通,熟读三国历史,你的名字是{name}";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "三国历史智能助手"));

        String userText = "说一下刘备的个人平生事迹";
        Message userMessage = new UserMessage(userText);

        Prompt prompt = new Prompt(systemMessage, userMessage);
        return chatClient.prompt(prompt).advisors(new SimpleLoggerAdvisor()).call().content();
    }


    @Value("classpath:/prompts/system-message.st")
    private Resource systemResource;

    //本地资源提示词
    @GetMapping("/ai/promptResource")
    public String promptResource() {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "三国历史智能助手"));

        String userText = "刘备在哪年称帝的？";
        Message userMessage = new UserMessage(userText);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.prompt(prompt).advisors(new SimpleLoggerAdvisor()).call().content();
    }

}