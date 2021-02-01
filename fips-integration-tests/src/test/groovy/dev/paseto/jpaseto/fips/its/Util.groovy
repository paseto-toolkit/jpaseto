/*
 * Copyright 2019-Present paseto.dev, Inc.
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
package dev.paseto.jpaseto.fips.its

import dev.paseto.jpaseto.Paseto
import dev.paseto.jpaseto.Purpose
import dev.paseto.jpaseto.Version
import dev.paseto.jpaseto.impl.DefaultClaims
import dev.paseto.jpaseto.impl.DefaultFooterClaims
import dev.paseto.jpaseto.impl.DefaultPaseto
import org.testng.Assert

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class Util {
    static Paseto v1PublicFromClaims(Map<String, Object> claims, Map<String, Object> footerClaims) {
        return fromClaims(Version.V1, Purpose.PUBLIC, claims, footerClaims)
    }

    static Paseto fromClaims(Version version, Purpose purpose, Map<String, Object> claims, Map<String, Object> footerClaims) {
        new DefaultPaseto(version, purpose, new DefaultClaims(claims), new DefaultFooterClaims(footerClaims))
    }

    static Clock clockForVectors() {
        return Clock.fixed(Instant.ofEpochMilli(1544490000000), ZoneOffset.UTC) // December 11, 2018 01:00:00
    }

    static <T extends Throwable> T expect(Class<T> catchMe, Closure closure) {
        try {
            closure.call()
            Assert.fail("Expected ${catchMe.getName()} to be thrown.")
        } catch(e) {
            if (!e.class.isAssignableFrom(catchMe)) {
                throw e
            }
            return e
        }
    }
}
