package com.svsbrains.svscrypto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//Added this because my environment didn't have a default bean for rest template
@Configuration
public class RestTemplateConfig  {
    @Bean("restTemplateCustom")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}