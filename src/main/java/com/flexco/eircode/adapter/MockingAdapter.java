package com.flexco.eircode.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("mock")
public class MockingAdapter implements ApiAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String forward(RestTemplate template, String urlSuffix, String queryString) {
        logger.info("returning mocked response");

        return "[{\"addressline1\":\"Some Address in Brazil\",\"addressline2\":\"Barra da Tijuca, Rio de Janeiro\",\"addressline3\":\"Rio de Janeiro\",\"summaryline\":\"Rubbish\",\"organisation\":\"The Brazilian Conspiracy\",\"buildingname\":\"Maracana\",\"premise\":\"Made Up Place\",\"street\":\"Made Up Street\",\"dependentlocality\":\"Mocked Locality\",\"posttown\":\"rio de janeiro\",\"county\":\"Rio de Janeiro\",\"postcode\":\"BRA 123\"}]";
    }

    @Override
    public MediaType extractMediaType(String queryString) {
        return MediaType.APPLICATION_JSON;
    }

}
