/*
 * Copyright 2019-Present paseto.dev
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
package dev.paseto.jpaseto.impl;

import dev.paseto.jpaseto.Paseto;
import dev.paseto.jpaseto.FooterClaims;
import dev.paseto.jpaseto.Claims;
import dev.paseto.jpaseto.Purpose;
import dev.paseto.jpaseto.Version;

import java.util.Objects;

class DefaultPaseto implements Paseto {

    private final Version version;
    private final Purpose purpose;
    private final Claims payload;
    private final FooterClaims footer;

    DefaultPaseto(Version version, Purpose purpose, Claims payload, FooterClaims footer) {
        this.version = version;
        this.purpose = purpose;
        this.payload = payload;
        this.footer = footer;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public Purpose getPurpose() {
        return purpose;
    }

    @Override
    public Claims getClaims() {
        return payload;
    }

    @Override
    public FooterClaims getFooter() {
        return footer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPaseto that = (DefaultPaseto) o;
        return version == that.version &&
                purpose == that.purpose &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(footer, that.footer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, purpose, payload, footer);
    }
}
