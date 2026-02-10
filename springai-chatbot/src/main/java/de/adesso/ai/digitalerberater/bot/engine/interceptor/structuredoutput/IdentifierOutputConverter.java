package de.adesso.ai.digitalerberater.bot.engine.interceptor.structuredoutput;

import org.springframework.ai.converter.StructuredOutputConverter;

public class IdentifierOutputConverter implements StructuredOutputConverter<String> {
    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public String convert(String source) {
        return source.trim().toUpperCase().replaceAll("[^A-Z0-9_]", "");
    }
}
