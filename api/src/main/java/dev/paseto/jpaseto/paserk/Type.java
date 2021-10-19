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
package dev.paseto.jpaseto.paserk;

import dev.paseto.jpaseto.Purpose;
import dev.paseto.jpaseto.UnsupportedPasetoException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Representation of a Paserk type.
 * @see <a href="https://github.com/paseto-standard/paserk#paserk">Paserk</a>
 * @author Sergio del Amo
 * @since 0.8.0
 */
public enum Type {
    LID("lid", "Unique Identifier for a separate PASERK for local PASETOs.", Purpose.LOCAL, true, true),
    LOCAL("local", "Symmetric key for local tokens.", Purpose.LOCAL, true, false),
    SEAL("seal", "Symmetric key wrapped using asymmetric encryption.", Purpose.LOCAL	,true	,true),
    LOCAL_WRAP("local-wrap", "Symmetric key wrapped by another symmetric key.", Purpose.LOCAL	, false	,true),
    LOCAL_PW("local-pw", "Symmetric key wrapped using password-based encryption.", Purpose.LOCAL	,true	,true),
    SID("sid", "Unique Identifier for a separate PASERK for public PASETOs. (Secret Key)", Purpose.PUBLIC	,true	,true),
    PUBLIC("public", "Public key for verifying public tokens.", Purpose.PUBLIC	,true	, false),
    PID("pid", "Unique Identifier for a separate PASERK for public PASETOs. (Public Key)", Purpose.PUBLIC	,true	,true),
    SECRET("secret", "Secret key for signing public tokens.", Purpose.PUBLIC,true	, false),
    SECRET_WRAP("secret-wrap", "Asymmetric secret key wrapped by another symmetric key.",	Purpose.PUBLIC	, false	,true),
    SECRET_PW("secret-pw", "Asymmetric secret key wrapped using password-based encryption.",	Purpose.PUBLIC	,true	,true);

    private final String type;
    private final String meaning;
    private final Purpose compatibility;
    private final boolean dataEncoded;
    private final boolean safeInFooter;

    Type(String type,
         String meaning,
         Purpose compatibility,
         boolean dataEncoded,
         boolean safeInFooter) {
        this.type = type;
        this.meaning = meaning;
        this.compatibility = compatibility;
        this.dataEncoded = dataEncoded;
        this.safeInFooter = safeInFooter;
    }

    private static final Map<String, Type> typeMap = new HashMap<>();

    static {
        for (Type type : EnumSet.allOf(Type.class)) {
            typeMap.put(type.toString(), type);
        }
    }

    public static Type from(String name) {
        return of(name)
                .orElseThrow(() -> new UnsupportedPasetoException("Could not parse Type from name: " + name));
    }

    public static Optional<Type> of(String name) {
        return Optional.ofNullable(typeMap.get(name));
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }

    public String getMeaning() {
        return meaning;
    }

    public Purpose getCompatibility() {
        return compatibility;
    }

    public boolean isDataEncoded() {
        return dataEncoded;
    }

    public boolean isSafeInFooter() {
        return safeInFooter;
    }
}
