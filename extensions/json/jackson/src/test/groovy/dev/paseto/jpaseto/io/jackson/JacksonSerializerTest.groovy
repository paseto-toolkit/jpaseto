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
package dev.paseto.jpaseto.io.jackson

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import dev.paseto.jpaseto.io.SerializationException
import dev.paseto.jpaseto.lang.DateFormats
import org.testng.Assert
import org.testng.annotations.Test

import java.time.Instant
import java.time.format.DateTimeFormatter

import static java.nio.charset.StandardCharsets.UTF_8
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class JacksonSerializerTest {

    @Test
    void testDefaultConstructor() {
        def serializer = new JacksonSerializer()
        assertThat serializer.objectMapper, notNullValue()
    }

    @Test
    void testObjectMapperConstructor() {
        def customOM = new ObjectMapper()
        def serializer = new JacksonSerializer<>(customOM)
        assertThat customOM, equalTo(serializer.objectMapper)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testObjectMapperConstructorWithNullArgument() {
        new JacksonSerializer<>(null)
    }

    @Test
    void testByte() {
        byte[] expected = "120".getBytes(UTF_8) //ascii("x") = 120
        byte[] bytes = "x".getBytes(UTF_8)
        byte[] result = new JacksonSerializer().serialize(bytes[0]) //single byte
        assertThat result, is(expected)
    }

    @Test
    void testByteArray() { //expect Base64 string by default:
        byte[] bytes = "hi".getBytes(UTF_8)
        String expected = '"aGk="' as String //base64(hi) --> aGk=
        byte[] result = new JacksonSerializer().serialize(bytes)
        assertThat new String(result, UTF_8), is(expected)
    }

    @Test
    void testEmptyByteArray() { //expect Base64 string by default:
        byte[] bytes = new byte[0]
        byte[] result = new JacksonSerializer().serialize(bytes)
        assertThat new String(result, UTF_8), is('""')
    }

    @Test
    void testChar() { //expect Base64 string by default:
        byte[] result = new JacksonSerializer().serialize('h' as char)
        assertThat new String(result, UTF_8), is("\"h\"")
    }

    @Test
    void testCharArray() { //expect Base64 string by default:
        byte[] result = new JacksonSerializer().serialize("hi".toCharArray())
        assertThat new String(result, UTF_8), is("\"hi\"")
    }

    @Test
    void testSerialize() {
        byte[] expected = '{"hello":"世界"}'.getBytes(UTF_8)
        byte[] result = new JacksonSerializer().serialize([hello: '世界'])
        assertThat result, is(expected)
    }

    @Test
    void testDateTime() {
        DateTimeFormatter dateTimeFormatter = DateFormats.ISO_OFFSET_DATE_TIME
        def anInstantString = "2019-01-01T00:00:00+00:00"
        def dateTime = Instant.from(dateTimeFormatter.parse(anInstantString))

        def expected = '{"hello":"' + anInstantString +  '"}'
        def result = new String(new JacksonSerializer().serialize([hello: dateTime]), UTF_8)
        assertThat result, is(expected)
    }

    @Test
    void testSerializeFailsWithJsonProcessingException() {

        def ex = mock(JsonProcessingException)
        when(ex.getMessage()).thenReturn('foo')

        def serializer = new JacksonSerializer() {
            @Override
            protected byte[] writeValueAsBytes(Object o) throws JsonProcessingException {
                throw ex
            }
        }

        try {
            serializer.serialize([hello: 'world'])
            Assert.fail("Expected SerializationException")
        } catch (SerializationException se) {
            assertThat se.getMessage(), is('Unable to serialize object: foo')
            assertThat se.getCause(), sameInstance(ex)
        }
    }
}
