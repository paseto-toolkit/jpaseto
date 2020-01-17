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
package dev.paseto.jpaseto.io.gson;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.paseto.jpaseto.io.SerializationException;
import dev.paseto.jpaseto.io.Serializer;
import dev.paseto.jpaseto.lang.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @since 0.1.0
 */
@AutoService(Serializer.class)
public class GsonSerializer<T> implements Serializer<T> {

    static final Gson DEFAULT_GSON = new GsonBuilder().disableHtmlEscaping().create();
    private Gson gson;

    @SuppressWarnings("unused") //used via reflection by RuntimeClasspathDeserializerLocator
    public GsonSerializer() {
        this(DEFAULT_GSON);
    }

    @SuppressWarnings("WeakerAccess") //intended for end-users to use when providing a custom gson
    public GsonSerializer(Gson gson) {
        Assert.notNull(gson, "gson cannot be null.");
        this.gson = gson;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        Assert.notNull(t, "Object to serialize cannot be null.");
        try {
            return writeValueAsBytes(t);
        } catch (Exception e) {
            String msg = "Unable to serialize object: " + e.getMessage();
            throw new SerializationException(msg, e);
        }
    }

    @SuppressWarnings("WeakerAccess") //for testing
    protected byte[] writeValueAsBytes(T t) {
        Object o;
        if (t instanceof byte[]) {
            o = Base64.getEncoder().encodeToString((byte[])t);
        } else if (t instanceof char[]) {
            o = new String((char[]) t);
        } else {
            o = t;
        }
        return this.gson.toJson(o).getBytes(StandardCharsets.UTF_8);
    }
}
