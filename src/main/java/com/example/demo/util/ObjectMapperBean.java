package com.example.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperBean {

	@Bean
	public ObjectMapper getObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper;
	}
}