package com.demo.micro.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.demo.micro.util.HelperClass.*;

@Component
public class StringDeserializerConfig implements Converter<String, String> {

    @Override
    public String convert(String source) {
        String value = source != null ? trimString.andThen(sanitizeString).apply(source) : null;
        Assert.isTrue(isStringWithoutSpecialCharacters.test(value), "Special Characters are not allowed as input");
        return value;
    }

}
