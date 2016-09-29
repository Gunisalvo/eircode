package com.fexco.eircode.adapter;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public interface ApiAdapter {

    String forward(RestTemplate template, String urlSuffix, String queryString);

    MediaType extractMediaType(String queryString);

}
