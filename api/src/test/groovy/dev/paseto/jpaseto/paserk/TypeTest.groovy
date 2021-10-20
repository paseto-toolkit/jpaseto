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
        assertThat(true, is(Type.LID.isSafeInFooter()))
        assertThat(false, is(Type.LOCAL.isSafeInFooter()))
        assertThat(true, is(Type.SEAL.isSafeInFooter()))
        assertThat(true, is(Type.LOCAL_WRAP.isSafeInFooter()))
        assertThat(true, is(Type.LOCAL_PW.isSafeInFooter()))
        assertThat(true, is(Type.SID.isSafeInFooter()))
        assertThat(false, is(Type.PUBLIC.isSafeInFooter()))
        assertThat(true, is(Type.PID.isSafeInFooter()))
        assertThat(false, is(Type.SECRET.isSafeInFooter()))
        assertThat(true, is(Type.SECRET_WRAP.isSafeInFooter()))
        assertThat(true, is(Type.SECRET_PW.isSafeInFooter()))
    }

    @Test
    void testDataEncoded() {
        assertThat(true, is(Type.LID.isDataEncoded()))
        assertThat(true, is(Type.LOCAL.isDataEncoded()))
        assertThat(true, is(Type.SEAL.isDataEncoded()))
        assertThat(false, is(Type.LOCAL_WRAP.isDataEncoded()))
        assertThat(true, is(Type.LOCAL_PW.isDataEncoded()))
        assertThat(true, is(Type.SID.isDataEncoded()))
        assertThat(true, is(Type.PUBLIC.isDataEncoded()))
        assertThat(true, is(Type.PID.isDataEncoded()))
        assertThat(true, is(Type.SECRET.isDataEncoded()))
        assertThat(false, is(Type.SECRET_WRAP.isDataEncoded()))
        assertThat(true, is(Type.SECRET_PW.isDataEncoded()))
    }

    @Test
    void testCompatibility() {
        assertThat(Purpose.LOCAL, is(Type.LID.getCompatibility()))
        assertThat(Purpose.LOCAL, is(Type.LOCAL.getCompatibility()))
        assertThat(Purpose.LOCAL, is(Type.SEAL.getCompatibility()))
        assertThat(Purpose.LOCAL, is(Type.LOCAL_WRAP.getCompatibility()))
        assertThat(Purpose.LOCAL, is(Type.LOCAL_PW.getCompatibility()))
        assertThat(Purpose.PUBLIC, is(Type.SID.getCompatibility()))
        assertThat(Purpose.PUBLIC, is(Type.PUBLIC.getCompatibility()))
        assertThat(Purpose.PUBLIC, is(Type.PID.getCompatibility()))
        assertThat(Purpose.PUBLIC, is(Type.SECRET.getCompatibility()))
        assertThat(Purpose.PUBLIC, is(Type.SECRET_WRAP.getCompatibility()))
        assertThat(Purpose.PUBLIC, is(Type.SECRET_PW.getCompatibility()))
    }

    @Test
    void testToStringReturnsType() {
        assertThat("lid", is(Type.LID.toString()))
        assertThat("local", is(Type.LOCAL.toString()))
        assertThat("seal", is(Type.SEAL.toString()))
        assertThat("local-wrap", is(Type.LOCAL_WRAP.toString()))
        assertThat("local-pw", is(Type.LOCAL_PW.toString()))
        assertThat("sid", is(Type.SID.toString()))
        assertThat("public", is(Type.PUBLIC.toString()))
        assertThat("pid", is(Type.PID.toString()))
        assertThat("secret", is(Type.SECRET.toString()))
        assertThat("secret-wrap", is(Type.SECRET_WRAP.toString()))
        assertThat("secret-pw", is(Type.SECRET_PW.toString()))
    }

    @Test
    void testOfMethod() {
        assertThat(false, is(Type.of("foo").isPresent()))
        assertThat(true, is(Type.of("lid").isPresent()))
        assertThat(true, is(Type.of("lid").get() == Type.LID))
        assertThat(true, is(Type.of("local").isPresent()))
        assertThat(true, is(Type.of("local").get() == Type.LOCAL))
        assertThat(true, is(Type.of("seal").isPresent()))
        assertThat(true, is(Type.of("seal").get() == Type.SEAL))
        assertThat(true, is(Type.of("local-wrap").isPresent()))
        assertThat(true, is(Type.of("local-wrap").get() == Type.LOCAL_WRAP))
        assertThat(true, is(Type.of("local-pw").isPresent()))
        assertThat(true, is(Type.of("local-pw").get() == Type.LOCAL_PW))
        assertThat(true, is(Type.of("sid").isPresent()))
        assertThat(true, is(Type.of("sid").get() == Type.SID))
        assertThat(true, is(Type.of("public").isPresent()))
        assertThat(true, is(Type.of("public").get() == Type.PUBLIC))
        assertThat(true, is(Type.of("pid").isPresent()))
        assertThat(true, is(Type.of("pid").get() == Type.PID))
        assertThat(true, is(Type.of("secret").isPresent()))
        assertThat(true, is(Type.of("secret").get() == Type.SECRET))
        assertThat(true, is(Type.of("secret-wrap").isPresent()))
        assertThat(true, is(Type.of("secret-wrap").get() == Type.SECRET_WRAP))
        assertThat(true, is(Type.of("secret-pw").isPresent()))
        assertThat(true, is(Type.of("secret-pw").get() == Type.SECRET_PW))
    }

    @Test
    void testFromMethod() {
        expect UnsupportedPasetoException, { Type.from("foo") }
        assertThat(true, is(Type.from("lid") == Type.LID))
        assertThat(true, is(Type.from("local") == Type.LOCAL))
        assertThat(true, is(Type.from("seal") == Type.SEAL))
        assertThat(true, is(Type.from("local-wrap") == Type.LOCAL_WRAP))
        assertThat(true, is(Type.from("local-pw") == Type.LOCAL_PW))
        assertThat(true, is(Type.from("sid") == Type.SID))
        assertThat(true, is(Type.from("public") == Type.PUBLIC))
        assertThat(true, is(Type.from("pid") == Type.PID))
        assertThat(true, is(Type.from("secret") == Type.SECRET))
        assertThat(true, is(Type.from("secret-wrap") == Type.SECRET_WRAP))
        assertThat(true, is(Type.from("secret-pw") == Type.SECRET_PW))
    }
}