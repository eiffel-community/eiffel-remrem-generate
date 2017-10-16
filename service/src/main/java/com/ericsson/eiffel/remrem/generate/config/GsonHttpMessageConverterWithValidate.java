package com.ericsson.eiffel.remrem.generate.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;


/**
 * Ugly class that "overrides" private methods in order to get hold of the inputstream (the json body) so that we can
 * perform a check for duplicate keys.
 * <p>
 *
 * Gson does not support check for duplicate keys yet.
 * See: <a href="https://github.com/google/gson/issues/647">GitHub issue</a>
 */
public class GsonHttpMessageConverterWithValidate extends GsonHttpMessageConverter {

    private Gson gson;

    @Override
    public void setGson(final Gson gson) {
        super.setGson(gson);
        this.gson = gson;
    }

    @Override
    public Object read(final Type type, final Class<?> contextClass, final HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {

        TypeToken<?> token = getTypeToken(type);
        return readTypeToken(token, inputMessage);
    }

    private Object readTypeToken(TypeToken<?> token, HttpInputMessage inputMessage) throws IOException {
        Reader reader = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));

        try {
            final String json = IOUtils.toString(reader);
            reader.close();

            // do the actual validation
            final ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
            mapper.readTree(json);

            return this.gson.fromJson(json, token.getType());
        } catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    private Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharset();
    }

}
