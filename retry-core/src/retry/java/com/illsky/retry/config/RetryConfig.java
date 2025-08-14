package com.illsky.retry.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: succongccong
 * @date: 2023-06-08
 * @description:
 * @modiFy:
 */
@Data
@Configuration
public class RetryConfig {

    @Value("${maxretrycount:5}")
    private int maxRetryCount;

}
