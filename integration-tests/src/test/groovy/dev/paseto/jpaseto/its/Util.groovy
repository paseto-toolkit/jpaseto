package dev.paseto.jpaseto.its

import dev.paseto.jpaseto.Paseto
import dev.paseto.jpaseto.Purpose
import dev.paseto.jpaseto.Version
import dev.paseto.jpaseto.impl.DefaultPaseto
import dev.paseto.jpaseto.impl.DefaultFooterClaims
import dev.paseto.jpaseto.impl.DefaultClaims

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class Util {

    static Paseto v2PublicFromClaims(Map<String, Object> claims, Map<String, Object> footerClaims) {
        return fromClaims(Version.V2, Purpose.PUBLIC, claims, footerClaims)
    }

    static Paseto v2LocalFromClaims(Map<String, Object> claims, Map<String, Object> footerClaims) {
        return fromClaims(Version.V2, Purpose.LOCAL, claims, footerClaims)
    }

    static Paseto v1LocalFromClaims(Map<String, Object> claims, Map<String, Object> footerClaims) {
        return fromClaims(Version.V1, Purpose.LOCAL, claims, footerClaims)
    }

    static Paseto v1PublicFromClaims(Map<String, Object> claims, Map<String, Object> footerClaims) {
        return fromClaims(Version.V1, Purpose.PUBLIC, claims, footerClaims)
    }

    static Paseto fromClaims(Version version, Purpose purpose, Map<String, Object> claims, Map<String, Object> footerClaims) {
        new DefaultPaseto(version, purpose, new DefaultClaims(claims), new DefaultFooterClaims(footerClaims))
    }

    static Clock clockForVectors() {
        return Clock.fixed(Instant.ofEpochMilli(1544490000000), ZoneOffset.UTC) // December 11, 2018 01:00:00
    }
}
