package com.amuseboy.springai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WeatherService {

    @Tool(description = "根据城市名称获取天气预报")
    public String getWeatherByCity(String city) {
        log.info("入参:" + city);
        Map<String, String> mockData = Map.of(
                "西安", "晴天",
                "北京", "小雨",
                "上海", "大雨" );

        return mockData.getOrDefault(city, "抱歉:未查询到对应的城市");
    }

}
