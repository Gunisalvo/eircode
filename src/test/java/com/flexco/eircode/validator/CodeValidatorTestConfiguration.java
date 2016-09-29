package com.flexco.eircode.validator;

import com.flexco.eircode.EircodeApp;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.flexco.eircode.validator"})
@PropertySource("classpath:/application.properties")
public class CodeValidatorTestConfiguration {
}