package com.fexco.eircode.validator;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=CodeValidatorTestConfiguration.class)
public class CodeValidatorTest {

    @Autowired
    private CodeValidator validator;

    @Test
    public void mustValidateEircode() throws Exception {
        Assert.assertFalse(validator.validateEirCode("banana"));
        Assert.assertTrue(validator.validateEirCode("D02X285"));
    }

    @Test
    public void mustValidatePostalCode() throws Exception {
        Assert.assertFalse(validator.validatePostalCode("banana"));
        Assert.assertTrue(validator.validatePostalCode("NR147PZ"));
    }
}