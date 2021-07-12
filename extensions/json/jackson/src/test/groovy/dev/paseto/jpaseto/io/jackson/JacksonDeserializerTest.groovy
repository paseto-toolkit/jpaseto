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

import com.fasterxml.jackson.databind.ObjectMapper
import dev.paseto.jpaseto.io.DeserializationException
import dev.paseto.jpaseto.io.jackson.stubs.CustomBean
import org.testng.Assert
import org.testng.annotations.Test

import static java.nio.charset.StandardCharsets.UTF_8
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class JacksonDeserializerTest {

    @Test
    void testDefaultConstructor() {
        def deserializer = new JacksonDeserializer()
        assertThat deserializer.objectReader, notNullValue()
    }

    @Test
    void testObjectMapperConstructor() {
        def customOM = new ObjectMapper()
        def deserializer = new JacksonDeserializer(customOM)
        assertThat customOM.reader().config, equalTo(deserializer.objectReader.config)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testObjectMapperConstructorWithNullArgument() {
        new JacksonDeserializer<>((ObjectMapper) null)
    }

    @Test
    void testDeserialize() {
        byte[] serialized = '{"hello":"世界"}'.getBytes(UTF_8)
        def expected = [hello: '世界']
        def result = new JacksonDeserializer().deserialize(serialized)
        assertThat result, is(expected)
    }

    @Test
    void testDeserializeWithCustomObject() {

        long currentTime = System.currentTimeMillis()

        // TODO add instant
        byte[] serialized = """{
                "oneKey":"oneValue", 
                "custom": {
                    "stringValue": "s-value",
                    "intValue": "11",
                    "dateValue": ${currentTime},
                    "shortValue": 22,
                    "longValue": 33,
                    "byteValue": 15,
                    "byteArrayValue": "${base64('bytes')}",
                    "nestedValue": {
                        "stringValue": "nested-value",
                        "intValue": "111",
                        "dateValue": ${currentTime + 1},
                        "shortValue": 222,
                        "longValue": 333,
                        "byteValue": 10,
                        "byteArrayValue": "${base64('bytes2')}"
                    }
                }
            }
            """.getBytes(UTF_8)

        CustomBean expectedCustomBean = new CustomBean()
            .setByteArrayValue("bytes".getBytes("UTF-8"))
            .setByteValue(0xF as byte)
            .setDateValue(new Date(currentTime))
            .setIntValue(11)
            .setShortValue(22 as short)
            .setLongValue(33L)
            .setStringValue("s-value")
            .setNestedValue(new CustomBean()
                .setByteArrayValue("bytes2".getBytes("UTF-8"))
                .setByteValue(0xA as byte)
                .setDateValue(new Date(currentTime+1))
                .setIntValue(111)
                .setShortValue(222 as short)
                .setLongValue(333L)
                .setStringValue("nested-value")
            )

        def expected = [oneKey: "oneValue", custom: expectedCustomBean]
        def result = new JacksonDeserializer(["custom": CustomBean]).deserialize(serialized)
        assertThat result, is(expected)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullClaimTypeMap() {
        new JacksonDeserializer((Map) null)
    }

    @Test
    void testDeserializeFailsWithJsonProcessingException() {

        def ex = mock(IOException)
        when(ex.getMessage()).thenReturn('foo')

        def deserializer = new JacksonDeserializer() {
            @Override
            protected Object readValue(byte[] bytes) throws java.io.IOException {
                throw ex
            }
        }


        try {
            deserializer.deserialize('{"hello":"世界"}'.getBytes(UTF_8))
            Assert.fail("Expected DeserializationException")
        } catch (DeserializationException se) {
            assertThat se.getMessage(), is('Unable to deserialize bytes into a java.lang.Object instance: foo')
            assertThat se.getCause(), sameInstance(ex)
        }
    }

    private String base64(String input) {
        return Base64.encoder.encodeToString(input.getBytes(UTF_8))
    }
}
