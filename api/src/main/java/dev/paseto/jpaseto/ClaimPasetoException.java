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
package dev.paseto.jpaseto;

/**
 * ClaimPasetoException is a subclass of the {@link PasetoException} that is thrown after a validation of an paseto claim failed.
 *
 * @since 0.1
 */
public abstract class ClaimPasetoException extends PasetoException {

    public static final String INCORRECT_EXPECTED_CLAIM_MESSAGE_TEMPLATE = "Expected '%s' claim to be %s, but was: '%s'.";
    public static final String MISSING_EXPECTED_CLAIM_MESSAGE_TEMPLATE = "Expected '%s' claim to be %s, but was not present in the paseto claims.";

    private final Paseto paseto;

    ClaimPasetoException(Paseto paseto, String message) {
        super(message);
        this.paseto = paseto;
    }

    ClaimPasetoException(Paseto paseto, String message, Throwable cause) {
        super(message, cause);
        this.paseto = paseto;
    }

    public Paseto getPaseto() {
        return paseto;
    }
}
