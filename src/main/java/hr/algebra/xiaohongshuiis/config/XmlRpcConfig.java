package hr.algebra.xiaohongshuiis.config;

import hr.algebra.xiaohongshuiis.service.DhmzWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class XmlRpcConfig {

    @Autowired
    private DhmzWeatherService dhmzWeatherService;

    @Bean
    public HandlerMapping xmlRpcHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("/xmlrpc", new WeatherXmlRpcHandler(dhmzWeatherService));
        mapping.setUrlMap(urlMap);
        mapping.setOrder(1);
        return mapping;
    }
}