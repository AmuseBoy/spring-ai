package com.amuseboy.springai;

import com.amuseboy.springai.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
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

}
