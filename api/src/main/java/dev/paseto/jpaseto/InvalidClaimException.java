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
package dev.paseto.jpaseto;

/**
 * Exception indicating a parsed claim is invalid in some way.  Subclasses reflect the specific
 * reason the claim is invalid.
 *
 * @see IncorrectClaimException
 * @see MissingClaimException
 *
 * @since 0.1
 */
public class InvalidClaimException extends ClaimPasetoException {

    private String claimName;
    private Object claimDescription;

    protected InvalidClaimException(Paseto paseto, String message) {
        super(paseto, message);
    }

    protected InvalidClaimException(Paseto paseto, String message, Throwable cause) {
        super(paseto, message, cause);
    }

    public String getClaimName() {
        return claimName;
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public Object getClaimDescription() {
        return claimDescription;
    }

    public void setClaimDescription(Object claimDescription) {
        this.claimDescription = claimDescription;
    }
}
