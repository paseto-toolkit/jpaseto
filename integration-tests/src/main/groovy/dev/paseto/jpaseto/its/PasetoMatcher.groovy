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
package dev.paseto.jpaseto.its

import dev.paseto.jpaseto.Paseto
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class PasetoMatcher extends TypeSafeMatcher<Paseto> {

    private final Paseto expected

    PasetoMatcher(Paseto expected) {
        this.expected = expected
    }

    @Override
    protected boolean matchesSafely(Paseto actual) {
        return expected.equals(actual)
    }

    @Override
    void describeTo(Description description) {
        description.appendText("token with:")
                .appendText("\n\t\tversion: ").appendValue(expected.version)
                .appendText("\n\t\tpurpose: ").appendValue(expected.purpose)
                .appendText("\n\t\tpayload: ").appendValue(expected.claims?.entrySet())
                .appendText("\n\t\tfooter:  ")
                .appendText("\n\t\t\tvalue:  ").appendValue(expected.footer?.value())
                .appendText("\n\t\t\tclaims: ").appendValue(expected.footer?.entrySet())
    }

    @Override
    protected void describeMismatchSafely(Paseto item, Description mismatchDescription) {
        mismatchDescription.appendText( "was token with:")
        mismatchDescription.appendText("\n\t\tversion: ").appendValue(item.version)
        if (expected.version != item.version) {
            mismatchDescription.appendText(" << does not match")
        }
        mismatchDescription.appendText("\n\t\tpurpose: ").appendValue(item.purpose)
        if (expected.purpose != item.purpose) {
            mismatchDescription.appendText(" << does not match")
        }
        mismatchDescription.appendText("\n\t\tpayload: ").appendValue(item.claims?.entrySet())
        if (expected.claims != item.claims) {
            mismatchDescription.appendText(" << does not match")
        }
        mismatchDescription.appendText("\n\t\tfooter:  ")
        if (expected.footer != item.footer) {
            mismatchDescription.appendText(" << does not match")
        }

        if (item.footer != null) {
            mismatchDescription.appendText("\n\t\t\tvalue:  ").appendValue(item.footer.value())
            if (expected.footer == null || item.footer.value() != expected.footer.value()) {
                mismatchDescription.appendText(" << does not match")
            }
            mismatchDescription.appendText("\n\t\t\tclaims: ").appendValue(item.footer.entrySet())
            if (expected.footer == null || item.footer.claims != expected.footer.claims) {
                mismatchDescription.appendText(" << does not match")
            }
        }
    }

    static PasetoMatcher paseto(Paseto paseto) {
        return new PasetoMatcher(paseto)
    }
}
