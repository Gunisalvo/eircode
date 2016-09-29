package com.fexco.eircode.api;

import com.fexco.eircode.validator.CodeValidator;
import com.fexco.eircode.adapter.ApiAdapter;

import io.swagger.annotations.*;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@Api(description = "Forwards HTTP Requests to configured APIs")
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "urlSuffix", value = "Terminating part of the URL that will be forwarded to the API", required = true, dataType = "string", paramType = "path")
    })
    @ApiOperation(value = "Forward Request", notes = "Forwards the URL to the configured API. NOTE: due to the dynamic nature of the URL construction this method cannot be tested by this GUI", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public ResponseEntity forwardRequest(@ApiIgnore @Autowired HttpServletRequest request) {
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eirCode", value = "Postal Code of the Republic of Ireland", required = true, dataType = "string", paramType = "path")
    })
    @ApiOperation(value = "Lookup EIRCODE", notes = "Forwards the URL mapped EIRCODEs to the configured API", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public ResponseEntity lookupEirCode(@PathVariable("eirCode") String eirCode, @ApiIgnore @Autowired HttpServletRequest request) {
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postalCode", value = "Postal Code of the United Kingdom", required = true, dataType = "string", paramType = "path")
    })
    @ApiOperation(value = "Lookup POSTAL CODE", notes = "Forwards the URL mapped UK's postal codes to the configured API", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public ResponseEntity lookupPostalCode(@PathVariable("postalCode") String postalCode, @ApiIgnore @Autowired HttpServletRequest request) {
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
    @ApiOperation(value = "Information", notes = "Retrieves relevant information about the application", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Failure")})
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
