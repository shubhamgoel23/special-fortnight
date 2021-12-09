package com.demo.micro.config;

import static com.fasterxml.jackson.core.JsonToken.VALUE_STRING;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@JsonComponent
public class JsonDeserializerConfig extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		return parser.hasToken(VALUE_STRING) ? parser.getText().trim() : null;
	}

}
