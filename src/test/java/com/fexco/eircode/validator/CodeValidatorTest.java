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
    public void mustRejectNullEircode() throws Exception {
        Assert.assertFalse(validator.validateEirCode(null));
    }

    @Test
    public void mustRejectEmptyEircode() throws Exception {
        Assert.assertFalse(validator.validateEirCode(""));
    }

    @Test
    public void mustRejectInvalidEircode() throws Exception {
        Assert.assertFalse(validator.validateEirCode("1234ABCD"));
    }

    @Test
    public void mustValidateEircode() throws Exception {
        Assert.assertTrue(validator.validateEirCode("D02X285"));
    }

    @Test
    public void mustRejectNullPostalCode() throws Exception {
        Assert.assertFalse(validator.validatePostalCode(null));
    }

    @Test
    public void mustRejectEmptyPostalCode() throws Exception {
        Assert.assertFalse(validator.validatePostalCode(""));
    }

    @Test
    public void mustRejectInvalidPostalCode() throws Exception {
        Assert.assertFalse(validator.validatePostalCode("codigo postal"));
    }

    @Test
    public void mustValidatePostalCode() throws Exception {
        Assert.assertTrue(validator.validatePostalCode("NR147PZ"));
    }
}