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
import static org.testng.AssertJUnit.*

class TypeTest {

    @Test
    void testSafeInFooter() {
        assertEquals(true, Type.LID.isSafeInFooter())
        assertEquals(false, Type.LOCAL.isSafeInFooter())
        assertEquals(true, Type.SEAL.isSafeInFooter())
        assertEquals(true, Type.LOCAL_WRAP.isSafeInFooter())
        assertEquals(true, Type.LOCAL_PW.isSafeInFooter())
        assertEquals(true, Type.SID.isSafeInFooter())
        assertEquals(false, Type.PUBLIC.isSafeInFooter())
        assertEquals(true, Type.PID.isSafeInFooter())
        assertEquals(false, Type.SECRET.isSafeInFooter())
        assertEquals(true, Type.SECRET_WRAP.isSafeInFooter())
        assertEquals(true, Type.SECRET_PW.isSafeInFooter())
    }

    @Test
    void testDataEncoded() {
        assertEquals(true, Type.LID.isDataEncoded())
        assertEquals(true, Type.LOCAL.isDataEncoded())
        assertEquals(true, Type.SEAL.isDataEncoded())
        assertEquals(false, Type.LOCAL_WRAP.isDataEncoded())
        assertEquals(true, Type.LOCAL_PW.isDataEncoded())
        assertEquals(true, Type.SID.isDataEncoded())
        assertEquals(true, Type.PUBLIC.isDataEncoded())
        assertEquals(true, Type.PID.isDataEncoded())
        assertEquals(true, Type.SECRET.isDataEncoded())
        assertEquals(false, Type.SECRET_WRAP.isDataEncoded())
        assertEquals(true, Type.SECRET_PW.isDataEncoded())
    }

    @Test
    void testCompatibility() {
        assertEquals(Purpose.LOCAL, Type.LID.getCompatibility())
        assertEquals(Purpose.LOCAL, Type.LOCAL.getCompatibility())
        assertEquals(Purpose.LOCAL, Type.SEAL.getCompatibility())
        assertEquals(Purpose.LOCAL, Type.LOCAL_WRAP.getCompatibility())
        assertEquals(Purpose.LOCAL, Type.LOCAL_PW.getCompatibility())
        assertEquals(Purpose.PUBLIC, Type.SID.getCompatibility())
        assertEquals(Purpose.PUBLIC, Type.PUBLIC.getCompatibility())
        assertEquals(Purpose.PUBLIC, Type.PID.getCompatibility())
        assertEquals(Purpose.PUBLIC, Type.SECRET.getCompatibility())
        assertEquals(Purpose.PUBLIC, Type.SECRET_WRAP.getCompatibility())
        assertEquals(Purpose.PUBLIC, Type.SECRET_PW.getCompatibility())
    }

    @Test
    void testToStringReturnsType() {
        assertEquals("lid", Type.LID.toString())
        assertEquals("local", Type.LOCAL.toString())
        assertEquals("seal", Type.SEAL.toString())
        assertEquals("local-wrap", Type.LOCAL_WRAP.toString())
        assertEquals("local-pw", Type.LOCAL_PW.toString())
        assertEquals("sid", Type.SID.toString())
        assertEquals("public", Type.PUBLIC.toString())
        assertEquals("pid", Type.PID.toString())
        assertEquals("secret", Type.SECRET.toString())
        assertEquals("secret-wrap", Type.SECRET_WRAP.toString())
        assertEquals("secret-pw", Type.SECRET_PW.toString())
    }

    @Test
    void testOfMethod() {
        assertFalse Type.of("foo").isPresent()
        assertTrue Type.of("lid").isPresent()
        assertTrue Type.of("lid").get() == Type.LID
        assertTrue Type.of("local").isPresent()
        assertTrue Type.of("local").get() == Type.LOCAL
        assertTrue Type.of("seal").isPresent()
        assertTrue Type.of("seal").get() == Type.SEAL
        assertTrue Type.of("local-wrap").isPresent()
        assertTrue Type.of("local-wrap").get() == Type.LOCAL_WRAP
        assertTrue Type.of("local-pw").isPresent()
        assertTrue Type.of("local-pw").get() == Type.LOCAL_PW
        assertTrue Type.of("sid").isPresent()
        assertTrue Type.of("sid").get() == Type.SID
        assertTrue Type.of("public").isPresent()
        assertTrue Type.of("public").get() == Type.PUBLIC
        assertTrue Type.of("pid").isPresent()
        assertTrue Type.of("pid").get() == Type.PID
        assertTrue Type.of("secret").isPresent()
        assertTrue Type.of("secret").get() == Type.SECRET
        assertTrue Type.of("secret-wrap").isPresent()
        assertTrue Type.of("secret-wrap").get() == Type.SECRET_WRAP
        assertTrue Type.of("secret-pw").isPresent()
        assertTrue Type.of("secret-pw").get() == Type.SECRET_PW
    }

    @Test
    void testFromMethod() {
        boolean threwException = false
        try {
            Type.from("foo")
        } catch(UnsupportedPasetoException e) {
            threwException = true
        }
        assertTrue threwException
        assertTrue Type.from("lid") == Type.LID
        assertTrue Type.from("local") == Type.LOCAL
        assertTrue Type.from("seal") == Type.SEAL
        assertTrue Type.from("local-wrap") == Type.LOCAL_WRAP
        assertTrue Type.from("local-pw") == Type.LOCAL_PW
        assertTrue Type.from("sid") == Type.SID
        assertTrue Type.from("public") == Type.PUBLIC
        assertTrue Type.from("pid") == Type.PID
        assertTrue Type.from("secret") == Type.SECRET
        assertTrue Type.from("secret-wrap") == Type.SECRET_WRAP
        assertTrue Type.from("secret-pw") == Type.SECRET_PW
    }
}