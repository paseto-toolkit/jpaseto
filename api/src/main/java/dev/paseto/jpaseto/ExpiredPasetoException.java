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
 * Exception indicating that a Paseto was accepted after it expired and must be rejected.
 *
 * @since 0.1
 */
public class ExpiredPasetoException extends ClaimPasetoException {

    public ExpiredPasetoException(Paseto paseto, String message) {
        super(paseto, message);
    }

    /**
     * @param paseto token
     * @param message exception message
     * @param cause cause
     */
    public ExpiredPasetoException(Paseto paseto, String message, Throwable cause) {
        super(paseto, message, cause);
    }
}
