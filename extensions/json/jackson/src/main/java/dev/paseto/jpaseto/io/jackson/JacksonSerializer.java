/*
 * Copyright (C) 2014 jsonwebtoken.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.paseto.jpaseto.io.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.io.SerializationException;
import dev.paseto.jpaseto.io.Serializer;
import dev.paseto.jpaseto.lang.Assert;
import dev.paseto.jpaseto.lang.DateFormats;

import java.time.Instant;

/**
 * @since 0.1.0
 */
@AutoService(Serializer.class)
public class JacksonSerializer<T> implements Serializer<T> {

    static final ObjectMapper DEFAULT_OBJECT_MAPPER = createObjectMapper();

    private final ObjectWriter objectWriter;

    @SuppressWarnings("unused") //used via reflection by RuntimeClasspathDeserializerLocator
    public JacksonSerializer() {
        this(DEFAULT_OBJECT_MAPPER);
    }

    @SuppressWarnings("WeakerAccess") //intended for end-users to use when providing a custom ObjectMapper
    public JacksonSerializer(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper cannot be null.");
        this.objectWriter = objectMapper.writer();
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        Assert.notNull(t, "Object to serialize cannot be null.");
        try {
            return writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            String msg = "Unable to serialize object: " + e.getMessage();
            throw new SerializationException(msg, e);
        }
    }

    @SuppressWarnings("WeakerAccess") //for testing
    protected byte[] writeValueAsBytes(T t) throws JsonProcessingException {
        return this.objectWriter.writeValueAsBytes(t);
    }

    private static ObjectMapper createObjectMapper() {

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // custom time formatting replace 'Z' with +00:00
                .registerModule(new SimpleModule()
                    .addSerializer(Instant.class, new NonZInstantSerializer())
                );
    }
    
    private static class NonZInstantSerializer extends InstantSerializer {
        private static final long serialVersionUID = 1L;

        private NonZInstantSerializer() {
            super(InstantSerializer.INSTANCE, true, DateFormats.ISO_OFFSET_DATE_TIME);
        }
    }
}
