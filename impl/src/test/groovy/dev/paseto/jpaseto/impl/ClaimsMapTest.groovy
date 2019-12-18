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
import org.testng.annotations.Test

import java.time.Instant

import static dev.paseto.jpaseto.impl.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class ClaimsMapTest {

    @Test
    void testToInstantFromNull() {
        Instant actual = ClaimsMap.toInstant(null, 'foo')
        assertThat actual, nullValue()
    }

    @Test
    void testToDateFromDate() {
        def d = Instant.now()
        Instant instant = ClaimsMap.toInstant(d, 'foo')
        assertThat instant, is(d)
    }

    @Test
    void testToDateFromCalendar() {
        def c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        def d = c.getTime().toInstant()
        Instant instant = ClaimsMap.toInstant(c, 'foo')
        assertThat instant, is(d)
    }

    @Test
    void testToDateFromIso8601String() {
        def d = new Date(2015, 1, 1, 12, 0, 0).toInstant()
        String s = DateFormats.formatIso8601(d)
        Instant instant = ClaimsMap.toInstant(s, 'foo')
        assertThat instant, is(d)
    }

    @Test
    void testToDateFromInvalidIso8601String() {
        Date d = new Date(2015, 1, 1, 12, 0, 0)
        String s = d.toString()

        def e = expect(IllegalArgumentException, { ClaimsMap.toInstant(d.toString(), 'foo') })
        assertThat e.getMessage(), is("'foo' value does not appear to be ISO-8601-formatted: $s" as String)
    }

    @Test
    void testToDateFromIso8601MillisString() {
        long millis = System.currentTimeMillis();
        def d = new Date(millis).toInstant()
        String s = DateFormats.formatIso8601(d)
        Instant instant = ClaimsMap.toInstant(s, 'foo')
        assertThat instant, is(d)
    }

    @Test
    void testToSpecDateWithNull() {
        assertThat ClaimsMap.toSpecDate(null, 'exp'), nullValue()
    }

    @Test
    void testToSpecDateWithString() {
        String s = "2039-01-01T00:00:00+00:00"
        def d = DateFormats.parseIso8601Date(s)
        Instant instant = ClaimsMap.toSpecDate(s, 'exp')
        assertThat instant, is(d)
    }

    @Test
    void testToSpecDateWithIso8601String() {
        long millis = System.currentTimeMillis();
        def d = new Date(millis).toInstant()
        String s = DateFormats.formatIso8601(d)
        Instant instant = ClaimsMap.toSpecDate(s, 'exp')
        assertThat instant, is(d)
    }

    @Test
    void testToSpecDateWithInstant() {
        long millis = System.currentTimeMillis();
        def d = new Date(millis).toInstant()
        Instant instant = ClaimsMap.toSpecDate(d, 'exp')
        assertThat instant, is(d)
    }

    @Test
    void testToSpecDateWithDate() {
        long millis = System.currentTimeMillis();
        def d = new Date(millis)
        Instant instant = ClaimsMap.toSpecDate(d, 'exp')
        assertThat instant, is(d.toInstant())
    }

    @Test
    void testToDateFromNonDateObject() {

        def value = new Object() {
            @Override
            String toString() { return 'hi' }
        }

        def e = expect IllegalStateException, { ClaimsMap.toInstant(value, 'foo') }
        assertThat e.getMessage(), is("Cannot create Date from 'foo' value 'hi'.")
    }

    @Test
    void testContainsKey() {
        def m = createClaimsMap()
        m.put('foo', 'bar')
        assertThat m, hasKey('foo')
    }

    @Test
    void testContainsValue() {
        def m = createClaimsMap()
        m.put('foo', 'bar')
        assertThat m, hasValue('bar')
    }

    @Test
    void testRemoveByPuttingNull() {
        def m = createClaimsMap()
        m.put('foo', 'bar')
        assertThat m, hasKey('foo')
        assertThat m, hasValue('bar')
        m.put('foo', null)
        assertThat m, not(hasKey('foo'))
        assertThat m, not(hasValue('bar'))
    }

    @Test
    void testPutAll() {
        def m = createClaimsMap()
        m.putAll([a: 'b', c: 'd'])
        assertThat m, aMapWithSize(2)
        assertThat m, hasEntry("a", "b")
        assertThat m, hasEntry("c", "d")
    }

    @Test
    void testPutAllWithNullArgument() {
        def m = createClaimsMap()
        m.putAll((Map)null)
        assertThat m, aMapWithSize(0)
    }

    @Test
    void testClear() {
        def m = createClaimsMap()
        m.put('foo', 'bar')
        assertThat m, aMapWithSize(1)
        m.clear()
        assertThat m, aMapWithSize(0)
    }

    @Test
    void testKeySet() {
        def m = createClaimsMap()
        m.putAll([a: 'b', c: 'd'])
        assertThat m.keySet(), is(['a', 'c'] as Set)
    }

    @Test
    void testValues() {
        def m = createClaimsMap()
        m.putAll([a: 'b', c: 'd'])
        def s = ['b', 'd']
        assertThat m.values(), contains('b', 'd')
    }

    @Test
    void testEquals() throws Exception {
        def m1 = createClaimsMap()
        m1.put("a", "a")

        def m2 = createClaimsMap()
        m2.put("a", "a")

        assertThat m1, is(m2)
    }

    @Test
    void testHashcode() throws Exception {
        def m = createClaimsMap()
        def hashCodeEmpty = m.hashCode()

        m.put("a", "b");
        def hashCodeNonEmpty = m.hashCode()
        assertThat hashCodeEmpty, not(equalTo(hashCodeNonEmpty))

        def identityHash = System.identityHashCode(m)
        assertThat hashCodeNonEmpty, not(equalTo(identityHash))
    }

    @Test
    void testGetClaimWithRequiredType_Null_Success() {
        def m = createClaimsMap()
        m.put("aNull", null)
        Object result = m.get("aNull", Integer.class)
        assertThat result, nullValue()
    }

    @Test
    void testGetClaimWithRequiredType_Exception() {
        def m = createClaimsMap()
        m.put("anInteger", new Integer(5))
        def e = expect RequiredTypeException, { m.get("anInteger", String.class) }
        assertThat e.getMessage(), is(String.format(ClaimsMap.CONVERSION_ERROR_MSG, 'class java.lang.Integer', 'class java.lang.String'))
    }

    @Test
    void testGetClaimWithRequiredType_Integer_Success() {
        def expected = new Integer(5)
        def m = createClaimsMap()
        m.put("anInteger", expected)
        Object result = m.get("anInteger", Integer.class)
        assertThat result, is(expected)
    }

    @Test
    void testGetClaimWithRequiredType_Long_Success() {
        def expected = new Long(123)
        def m = createClaimsMap()
        m.put("aLong", expected)
        Object result = m.get("aLong", Long.class)
        assertThat result, is(expected)
    }

    @Test
    void testGetClaimWithRequiredType_LongWithInteger_Success() {
        // long value that fits inside an Integer
        def expected = new Long(Integer.MAX_VALUE - 100)
        // deserialized as an Integer from JSON
        // (type information is not available during parsing)
        def m = createClaimsMap()
        m.put("smallLong", expected.intValue())
        // should still be available as Long
        Object result = m.get("smallLong", Long.class)
        assertThat result, is(expected)
    }

    @Test
    void testGetClaimWithRequiredType_ShortWithInteger_Success() {
        def expected = new Short((short) 42)
        def m = createClaimsMap()
        m.put("short", expected.intValue())
        Object result = m.get("short", Short.class)
        assertThat result, is(expected)
    }

    @Test
    void testGetClaimWithRequiredType_ShortWithBigInteger_Exception() {
        def m = createClaimsMap()
        m.put("tooBigForShort", ((int) Short.MAX_VALUE) + 42)

        def e = expect RequiredTypeException, { m.get("tooBigForShort", Short.class) }
        assertThat e.getMessage(), is(String.format(ClaimsMap.CONVERSION_ERROR_MSG, 'class java.lang.Integer', 'class java.lang.Short'))
    }

    @Test
    void testGetClaimWithRequiredType_ShortWithSmallInteger_Exception() {
        def m = createClaimsMap()
        m.put("tooSmallForShort", ((int) Short.MIN_VALUE) - 42)

        def e = expect RequiredTypeException, { m.get("tooSmallForShort", Short.class) }
        assertThat e.getMessage(), is(String.format(ClaimsMap.CONVERSION_ERROR_MSG, 'class java.lang.Integer', 'class java.lang.Short'))
    }

    @Test
    void testGetClaimWithRequiredType_ByteWithInteger_Success() {
        def expected = new Byte((byte) 42)
        def m = createClaimsMap()
        m.put("byte", expected.intValue())
        Object result = m.get("byte", Byte.class)
        assertThat result, is(expected)
    }

    @Test
    void testGetClaimWithRequiredType_ByteWithBigInteger_Exception() {
        def m = createClaimsMap()
        m.put("tooBigForByte", ((int) Byte.MAX_VALUE) + 42)

        def e = expect RequiredTypeException, { m.get("tooBigForByte", Byte.class) }
        assertThat e.getMessage(), is(String.format(ClaimsMap.CONVERSION_ERROR_MSG, 'class java.lang.Integer', 'class java.lang.Byte'))
    }

    @Test
    void testGetClaimWithRequiredType_ByteWithSmallInteger_Exception() {
        def m = createClaimsMap()
        m.put("tooSmallForByte", ((int) Byte.MIN_VALUE) - 42)

        def e = expect RequiredTypeException, { m.get("tooSmallForByte", Byte.class) }
        assertThat e.getMessage(), is(String.format(ClaimsMap.CONVERSION_ERROR_MSG, 'class java.lang.Integer', 'class java.lang.Byte'))
    }

    @Test
    void testGetClaimWithRequiredType_Date_Success() {
        def expected = new Date()
        def m = createClaimsMap()
        m.put("aDate", expected)
        Date actual = m.get("aDate", Date.class)
        assertThat actual, is(expected)
    }

    @Test
    void testGetClaimWithRequiredType_DateWithLong_Success() {
        def expected = new Date()
        // note that Long is stored in claim
        def m = createClaimsMap()
        m.put("aDate", expected.getTime())
        Date actual = m.get("aDate", Date.class)
        assertThat actual, is(expected)
    }
    
    private static ClaimsMap createClaimsMap() {
        return new ClaimsMap() {}
    }
}
