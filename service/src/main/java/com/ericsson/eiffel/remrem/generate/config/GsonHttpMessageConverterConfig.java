package com.ericsson.eiffel.remrem.generate.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Configuration
public class GsonHttpMessageConverterConfig {

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverterWithValidate();
        converter.setGson(gson());
        return converter;
    }

    private Gson gson() {
        return new GsonBuilder().create();
    }
}
