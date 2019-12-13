package dev.paseto.jpaseto.its

import dev.paseto.jpaseto.Paseto
import dev.paseto.jpaseto.PasetoBuilder
import dev.paseto.jpaseto.PasetoParser
import dev.paseto.jpaseto.PasetoV1LocalBuilder
import dev.paseto.jpaseto.PasetoV2PublicBuilder
import dev.paseto.jpaseto.Pasetos
import dev.paseto.jpaseto.lang.Keys
import org.apache.commons.codec.binary.Hex
import org.hamcrest.MatcherAssert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import javax.crypto.SecretKey
import java.security.PrivateKey
import java.security.PublicKey

import static dev.paseto.jpaseto.its.Util.clockForVectors
import static dev.paseto.jpaseto.its.Util.v1LocalFromClaims
import static dev.paseto.jpaseto.its.Util.v1PublicFromClaims
import static dev.paseto.jpaseto.its.Util.v2LocalFromClaims
import static dev.paseto.jpaseto.its.Util.v2LocalFromClaims
import static dev.paseto.jpaseto.its.Util.v2PublicFromClaims
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class V1LocalStandardIT {

        @Test(dataProvider = "officialV1LocalTestVectors")
    void officialV1LocalDecodeTest(String token, SecretKey secretKey, Map<String, Object> claims, Map<String, Object> footer, String name) {

        PasetoParser parser = Pasetos.parserBuilder()
                .setClock(clockForVectors())
                .setSharedSecret(secretKey)
                .build()

        Paseto result = parser.parse(token)
        assertThat result, PasetoMatcher.paseto(v1LocalFromClaims(claims, footer))
    }

    @Test(dataProvider = "officialV1LocalTestVectors")
    void officialV1LocalEncodeTest(String token, SecretKey secretKey, Map<String, Object> claims, Map<String, Object> footer, String name) {

        PasetoBuilder pasetoBuilder = Pasetos.v1Local().builder().setSharedSecret(secretKey)

        claims.forEach { key, value ->
            pasetoBuilder.claim(key, value)
        }
        if (footer) {
            footer.forEach { key, value ->
                pasetoBuilder.footerClaim(key, value)
            }
        }

        String result = pasetoBuilder.compact()
        Paseto parsedResult =  Pasetos.parserBuilder()
                .setClock(clockForVectors())
                .setSharedSecret(secretKey)
                .build()
                .parse(result)

        assertThat parsedResult, is(PasetoMatcher.paseto(v1LocalFromClaims(claims, footer)))
    }

    @DataProvider()
    Object[][] officialV1LocalTestVectors() {

        SecretKey sharedSecret = Keys.secretKey(Hex.decodeHex('707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f'))

        return [
                ['v1.local.WzhIh1MpbqVNXNt7-HbWvL-JwAym3Tomad9Pc2nl7wK87vGraUVvn2bs8BBNo7jbukCNrkVID0jCK2vr5bP18G78j1bOTbBcP9HZzqnraEdspcjd_PvrxDEhj9cS2MG5fmxtvuoHRp3M24HvxTtql9z26KTfPWxJN5bAJaAM6gos8fnfjJO8oKiqQMaiBP_Cqncmqw8',
                sharedSecret,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                null,
                'Test Vector 1.1'],

                ['v1.local.w_NOpjgte4bX-2i1JAiTQzHoGUVOgc2yqKqsnYGmaPaCu_KWUkRGlCRnOvZZxeH4HTykY7AE_jkzSXAYBkQ1QnwvKS16uTXNfnmp8IRknY76I2m3S5qsM8klxWQQKFDuQHl8xXV0MwAoeFh9X6vbwIqrLlof3s4PMjRDwKsxYzkMr1RvfDI8emoPoW83q4Q60_xpHaw',
                sharedSecret,
                [data: 'this is a secret message', exp: '2019-01-01T00:00:00+00:00'],
                null,
                'Test Vector 1.2'],

                ['v1.local.4VyfcVcFAOAbB8yEM1j1Ob7Iez5VZJy5kHNsQxmlrAwKUbOtq9cv39T2fC0MDWafX0nQJ4grFZzTdroMvU772RW-X1oTtoFBjsl_3YYHWnwgqzs0aFc3ejjORmKP4KUM339W3syBYyjKIOeWnsFQB6Yef-1ov9rvqt7TmwONUHeJUYk4IK_JEdUeo_uFRqAIgHsiGCg',
                sharedSecret,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                null,
                'Test Vector 1.3'],

                ['v1.local.IddlRQmpk6ojcD10z1EYdLexXvYiadtY0MrYQaRnq3dnqKIWcbbpOcgXdMIkm3_3gksirTj81bvWrWkQwcUHilt-tQo7LZK8I6HCK1V78B9YeEqGNeeWXOyWWHoJQIe0d5nTdvejdt2Srz_5Q0QG4oiz1gB_wmv4U5pifedaZbHXUTWXchFEi0etJ4u6tqgxZSklcec',
                sharedSecret,
                [data: 'this is a secret message', exp: '2019-01-01T00:00:00+00:00'],
                null,
                'Test Vector 1.4'],

                ['v1.local.4VyfcVcFAOAbB8yEM1j1Ob7Iez5VZJy5kHNsQxmlrAwKUbOtq9cv39T2fC0MDWafX0nQJ4grFZzTdroMvU772RW-X1oTtoFBjsl_3YYHWnwgqzs0aFc3ejjORmKP4KUM339W3szA28OabR192eRqiyspQ6xPM35NMR-04-FhRJZEWiF0W5oWjPVtGPjeVjm2DI4YtJg.eyJraWQiOiJVYmtLOFk2aXY0R1poRnA2VHgzSVdMV0xmTlhTRXZKY2RUM3pkUjY1WVp4byJ9',
                sharedSecret,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                [kid: 'UbkK8Y6iv4GZhFp6Tx3IWLWLfNXSEvJcdT3zdR65YZxo'],
                'Test Vector 1.5'],

                ['v1.local.IddlRQmpk6ojcD10z1EYdLexXvYiadtY0MrYQaRnq3dnqKIWcbbpOcgXdMIkm3_3gksirTj81bvWrWkQwcUHilt-tQo7LZK8I6HCK1V78B9YeEqGNeeWXOyWWHoJQIe0d5nTdvcT2vnER6NrJ7xIowvFba6J4qMlFhBnYSxHEq9v9NlzcKsz1zscdjcAiXnEuCHyRSc.eyJraWQiOiJVYmtLOFk2aXY0R1poRnA2VHgzSVdMV0xmTlhTRXZKY2RUM3pkUjY1WVp4byJ9',
                sharedSecret,
                [data: 'this is a secret message', exp: '2019-01-01T00:00:00+00:00'],
                [kid: 'UbkK8Y6iv4GZhFp6Tx3IWLWLfNXSEvJcdT3zdR65YZxo'],
                'Test Vector 1.6'],
        ]
    }
}
