package com.fexco.eircode.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CodeValidator {

    private Pattern eirCodeRegex;

    private Pattern postalCodeRegex;

    @Autowired
    public CodeValidator(@Value("${regex.eircode}") String eirCodeRegex,
                         @Value("${regex.postalcode}") String postalCodeRegex){
        this.eirCodeRegex = Pattern.compile(eirCodeRegex);
        this.postalCodeRegex = Pattern.compile(postalCodeRegex);
    }

    public boolean validateEirCode(String eirCode){
        if(eirCode == null) return false;
        return this.eirCodeRegex.matcher(eirCode).matches();
    }

    public boolean validatePostalCode(String postalCode){
        if(postalCode == null) return false;
        return this.postalCodeRegex.matcher(postalCode).matches();
    }
}
