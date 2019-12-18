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

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.FooterClaims;
import dev.paseto.jpaseto.KeyResolver;
import dev.paseto.jpaseto.PasetoParser;
import dev.paseto.jpaseto.PasetoParserBuilder;
import dev.paseto.jpaseto.Purpose;
import dev.paseto.jpaseto.Version;
import dev.paseto.jpaseto.io.Deserializer;
import dev.paseto.jpaseto.lang.Assert;
import dev.paseto.jpaseto.lang.Services;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@AutoService(PasetoParserBuilder.class)
public class DefaultPasetoParserBuilder implements PasetoParserBuilder {

    private PublicKey publicKey = null;
    private SecretKey sharedSecret = null;
    private KeyResolver keyResolver = null;
    private Deserializer<Map<String, Object>> deserializer;
    private Clock clock = Clock.systemUTC();
    private Duration allowedClockSkew = Duration.ofMillis(0);

    private final Map<String, Predicate<Object>> expectedClaimsMap = new HashMap<>();
    private final Map<String, Predicate<Object>> expectedFooterClaimsMap = new HashMap<>();

    @Override
    public PasetoParserBuilder setKeyResolver(KeyResolver keyResolver) {
        this.keyResolver = keyResolver;
        return this;
    }

    @Override
    public PasetoParserBuilder setSharedSecret(SecretKey sharedSecret) {
        this.sharedSecret = sharedSecret;
        return this;
    }

    @Override
    public PasetoParserBuilder setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    @Override
    public PasetoParserBuilder setDeserializer(Deserializer<Map<String, Object>> deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    @Override
    public PasetoParser build() {

        Assert.isTrue( publicKey != null || sharedSecret != null,"PasetoParser must be configure with a public key (for public tokens) and/or a sharedSecret (for local tokens).");

        @SuppressWarnings("unchecked")
        Deserializer<Map<String, Object>> tmpDeserializer = (this.deserializer != null)
                ? this.deserializer
                : Services.loadFirst(Deserializer.class);

        // validate we have either a private key or shared key OR the KeyResolver set, NOT both
        boolean hasDirectKeys = publicKey != null || sharedSecret != null;
        if (hasDirectKeys && keyResolver != null) {
            throw new IllegalStateException("Both a KeyResolver and a publicKey/sharedSecret cannot be used together, use one or the other");
        }

        KeyResolver tmpKeyResolver = keyResolver != null
                ? keyResolver
                : new SimpleKeyResolver(publicKey, sharedSecret);

        return new DefaultPasetoParser(tmpKeyResolver, tmpDeserializer, clock, allowedClockSkew, expectedClaimsMap, expectedFooterClaimsMap);
    }


    @Override
    public PasetoParserBuilder require(String claimName, Predicate<Object> value) {
        Assert.hasText(claimName, "claim name cannot be null or empty.");
        Assert.notNull(value, "The value cannot be null for claim name: " + claimName);
        expectedClaimsMap.put(claimName, value);
        return this;
    }

    @Override
    public PasetoParserBuilder requireFooter(String claimName, Predicate<Object> value) {
        Assert.hasText(claimName, "claim name cannot be null or empty.");
        Assert.notNull(value, "The value cannot be null for claim name: " + claimName);
        expectedFooterClaimsMap.put(claimName, value);
        return this;
    }

    @Override
    public PasetoParserBuilder setClock(Clock clock) {
        Assert.notNull(clock, "Clock instance cannot be null.");
        this.clock = clock;
        return this;
    }

    @Override
    public PasetoParserBuilder setAllowedClockSkew(Duration allowedClockSkewMillis) {
        this.allowedClockSkew = allowedClockSkewMillis;
        return this;
    }

    private static class SimpleKeyResolver implements KeyResolver {

        private final PublicKey publicKey;
        private final SecretKey sharedSecret;

        private SimpleKeyResolver(PublicKey publicKey, SecretKey sharedSecret) {
            this.publicKey = publicKey;
            this.sharedSecret = sharedSecret;
        }

        @Override
        public PublicKey resolvePublicKey(Version version, Purpose purpose, FooterClaims footer) {
            Assert.notNull(publicKey, "A public key has not been configured.  A public key must be configured in " +
                "'Pasetos.parserBuilder().setPublicKey(...)' or Pasetos.parserBuilder().setKeyResolver(...)");
            return publicKey;
        }

        @Override
        public SecretKey resolveSharedKey(Version version, Purpose purpose, FooterClaims footer) {
            Assert.notNull(sharedSecret, "A shared secret has not been configured.  A shared secret must be configured in " +
                "'Pasetos.parserBuilder().setSharedSecret(...)' or Pasetos.parserBuilder().setKeyResolver(...)");
            return sharedSecret;
        }
    }
}
