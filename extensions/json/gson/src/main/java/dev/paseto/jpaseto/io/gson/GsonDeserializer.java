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
import dev.paseto.jpaseto.io.DeserializationException;
import dev.paseto.jpaseto.io.Deserializer;
import dev.paseto.jpaseto.lang.Assert;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @since 0.1.0
 */
@AutoService(Deserializer.class)
public class GsonDeserializer<T> implements Deserializer<T> {

    private final Class<T> returnType;
    private final Gson gson;

    public GsonDeserializer() {
        this(GsonSerializer.DEFAULT_GSON);
    }

    @SuppressWarnings({"unchecked", "WeakerAccess", "unused"}) // for end-users providing a custom gson
    public GsonDeserializer(Gson gson) {
        this(gson, (Class<T>) Object.class);
    }

    private GsonDeserializer(Gson gson, Class<T> returnType) {
        Assert.notNull(gson, "gson cannot be null.");
        Assert.notNull(returnType, "Return type cannot be null.");
        this.gson = gson;
        this.returnType = returnType;
    }

    @Override
    public T deserialize(byte[] bytes) throws DeserializationException {
        try {
            return readValue(bytes);
        } catch (IOException e) {
            String msg = "Unable to deserialize bytes into a " + returnType.getName() + " instance: " + e.getMessage();
            throw new DeserializationException(msg, e);
        }
    }

    protected T readValue(byte[] bytes) throws IOException {
        return gson.fromJson(new String(bytes, UTF_8), returnType);
    }
}