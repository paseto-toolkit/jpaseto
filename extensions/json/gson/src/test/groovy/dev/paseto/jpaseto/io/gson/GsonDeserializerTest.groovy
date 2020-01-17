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
package dev.paseto.jpaseto.io.gson

import com.google.gson.Gson
import dev.paseto.jpaseto.io.DeserializationException
import org.testng.Assert
import org.testng.annotations.Test

import static java.nio.charset.StandardCharsets.UTF_8
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.*

class GsonDeserializerTest {

    @Test
    void testDefaultConstructor() {
        def deserializer = new GsonDeserializer()
        assertThat deserializer.gson, notNullValue()
    }

    @Test
    void testObjectMapperConstructor() {
        def customGSON = new Gson()
        def deserializer = new GsonDeserializer(customGSON)
        assertThat deserializer.gson, sameInstance(customGSON)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testObjectMapperConstructorWithNullArgument() {
        new GsonDeserializer<>(null)
    }

    @Test
    void testDeserialize() {
        byte[] serialized = '{"hello":"世界"}'.getBytes(UTF_8)
        def expected = [hello: '世界']
        def result = new GsonDeserializer().deserialize(serialized)
        assertThat result, is(expected)
    }

    @Test
    void testDeserializeFailsWithJsonProcessingException() {

        def ex = mock(IOException)

        when(ex.getMessage()).thenReturn('foo')

        def deserializer = new GsonDeserializer() {
            @Override
            protected Object readValue(byte[] bytes) throws IOException {
                throw ex
            }
        }

        try {
            deserializer.deserialize('{"hello":"世界"}'.getBytes(UTF_8))
            Assert.fail("Expected DeserializationException to be thrown")
        } catch (DeserializationException se) {
            assertThat se.getMessage(), is('Unable to deserialize bytes into a java.lang.Object instance: foo')
            assertThat se.getCause(), is(ex)
        }
    }
}
