/*
 * Copyright (C) 2019 jsonwebtoken.io
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
package dev.paseto.jpaseto.lang

import dev.paseto.jpaseto.stub.DefaultStubService
import dev.paseto.jpaseto.stub.StubService
import org.testng.annotations.Test

import static dev.paseto.jpaseto.Util.expect
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class ServicesTest {

    @Test
    void testSuccessfulLoading() {
        def factory = Services.loadFirst(StubService)
        assertThat factory, notNullValue()
        assertThat factory, instanceOf(DefaultStubService)
    }

    @Test
    void testDefaultImplementation() {
        def defaultValue = new Object()
        def result = Services.loadFirst(Object, defaultValue)
        assertThat result, sameInstance(result)
    }

    @Test
    void testLoadFirstUnavailable() {
        NoServicesClassLoader.runWith {
            expect UnavailableImplementationException, { Services.loadFirst(StubService.class) }
        }
    }

    @Test
    void testLoadAllUnavailable() {
        NoServicesClassLoader.runWith {
            expect UnavailableImplementationException, { Services.loadAll(StubService.class) }
        }
    }

    static class NoServicesClassLoader extends ClassLoader {
        private NoServicesClassLoader(ClassLoader parent) {
            super(parent)
        }

        @Override
        Enumeration<URL> getResources(String name) throws IOException {
            if (name.startsWith("META-INF/services/")) {
                return java.util.Collections.emptyEnumeration()
            } else {
                return super.getResources(name)
            }
        }

        static void runWith(Closure closure) {
            ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader()
            try {
                Thread.currentThread().setContextClassLoader(new NoServicesClassLoader(originalClassloader))
                closure.run()
            } finally {
                if (originalClassloader != null) {
                    Thread.currentThread().setContextClassLoader(originalClassloader)
                }
            }
        }
    }
}
