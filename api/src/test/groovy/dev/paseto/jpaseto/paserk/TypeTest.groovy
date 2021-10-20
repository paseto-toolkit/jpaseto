/*
 * Copyright (C) 2021-Present paseto.dev
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
package dev.paseto.jpaseto.paserk

import dev.paseto.jpaseto.Purpose
import dev.paseto.jpaseto.UnsupportedPasetoException
import org.testng.annotations.Test
import static dev.paseto.jpaseto.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class TypeTest {

    @Test
    void testSafeInFooter() {
        assertSafeInFooter Type.LID
        assertNotSafeInFooter Type.LOCAL
        assertSafeInFooter Type.SEAL
        assertSafeInFooter Type.LOCAL_WRAP
        assertSafeInFooter Type.LOCAL_PW
        assertSafeInFooter Type.SID
        assertNotSafeInFooter Type.PUBLIC
        assertSafeInFooter Type.PID
        assertNotSafeInFooter Type.SECRET
        assertSafeInFooter Type.SECRET_WRAP
        assertSafeInFooter Type.SECRET_PW
    }

    @Test
    void testDataEncoded() {
        assertEncoded Type.LID
        assertEncoded Type.LOCAL
        assertEncoded Type.SEAL
        assertNotEncoded Type.LOCAL_WRAP
        assertEncoded Type.LOCAL_PW
        assertEncoded Type.SID
        assertEncoded Type.PUBLIC
        assertEncoded Type.PID
        assertEncoded Type.SECRET
        assertNotEncoded Type.SECRET_WRAP
        assertEncoded Type.SECRET_PW
    }

    @Test
    void testCompatibility() {
        assertLocal Type.LID
        assertLocal Type.LOCAL
        assertLocal Type.SEAL
        assertLocal Type.LOCAL_WRAP
        assertLocal Type.LOCAL_PW
        assertPublic Type.SID
        assertPublic Type.PUBLIC
        assertPublic Type.PID
        assertPublic Type.SECRET
        assertPublic Type.SECRET_WRAP
        assertPublic Type.SECRET_PW
    }

    @Test
    void testToStringReturnsType() {
        assertThat(Type.LID.toString(), is("lid"))
        assertThat(Type.LOCAL.toString(), is("local"))
        assertThat(Type.SEAL.toString(), is("seal"))
        assertThat(Type.LOCAL_WRAP.toString(), is("local-wrap"))
        assertThat(Type.LOCAL_PW.toString(), is("local-pw"))
        assertThat(Type.SID.toString(), is("sid"))
        assertThat(Type.PUBLIC.toString(), is("public"))
        assertThat(Type.PID.toString(), is("pid"))
        assertThat(Type.SECRET.toString(), is("secret"))
        assertThat(Type.SECRET_WRAP.toString(), is("secret-wrap"))
        assertThat(Type.SECRET_PW.toString(), is("secret-pw"))
    }

    @Test
    void testOfMethod() {
        assertNotPresent("foo")
        assertOf("lid", Type.LID)
        assertOf("local", Type.LOCAL)
        assertOf("seal", Type.SEAL)
        assertOf("local-wrap", Type.LOCAL_WRAP)
        assertOf("local-pw", Type.LOCAL_PW)
        assertOf("sid", Type.SID)
        assertOf("public", Type.PUBLIC)
        assertOf("pid", Type.PID)
        assertOf("secret", Type.SECRET)
        assertOf("secret-wrap", Type.SECRET_WRAP)
        assertOf("secret-pw", Type.SECRET_PW)
    }

    @Test
    void testFromMethod() {
        expect UnsupportedPasetoException, { Type.from("foo") }
        assertThat(Type.from("lid"), is(Type.LID))
        assertThat(Type.from("local"), is(Type.LOCAL))
        assertThat(Type.from("seal"), is(Type.SEAL))
        assertThat(Type.from("local-wrap"), is(Type.LOCAL_WRAP))
        assertThat(Type.from("local-pw"), is(Type.LOCAL_PW))
        assertThat(Type.from("sid"), is(Type.SID))
        assertThat(Type.from("public"), is(Type.PUBLIC))
        assertThat(Type.from("pid"), is(Type.PID))
        assertThat(Type.from("secret"), is(Type.SECRET))
        assertThat(Type.from("secret-wrap"), is(Type.SECRET_WRAP))
        assertThat(Type.from("secret-pw"), is(Type.SECRET_PW))
    }

    static void assertSafeInFooter(Type type) {
        assertThat("${type.name()}.safeInFooter", type.isSafeInFooter(), is(true))
    }

    static void assertNotSafeInFooter(Type type) {
        assertThat("${type.name()}.safeInFooter", type.isSafeInFooter(), is(false))
    }

    static void assertEncoded(Type type) {
        assertThat("${type.name()}.dataEncoded", type.isDataEncoded(), is(true))
    }

    static void assertNotEncoded(Type type) {
        assertThat("${type.name()}.dataEncoded", type.isDataEncoded(), is(false))
    }

    static void assertPublic(Type type) {
        assertThat("${type.name()}.compatibility", type.getCompatibility(), is(Purpose.PUBLIC))
    }

    static void assertLocal(Type type) {
        assertThat("${type.name()}.compatibility", type.getCompatibility(), is(Purpose.LOCAL))
    }

    static void assertNotPresent(String key) {
        assertThat("Type.of(${key}).isPresent()", Type.of(key).isPresent(), is(false))
    }

    static void assertOf(String key, Type type) {
        assertThat("Type.of(${key}).isPresent()", Type.of(key).isPresent(), is(true))
        assertThat("Type.of(${key}).get()", Type.of(key).get(), is(type))
    }
}