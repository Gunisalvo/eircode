package com.flexco.eircode.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Profile("alliescomputing")
public class AlliescomputingAdapter implements ApiAdapter{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.url}")
    private String baseUrl;

    @Override
    @Cacheable(value ="forwards", key = "{ #root.methodName, #urlSuffix, #queryString }")
    public String forward(RestTemplate template, String urlSuffix, String queryString) {
        String forwardUrl = this.baseUrl + this.apiKey + urlSuffix;
        if (queryString != null) {
            forwardUrl += "?" + queryString;
        }
        String responseBody = template.getForObject(forwardUrl, String.class, queryString);
        logger.info("Response received: \n{}", responseBody);
        return responseBody;

    }

    @Override
    public MediaType extractMediaType(String queryString) {
        boolean isXml = false;
        if(queryString != null) {
            isXml = Arrays.asList(queryString.split("&")).stream().anyMatch(n ->
                    n.equals("format=xml")
            );
        }
        if(isXml){
            return MediaType.APPLICATION_XML;
        }else{
            return MediaType.APPLICATION_JSON;
        }

    }

}
