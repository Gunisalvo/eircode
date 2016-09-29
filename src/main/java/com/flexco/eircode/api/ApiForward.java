package com.flexco.eircode.api;

import com.flexco.eircode.adapter.ApiAdapter;

import com.flexco.eircode.validator.CodeValidator;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class ApiForward {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.name}")
    private String appName;

    @Value("${app.description}")
    private String appDescription;

    @Value("${app.version}")
    private String appVersion;

    @Value("${spring.profiles.active}")
    private String appApi;

    @Autowired
    private ApiAdapter apiAdapter;

    @Autowired
    private CodeValidator validator;

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/api-forward/**", method = RequestMethod.GET)
    public ResponseEntity forwardRequest(@Autowired HttpServletRequest request) {
        String uri = request.getRequestURI();
        logger.info("Forwarding: {}", request.getRequestURL().toString());
        String parameters = request.getQueryString();
        if(parameters != null) {
            logger.info("Parameters passed: {}", parameters);
        }
        String urlSuffix = uri.split("forward")[1];
        String resultBody = this.apiAdapter.forward(restTemplate,urlSuffix,parameters);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(this.apiAdapter.extractMediaType(parameters));
        return new ResponseEntity(resultBody, headers, HttpStatus.OK);

    }

    @RequestMapping(value = "/ie/{eirCode}", method = RequestMethod.GET)
    public ResponseEntity lookupEirCode(@PathVariable("eirCode") String eirCode, @Autowired HttpServletRequest request) {
        logger.info("Looking up EIR Code: {}", eirCode);
        if(!this.validator.validateEirCode(eirCode)){
            throw new IllegalArgumentException("Bad EIR Code: " + eirCode);
        }
        String parameters = request.getQueryString();
        if(parameters != null) {
            logger.info("Parameters passed: {}", parameters);
        }
        String resultBody = this.apiAdapter.forward(restTemplate, "/address/ie/" + eirCode, parameters);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(this.apiAdapter.extractMediaType(parameters));
        return new ResponseEntity(resultBody, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/uk/{postalCode}", method = RequestMethod.GET)
    public ResponseEntity lookupPostalCode(@PathVariable("postalCode") String postalCode, @Autowired HttpServletRequest request) {
        logger.info("Looking up Postal Code: {}", postalCode);
        if(!this.validator.validatePostalCode(postalCode)){
            throw new IllegalArgumentException("Bad POSTAL Code: " + postalCode);
        }
        String parameters = request.getQueryString();
        if(parameters != null) {
            logger.info("Parameters passed: {}", parameters);
        }
        String resultBody = this.apiAdapter.forward(restTemplate, "/address/uk/" + postalCode, parameters);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(this.apiAdapter.extractMediaType(parameters));
        return new ResponseEntity(resultBody, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,String> info(){
        logger.info("Retrieving app info");
        Map<String,String> info = new HashMap<>();
        info.put("name", this.appName);
        info.put("description", this.appDescription);
        info.put("version", this.appVersion);
        info.put("forward-api", this.appApi);
        return info;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleException(IllegalArgumentException e) {
        this.logger.error("IllegalArgumentException: ", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity("{\"msg\":\"please supply a valid CODE\"}", headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity handleException(IndexOutOfBoundsException e) {
        this.logger.error("IndexOutOfBoundsException: ", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity("{\"msg\":\"please supply a valid forward URL\"}", headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleException(HttpClientErrorException e) {
        this.logger.error("HttpClientErrorException: ", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity("{\"msg\":\"" + e.getMessage() + "\"}", headers, e.getStatusCode());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity handleException(HttpServerErrorException e) {
        this.logger.error("HttpServerErrorException: ", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity("{\"msg\":\"" + e.getMessage() + "\"}", headers, e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        this.logger.error("Unknown Error", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity("{\"msg\":\"This is very embarassing...\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
