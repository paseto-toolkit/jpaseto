/*
 * Copyright (C) 2015 jsonwebtoken.io
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
package dev.paseto.jpaseto.impl

import dev.paseto.jpaseto.RequiredTypeException
import dev.paseto.jpaseto.lang.DateFormats
import org.hamcrest.MatcherAssert
import org.testng.annotations.Test

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class DateFormatsTest {
    @Test
    void testParseIso8601String() {
        def d = ZonedDateTime.of(2015, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant()
        String s = "2015-01-01T12:00:00+00:00"
        Instant instant = DateFormats.parseIso8601Date(s)
        assertThat instant, is(d)
    }

    @Test
    void testParseIso8601ZString() {
        def d = ZonedDateTime.of(2015, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant()
        String s = "2015-01-01T12:00:00Z"
        Instant instant = DateFormats.parseIso8601Date(s)
        assertThat instant, is(d)
    }

    @Test
    void testFormatDateWithIso8601String() {
        def d = ZonedDateTime.of(2015, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")).toInstant()
        String exp = "2015-01-01T12:00:00Z"
        String s = DateFormats.formatIso8601(d)
        assertThat s, is(exp)
    }
}
