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

/**
 * Factory class useful for creating instances of Paseto interfaces.
 * @since 0.1
 */
public final class Pasetos {

    private Pasetos() {}

    public static PasetoParserBuilder parserBuilder() {
        return Services.loadFirst(PasetoParserBuilder.class);
    }

    public static final class V2Local {
        private V2Local() {}

        public PasetoV2LocalBuilder builder() {
            return Services.loadFirst(PasetoV2LocalBuilder.class);
        }
    }

    public static final class V2Public {
        private V2Public() {}

        public PasetoV2PublicBuilder builder() {
            return Services.loadFirst(PasetoV2PublicBuilder.class);
        }
    }

    public static final class V1Local {
        private V1Local() {}

        public PasetoV1LocalBuilder builder() {
            return Services.loadFirst(PasetoV1LocalBuilder.class);
        }
    }

    public static final class V1Public {
        private V1Public() {}

        public PasetoV1PublicBuilder builder() {
            return Services.loadFirst(PasetoV1PublicBuilder.class);
        }
    }

    public static final class V1 {
        public static final V1Public PUBLIC = new V1Public();
        public static final V1Local LOCAL = new V1Local();

        private V1() {}
    }

    public static final class V2 {
        public static final V2Public PUBLIC = new V2Public();
        public static final V2Local LOCAL = new V2Local();

        private V2() {}
    }
}
