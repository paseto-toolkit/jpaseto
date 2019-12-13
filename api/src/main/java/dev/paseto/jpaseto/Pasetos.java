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
package dev.paseto.jpaseto;

import dev.paseto.jpaseto.lang.Services;

public final class Pasetos {

    public static PasetosV2Local v2Local() {
        return new PasetosV2Local();
    }

    public static PasetosV2Public v2Public() {
        return new PasetosV2Public();
    }

    public static PasetosV1Local v1Local() {
        return new PasetosV1Local();
    }

    public static PasetosV1Public v1Public() {
        return new PasetosV1Public();
    }

    public static PasetoParserBuilder parserBuilder() {
        return Services.loadFirst(PasetoParserBuilder.class);
    }

    public static class PasetosV2Local {
        public PasetoV2LocalBuilder builder() {
            return Services.loadFirst(PasetoV2LocalBuilder.class);
        }
    }

    public static class PasetosV2Public {
        public PasetoV2PublicBuilder builder() {
            return Services.loadFirst(PasetoV2PublicBuilder.class);
        }
    }

    public static class PasetosV1Local {
        public PasetoV1LocalBuilder builder() {
            return Services.loadFirst(PasetoV1LocalBuilder.class);
        }
    }

    public static class PasetosV1Public {
        public PasetoV1PublicBuilder builder() {
            return Services.loadFirst(PasetoV1PublicBuilder.class);
        }
    }
}
