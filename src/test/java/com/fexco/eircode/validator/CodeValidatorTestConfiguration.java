package com.fexco.eircode.validator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.fexco.eircode.validator"})
@PropertySource("classpath:/application.properties")
public class CodeValidatorTestConfiguration {
}