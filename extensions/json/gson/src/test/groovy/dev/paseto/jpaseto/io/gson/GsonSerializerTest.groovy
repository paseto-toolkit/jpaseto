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
import dev.paseto.jpaseto.io.SerializationException
import org.testng.Assert
import org.testng.annotations.Test

import static java.nio.charset.StandardCharsets.UTF_8
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.sameInstance
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class GsonSerializerTest {

    @Test
    void testDefaultConstructor() {
        def serializer = new GsonSerializer()
        assertThat serializer.gson, notNullValue()
    }

    @Test
    void testObjectMapperConstructor() {
        def customGSON = new Gson()
        def serializer = new GsonSerializer<>(customGSON)
        assertThat serializer.gson, sameInstance(customGSON)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testObjectMapperConstructorWithNullArgument() {
        new GsonSerializer<>(null)
    }

    @Test
    void testByte() {
        byte[] expected = "120".getBytes(UTF_8) //ascii("x") = 120
        byte[] bytes = "x".getBytes(UTF_8)
        byte[] result = new GsonSerializer().serialize(bytes[0]) //single byte
        assertThat result, is(expected)
    }

    @Test
    void testByteArray() { //expect Base64 string by default:
        byte[] bytes = "hi".getBytes(UTF_8)
        String expected = '"aGk="' as String //base64(hi) --> aGk=
        byte[] result = new GsonSerializer().serialize(bytes)
        assertThat new String(result, UTF_8), is(expected)
    }

    @Test
    void testEmptyByteArray() { //expect Base64 string by default:
        byte[] bytes = new byte[0]
        byte[] result = new GsonSerializer().serialize(bytes)
        assertThat new String(result, UTF_8), is('""')
    }

    @Test
    void testChar() { //expect Base64 string by default:
        byte[] result = new GsonSerializer().serialize('h' as char)
        assertThat new String(result, UTF_8), is("\"h\"")
    }

    @Test
    void testCharArray() { //expect Base64 string by default:
        byte[] result = new GsonSerializer().serialize("hi".toCharArray())
        assertThat new String(result, UTF_8), is("\"hi\"")
    }

    @Test
    void testSerialize() {
        byte[] expected = '{"hello":"世界"}'.getBytes(UTF_8)
        byte[] result = new GsonSerializer().serialize([hello: '世界'])
        assertThat result, is(expected)
    }

    @Test
    void testSerializeFailsWithJsonProcessingException() {

        def ex = mock(SerializationException)
        when(ex.getMessage()).thenReturn('foo')

        def serializer = new GsonSerializer() {
            @Override
            protected byte[] writeValueAsBytes(Object o) throws SerializationException {
                throw ex
            }
        }

        try {
            serializer.serialize([hello: 'world'])
            Assert.fail("Expected SerializationException to be thrown")
        } catch (SerializationException se) {
            assertThat se.getMessage(), is('Unable to serialize object: foo')
            assertThat se.getCause(), is(ex)
        }
    }
}