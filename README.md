[<img src="/docs/jpaseto-logo" align="right" width="256px"/>](https://github.com/paseto-toolkit/jpaseto)
[![Maven Central](https://img.shields.io/maven-central/v/dev.paseto/jpaseto-api.svg)](https://search.maven.org/search?q=g:dev.paseto)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# JPaseto - Paseto Library for Java

JPaseto aims to be the easiest to use and understand library for creating and verifying Paseto tokens on the JVM.

JPaseto is a Java implementation based exclusively on the [Paseto specification](https://paseto.io/rfc/). And is a 
direct port of [JJWT](https://github.com/jwtk/jjwt/), if you are using JWTs check out that library.

We've also added some convenience extensions that are not yet part of the specification, such as validation of the 
registered date claims.

The goal of this project is to provide a pure Java implementation of the Paseto specification. 

**NOTE:** "v2.local" tokens currently require the use of a native library "libsodium", a pure java implementation will be 
available in the future. (see below for more details)

## Table of Contents

* [Features](#features)
  * [Differences Between Other Java Paseto Implementations](#other-options)
* [Community](#community)
  * [Getting Help](#help)
    * [Questions](#help-questions)
    * [Bugs and Feature Requests](#help-issues)
  * [Contributing](#contributing)
    * [Pull Requests](#contributing-pull-requests)
    * [Help Wanted](#contributing-help-wanted)
* [What is a Paseto Token?](#overview)
* [Installation](#install)
  * [JDK Projects](#install-jdk)
    * [Maven](#install-jdk-maven)
    * [Gradle](#install-jdk-gradle)
    * [Sodium native library](#install-sodium)
  * [Understanding JPaseto Dependencies](#install-understandingdependencies)
* [Quickstart](#quickstart)
* [Keys and Secrets](#keys-secrets)
  * [Creating Safe Keys](#key-create)
    * [Secret Keys](#key-create-secret)
    * [Asymetric Keys](#key-create-asym)
  * [Create a Paseto Token](#paseto-create)
    * [Footer](#paseto-create-footer)
    * [Claims](#paseto-create-claims)
      * [Standard Claims](#paseto-create-claims-standard)
      * [Custom Claims](#paseto-create-claims-custom)
  * [Read a Paseto Token](#paseto-read)
    * [Verification Key](#paseto-read-key)
      * [Find the Verification Key at Runtime](#paseto-read-key-resolver)
    * [Claims Assertions](#paseto-read-claims)
    * [Accounting for Clock Skew](#paseto-read-clock)
      * [Custom Clock](#paseto-read-clock-custom)
* [JSON Processor](#json)
  * [Custom JSON Processor](#json-custom)
  * [Parsing of Custom Claim Types](#json-jackson-custom-types)
* [Learn More](#learn-more)
* [License](#license)

<a name="features"></a>
## Features

 * Fully functional on all JDKs 1.8+
 * Automatic security best practices and assertions
 * Easy to learn and read API
 * Convenient and readable [fluent](http://en.wikipedia.org/wiki/Fluent_interface) interfaces, great for IDE auto-completion to write code quickly
 * Fully RFC-draft specification compliant on all implemented functionality, tested against RFC-specified test vectors
 * Convenience enhancements beyond the specification such as
    * Claims assertions (requiring specific values)
    * Claim POJO marshaling and unmarshaling when using a compatible JSON parser (e.g. Jackson) 
    * and more...
    
<a name="other-options"></a>
### Differences Between Other Java Paseto Implementations

Why choose this library over the other Java Paseto implementations?

- Fluent API
- Full security audited performed by [Paragon Initiative Enterprises](https://paragonie.com)
- Available on Maven Central
- Low dependency count(with the exception of libsodium)
- Already using JJWT, this library works the same way

<a name="community"></a>
## Community

<a name="help"></a>
### Getting Help

If you have trouble using JPaseto, please first read the documentation on this page before asking questions.  We try 
very hard to ensure JPaseto's documentation is robust, categorized with a table of contents, and up to date for each release.

<a name="help-questions"></a>
#### Questions

If the documentation or the API JavaDoc isn't sufficient, and you either have usability questions or are confused
about something, please [ask your question here](https://stackoverflow.com/questions/ask?tags=jpaseto&guided=false).
   
If you believe you have found a bug or would like to suggest a feature enhancement, please create a new GitHub issue, 
however:

**Please do not create a GitHub issue to ask a question.**

We use GitHub Issues to track actionable work that requires changes to JPaseto's design and/or codebase.  If you have a 
usability question, instead please 
[ask your question here](https://stackoverflow.com/questions/ask?tags=jpaseto&guided=false).

<a name="help-issues"></a>
#### Bugs and Feature Requests

If you do not have a usability question and believe you have a legitimate bug or feature request, 
please do [create a new JPaseto issue](https://github.com/pasetodev/jpaseto/issues/new).

If you feel like you'd like to help fix a bug or implement the new feature yourself, please read the Contributing 
section next before starting any work.

<a name="contributing"></a>
### Contributing

<a name="contributing-pull-requests"></a>
#### Pull Requests

Simple Pull Requests that fix anything other than JPaseto core code (documentation, JavaDoc, typos, test cases, etc) are 
always appreciated and have a high likelihood of being merged quickly. Please send them!

However, if you want or feel the need to change JPaseto's functionality or core code, please do not issue a pull request 
without [creating a new JPaseto issue](https://github.com/pasetodev/jpaseto/issues/new) and discussing your desired 
changes **first**, _before you start working on it_.

It would be a shame to reject your earnest and genuinely appreciated pull request if it might not not align with the 
project's goals, design expectations or planned functionality.

So, please [create a new JPaseto issue](https://github.com/pasetodev/jpaseto/issues/new) first to discuss, and then we can see if
(or how) a PR is warranted.  Thank you!

<a name="contributing-help-wanted"></a>
#### Help Wanted

If you would like to help, but don't know where to start, please visit the 
[Help Wanted Issues](https://github.com/pasetodev/jpaseto/labels/help%20wanted) page and pick any of the 
ones there, and we'll be happy to discuss and answer questions in the issue comments.

If any of those don't appeal to you, no worries! Any help you would like to offer would be 
appreciated based on the above caveats concerning [contributing pull reqeuests](#contributing-pull-requests). Feel free
to discuss or ask questions first if you're not sure. :)

<a name="overview"></a>
## What is a Paseto Token?

Don't know what a Paseto Token is? Read on. Otherwise, jump on down to the [Installation](#Installation) section.

Paseto is a means of transmitting information between two parties in a compact, verifiable form.

The bits of information encoded in the body of a Paseto token are called `claims`. The expanded form of the Paseto is in a JSON format, so each `claim` is a key in the JSON object.
 
Paseto can be cryptographically signed ("public" tokens) or encrypted with a shared secret ("local" tokens).

This adds a powerful layer of verifiability to the user of Paseto tokens. The receiver has a high degree of confidence that the Paseto token has not been tampered with by verifying the signature, for instance.

The compact representation of a signed Paseto token is a string that has three or four parts, each separated by a `.`:

```
version.purpose.payload.footer
```
> the footer is optional

The version is a string that represents the current version of the protocol. Currently, two versions are specified, which each possess their own ciphersuites. Accepted values: v1, v2.

The purpose is a short string describing the purpose of the token. Accepted values: local, public.

- local: shared-key authenticated encryption
- public: public-key digital signatures; not encrypted
Any optional data can be appended to the end. This information is NOT encrypted, but it is used in calculating the authentication tag for the payload. It's always base64url-encoded.

- For local tokens, it's included in the associated data alongside the nonce.
- For public tokens, it's appended to the message during the actual authentication/signing step, in accordance to our standard format.
Thus, if you want unencrypted, but authenticated, tokens, you can simply set your payload to an empty string and your footer to the message you want to authenticate.

Conversely, if you want to support key rotation, you can use the unencrypted footer to store the `kid` claim.

There are a number of standard claims, called Registered Claims, see section 6.1 
in [the specification](https://paseto.io/rfc/) and `sub` (for subject) is one of them.

To compute the signature, you need a secret key to sign it. We'll cover keys later.

<a name="install"></a>
## Installation

Use your favorite Maven-compatible build tool to pull the dependencies from Maven Central.

The dependencies could differ slightly if you are working with a [JDK project](#install-jdk).

<a name="install-jdk"></a>
### JDK Projects

If you're building a (non-Android) JDK project, you will want to define the following dependencies:

<a name="install-jdk-maven"></a>
#### Maven

```xml
<dependency>
    <groupId>dev.paseto</groupId>
    <artifactId>jpaseto-api</artifactId>
    <version>0.1.0</version>
</dependency>
<dependency>
    <groupId>dev.paseto</groupId>
    <artifactId>jpaseto-impl</artifactId>
    <version>0.1.0</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>dev.paseto</groupId>
    <artifactId>jpaseto-jackson</artifactId>
    <version>0.1.0</version>
    <scope>runtime</scope>
</dependency>
<!-- Uncomment the next lines if you want to use v1.local tokens -->
<!-- 
<dependency>
    <groupId>dev.paseto</groupId>
    <artifactId>jpaseto-bouncy-castle</artifactId>
    <version>0.1.0</version>
    <scope>runtime</scope>
</dependency> -->
<!-- Uncomment the next lines if you want to use v2 tokens -->
<!-- NOTE: this requires the native lib sodium library installed on your system see below -->
<!-- 
<dependency>
    <groupId>dev.paseto</groupId>
    <artifactId>jpaseto-sodium</artifactId>
    <version>0.1.0</version>
    <scope>runtime</scope>
</dependency> -->
```

<a name="install-jdk-gradle"></a>
#### Gradle

```groovy
dependencies {
    compile 'dev.paseto:jpaseto-api:0.1.0'
    runtime 'dev.paseto:jpaseto-impl:0.1.0',
            // Uncomment the next lines if you want to use v1.local tokens
            // 'dev.paseto:jpaseto-bouncy-castle:0.1.0',
            // Uncomment the next lines if you want to use v2 tokens
            // NOTE: this requires the native lib sodium library installed on your system see below
            // 'dev.paseto:jpaseto-sodium:0.1.0',
            'dev.paseto:jpaseto-jackson:0.1.0'
}
```
<a name="install-sodium"></a>
#### libsodium

Installation the a native library [libsodium](https://github.com/jedisct1/libsodium) is required when creating or parseing "v2.local" tokens.

**NOTE:** `public` tokens can be used with the `jpaseto-bouncy-castle` dependency or Java 11+. `v1.local` tokens require `jpaseto-bouncy-castle`.

- MacOS -  Can install libsodium using brew: 
  
  `brew install libsodium`

- Windows - Download [prebuilt binaries](https://download.libsodium.org/libsodium/releases/)

<a name="install-understandingdependencies"></a>
### Understanding JPaseto Dependencies

Notice the above dependency declarations all have only one compile-time dependency and the rest are declared as 
_runtime_ dependencies.

This is because JPaseto is designed so you only depend on the APIs that are explicitly designed for you to use in
your applications and all other internal implementation details - that can change without warning - are relegated to
runtime-only dependencies.  This is an extremely important point if you want to ensure stable JPaseto usage and
upgrades over time:

**JPaseto guarantees semantic versioning compatibility for only the `jpaseto-api` .jar.

This is done to benefit you: great care goes into curating the `jpaseto-api` .jar and ensuring it contains what you need
and remains backwards compatible as much as is possible so you can depend on that safely with compile scope.  The 
runtime `jpaseto-impl` .jar strategy affords the JPaseto developers the flexibility to change the internal packages and 
implementations whenever and however necessary.  This helps us implement features, fix bugs, and ship new releases to 
you more quickly and efficiently.

<a name="quickstart"></a>
## Quickstart

Most complexity is hidden behind a convenient and readable builder-based [fluent interface](http://en.wikipedia.org/wiki/Fluent_interface), great for relying on IDE auto-completion to write code quickly.  Here's an example:

```java
import dev.paseto.jpaseto.Pasetos;
import dev.paseto.jpaseto.lang.Keys;
import java.security.SecretKey;

// We need a secret key, so we'll create one just for this example. Usually
// the key would be read from your application configuration instead.
SecretKey key = Keys.secretKey()

String token = Pasetos.V1.LOCAL.builder()
    .setSubject("Joe")
    .setSharedSecret(key)
    .compact();
```

How easy was that!?

In this case, we are:
 
 1. *building* a Paseto token that will have the registered claim `sub` (subject) set to `Joe`. We are then
 2. *encrypted* the Paseto token using a shared secret with AES-256-CTR algorithm.  Finally, we are
 3. *compacting* it into its final `String` form.

The resultant `token` String looks like this:

```
v1.local.CuizxAzVIz5bCqAjsZpXXV5mk_WWGHbVxmdF81DORwyYcMLvzoUHUmS_VKvJ1hn5zXyoMkygkEYLM2LM00uBI3G9gXC5VrZCUM-BLZo1q9IDIncAZTxYkE1NUTMz
```

Now let's verify the Paseto (parsing tokens with invalid signatures or public keys will throw an exception):

```java
assert Pasetos.parserBuilder().setSharedSecret(key).build().parse(token).getClaims().getSubject().equals("Joe");
```

There are two things going on here. The `key` from before is being used to validate the signature of the token. If it 
fails to verify the token, a `PasetoSignatureException` (which extends from `PasetoException`) is thrown. Assuming the Paseto token is 
validated, we parse out the claims and assert that that subject is set to `Joe`.

You have to love code one-liners that pack a punch!

But what if parsing or signature validation failed?  You can catch `PasetoSignatureException` and react accordingly:

```java
try {
    Pasetos.parserBuilder().setSharedSecret(key).build().parse(token);
    //OK, we can trust this token
} catch (PasetoException e) {
    //don't trust the token!
}
```
<a name="keys-secrets"></a>
## Keys and Secrets

<a name="key-create"></a>
### Creating Safe Keys

If you don't want to think about key requirements or just want to make your life easier, JPaseto has
provided the `dev.paseto.jpaseto.lang.Keys` utility class that can generate sufficiently secure keys.

<a name="key-create-secret"></a>
#### Secret Keys

If you want to generate a sufficiently strong `SecretKey` for use with "local" tokens, use the 
`Keys.secretKey()` helper method:

```java
SecretKey key = Keys.secretKey();
```
<a name="key-create-asym"></a>
#### Asymmetric Keys

If you want to generate sufficiently strong Elliptic Curve or RSA asymmetric key pairs for use with "public" Ed25519 or RSA
algorithms, use the `Keys.keyPairFor(Version)` helper method:

```java
KeyPair keyPair = Keys.keyPairFor(Version.V1);
```

You use the private key (`keyPair.getPrivate()`) to create a token and the public key (`keyPair.getPublic()`) to 
parse/verify a token.

<a name="paseto-create"></a>
### Creating a Paseto Token

You create a Paseto token as follows:

1. Use one of the `Pasetos.*Builder()` methods to create a `PasetoBuilder` instance.  
2. Call `PasetoBuilder` methods to add claims as desired.
3. Specify the `SecretKey` for "local" or asymmetric `PrivateKey` for "public" tokens.
4. Finally, call the `compact()` method to compact and encrypt/sign, producing the final token.

For example:

```java
String token = Pasetos.V2.LOCAL.builder() // (1)
    .setSubject("Bob")                     // (2) 
    .setSharedSecret(key)                  // (3)
    .compact();                            // (4)
```

<a name="paseto-create-footer"></a>
#### Footer Parameters

A Paseto footer provides metadata about the contents, specifically a `kid` when using rotated keys.

If you need to set one or more footer parameters, you can simply call
`PasetBuilder` `footerClaim` one or more times as needed:

```java
String token = Pasetos.V2.PUBLIC.builder()
    .setKeyId("myKeyId")
    .footerClaim("other", "data")
    // ... etc ...
```

Each time `footerClaim` is called, it simply appends the key-value pair to an internal `footer` instance, 
potentially overwriting any existing identically-named key/value pair.

<a name="paseto-create-claims"></a>
#### Claims

Claims are a Paseto token's 'payload' and contain the information that the Paseto token creator wishes to present to the token recipient(s).

<a name="paseto-create-claims-standard"></a>
##### Standard Claims

The `PasetoBuilder` provides convenient setter methods for standard registered Claim names defined in the Paseto 
specification.  They are:

* `setIssuer`: sets the `iss` (Issuer) Claim
* `setSubject`: sets the `sub` (Subject) Claim
* `setAudience`: sets the `aud` (Audience) Claim
* `setExpiration`: sets the `exp` (Expiration Time) Claim
* `setNotBefore`: sets the `nbf` (Not Before) Claim
* `setIssuedAt`: sets the `iat` (Issued At) Claim
* `setTokenId`: sets the `jti` (Token ID) Claim
* `setKeyId`: sets the `kid` (Key ID) Claim - found in the footer

For example:

```java
Pasetos.V1.PUBLIC.builder()
    .setIssuer("me")
    .setSubject("Bob")
    .setAudience("you")
    .setExpiration(expiration)     //a java.time.Instant
    .setNotBefore(notBefore)       //a java.time.Instant
    .setIssuedAt(Instant.now())    // for example, now
    .setTokenId(UUID.randomUUID()) //just an example id
    /// ... etc ...
```

<a name="paseto-create-claims-custom"></a>
##### Custom Claims

If you need to set one or more custom claims that don't match the standard setter method claims shown above, you
can simply call `PasetoBuilder` `claim` one or more times as needed:

```java
Pasetos.V2.PUBLIC.builder()
    .claim("hello", "world")
    // ... etc ...
```

Each time `claim` is called, it simply appends the key-value pair to an internal `Claims` instance, potentially 
overwriting any existing identically-named key/value pair.

Obviously, you do not need to call `claim` for any [standard claim name](#jws-create-claims-standard) and it is 
recommended instead to call the standard respective setter method as this enhances readability.

<a name="paseto-read"></a>
### Reading a Paseto Token

You read (parse) a Pasto token as follows:

1. Use the `Pasetos.parserBuilder()` method to create a `PasetoParserBuilder` instance.  
2. Specify the `SecretKey` (for encrypted "local" tokens) or asymmetric `PublicKey` (for signed "public" tokens).<sup>1</sup>
3. Create a reusable and immutable `PasetoParser`.
4. Finally, call the `parse(String)` method with your token `String`, producing the original token.
5. The entire call is wrapped in a try/catch block in case parsing or signature validation fails.  We'll cover
   exceptions and causes for failure later.

<sup>1. If you don't know which key to use at the time of parsing, you can look up the key using a `KeyResolver` 
which [we'll cover later](#jws-read-key-resolver).</sup>

For example:

```java
Paseto paseto;

try {
    paseto = Pasetos.parserBuilder()  // (1)
        .setSharedSecret(key)         // (2)
        .build()                      // (3)
        .parse(tokenString);          // (4)
    // we can safely trust the token
} catch (PasetoException ex) {        // (5)
    // we *cannot* use the token as intended by its creator
}
```

<a name="paseto-read-key"></a>
#### Verification Key

The most important thing to do when reading a Paseto token is to specify the key to use to verify the token's
cryptographic signature.  If signature verification fails, the Paseto cannot be safely trusted and should be 
discarded.

So which key do we use for verification?

* If the token was encrypted with a `SecretKey` (for "local" tokens), the same `SecretKey` should be specified on the `PasetoParserBuilder`.  For example:

  ```java
  Pasetos.parserBuilder()
    .setSharedSecret(secretKey) // <----
    .build()
    .parse(tokenString);
  ```
* If the token was signed with a `PrivateKey` (for "public" tokens), that key's corresponding `PublicKey` (not the `PrivateKey`) should be 
  specified on the `PasetoParserBuilder`.  For example:

  ```java
  Pasetos.parserBuilder()
    .setPublicKey(publicKey) // <---- publicKey, not privateKey
    .build()
    .parse(tokenString);
  ```
  
But you might have noticed something - what if your application doesn't use just a single SecretKey or KeyPair? What
if tokens can be created with different `SecretKey`s or public/private keys, or a combination of both?  How do you
know which key to specify if you can't inspect the Paseto token first?

In these cases, you can't call the `PasetoParserBuilder`'s `setSharedSecret` method with a single key - instead, you'll need
to use a `KeyResolver`, covered next.

<a name="paseto-read-key-resolver"></a>
##### Signing Key Resolver

If your application expects tokens that can be signed with different keys, you won't call the `setSharedSecret` method.
Instead, you'll need to implement the 
`KeyResolver` interface and specify an instance on the `PasetoParserBuilder` via the `setKeyResolver` method.  
For example:

```java
KeyResolver KeyResolver = getMyKeyResolver();

Paseto token = Pasetos.parserBuilder()
    .setKeyResolver(KeyResolver) // <----
    .build()
    .parse(tokenString);
```

You can simplify things a little by extending from the `KeyResolverAdapter` and implementing the 
`resolvePublicKey(Version, Purpose, FooterClaims)` method.  For example:

```java
public class MyKeyResolver extends KeyResolverAdapter {
    
    @Override
    public PublicKey resolvePublicKey(Version version, Purpose purpose, FooterClaims footer) {
        // implement me
    }
}
```

The `PasetoParser` will invoke the `resolveSigningKey` method after parsing the payload JSON, but _before verifying the
signature_.  This allows you to inspect the `FooterClaims` argument for any information that can
help you look up the `Key` to use for verifying _that specific token_.  This is very powerful for applications
with more complex security models that might use different keys at different times or for different users or customers.

Which data might you inspect?

The Paseto specification's supported way to do this is to set a `kid` (Key ID) field in the footer when the token is 
being created, for example:

```java

PublicKey signingKey = getSigningKey();
String keyId = getKeyId(signingKey); //any mechanism you have to associate a key with an ID is fine
String token = Pasetos.V1.PUBLIC.builder()
    .setKeyId(keyId)            // 1
    .setPublicKey(signingKey)   // 2
    .compact();
```

Then during parsing, your `KeyResolver` can inspect the `FooterClaims` to get the `kid` and then use that value
to look up the key from somewhere, like a database.  For example:

```java
public class MyKeyResolver extends KeyResolverAdapter {
    
    @Override
    public PublicKey resolvePublicKey(Version version, Purpose purpose, FooterClaims footer) {
        //inspect the footer, lookup and return the signing key
        String keyId = footer.getKeyId(); //or any other field that you need to inspect
        PublicKey key = lookupVerificationKey(keyId); //implement me
        return key;
    }
}
```

Note that inspecting the `footer.getKeyId()` is just the most common approach to look up a key - you could 
inspect any number of footer fields to determine how to lookup the verification key.  It is all based on 
how the token was created.

Finally remember that for "local" tokens a `SecretKey` is used, and for "public" tokens a `Public` key is used.

<a name="paseto-read-claims"></a>
#### Claim Assertions

You can enforce that the Pasto token you are parsing conforms to expectations that you require and are important for your 
application.

For example, let's say that you require that the token you are parsing has a specific `sub` (subject) value,
otherwise you may not trust the token.  You can do that by using one of the various `require`* methods on the 
`PasetoParserBuilder`:

```java
try {
    Pasetos.parserBuilder()
        .requireSubject("jsmith")
        .setSharedSecret(key)
        .build()
        .parse(s);
} catch(InvalidClaimException ice) {
    // the sub field was missing or did not have a 'jsmith' value
}
```

If it is important to react to a missing vs an incorrect value, instead of catching `InvalidClaimException`, 
you can catch either `MissingClaimException` or `IncorrectClaimException`:

```java
try {
    Pasetos.parserBuilder().requireSubject("jsmith").setSharedSecret(key).build().parse(s);
} catch(MissingClaimException mce) {
    // the parsed token did not have the sub field
} catch(IncorrectClaimException ice) {
    // the parsed token had a sub field, but its value was not equal to 'jsmith'
}
```

You can also require custom fields by using the `require(fieldName, requiredFieldValue)` method - for example:

```java
try {
    Pasetos.parserBuilder().require("myfield", "myRequiredValue").setSharedSecret(key).build().parse(s);
} catch(InvalidClaimException ice) {
    // the 'myfield' field was missing or did not have a 'myRequiredValue' value
}
```
(or, again, you could catch either `MissingClaimException` or `IncorrectClaimException` instead).

Please see the `PasetoParserBuilder` class and/or JavaDoc for a full list of the various `require`* methods you may use for claims
assertions.

<a name="paseto-read-clock"></a>
#### Accounting for Clock Skew

When parsing a Paseto token, you might find that `exp` or `nbf` claim assertions fail (throw exceptions) because the clock on 
the parsing machine is not perfectly in sync with the clock on the machine that created the token.  This can cause 
obvious problems since `exp` and `nbf` are time-based assertions, and clock times need to be reliably in sync for shared
assertions.

You can account for these differences (usually no more than a few minutes) when parsing using the `PasetoParserBuilder`'s
 `setAllowedClockSkew`. For example:

```java
Pasetos.parserBuilder()
    .setAllowedClockSkew(Duration.ofMinutes(3)) // <----
    // ... etc ...
    .build()
    .parse(tokenString);
```
This ensures that clock differences between the machines can be ignored. Two or three minutes should be more than 
enough; it would be fairly strange if a production machine's clock was more than 5 minutes difference from most 
atomic clocks around the world.

<a name="paseto-read-clock-custom"></a>
##### Custom Clock Support

If the above `setAllowedClockSkew` isn't sufficient for your needs, the timestamps created
during parsing for timestamp comparisons can be obtained via a custom time source.  Call the `PasetoParserBuilder`'s `setClock`
 method with an implementation of the `java.time.Clock` interface.  For example:
 
 ```java
Clock clock = new MyClock();

Pasetos.parserBuilder().setClock(myClock) //... etc ...
``` 

The `PasetoParser`'s default `Clock` implementation uses `Clock.systemUTC()`, as most would expect.  
However, supplying your own clock could be useful, especially when writing test cases to 
guarantee deterministic behavior.

<a name="json"></a>
## JSON Support

A `PasetoBuilder` will serialize the `Claims` and `FooterClaims` maps (and potentially any Java objects they 
contain) to JSON with a `Serializer<Map<String, ?>>` instance.  Similarly, a `PasetoParser` will 
deserialize JSON into the `Claims` and `FooterClaims` using a `Deserializer<Map<String, ?>>` instance.

If you don't explicitly configure a `PasetoBuilder`'s `Serializer` or a `PasetoParser`'s `Deserializer`, JPaseto will 
automatically attempt to discover and use the following JSON implementation if found in the runtime classpath.  

1. Jackson: This will automatically be used if you specify `dev.paseto:jpaseto-jackson` as a project runtime 
   dependency.  Jackson supports POJOs as claims with full marshaling/unmarshaling as necessary.

2. Gson: This will be used automatically if you specify `dev.paseto:jpaseto-gson` as a 
   project runtime dependency.

**If you want to use POJOs as claim values, use the `dev.paseto:jpaseto-jackson` dependency** (or implement your own
Serializer and Deserializer if desired).  **But beware**, Jackson will force a sizable (> 1 MB) dependency to an 
Android application thus increasing the app download size for mobile users.

<a name="json-custom"></a>
### Custom JSON Processor

If you don't want to use JPaseto's runtime dependency approach, or just want to customize how JSON serialization and 
deserialization works, you can implement the `Serializer` and `Deserializer` interfaces and specify instances of
them on the `PasetoBuilder` and `PasetoParser` respectively.  For example:

When creating a Paseto token:

```java
Serializer<Map<String, Object>> serializer = getMySerializer(); //implement me
Pasetos.V2.LOCAL.builder()
    .setSerializer(serializer)
    // ... etc ...
```

When reading a Paseto token:

```java
Deserializer<Map<String, Object>> deserializer = getMyDeserializer(); //implement me
Pasetos.parserBuilder()
    .setDeserializer(deserializer)
    // ... etc ...
```

<a name="json-jackson-custom-types"></a>
### Parsing of Custom Claim Types

By default JPaseto will only convert simple claim types: String, Instant, Date, Long, Integer, Short and Byte.  If you need to deserialize other types you can configure the `JacksonDeserializer` by passing a `Map` of claim names to types in through a constructor. For example:

```java
new JacksonDeserializer(Maps.of("user", User.class).build())
```

This would trigger the value in the `user` claim to be deserialized into the custom type of `User`.  Given the claims body of:

```json
{
    "issuer": "https://example.com/issuer",
    "user": {
      "firstName": "Jill",
      "lastName": "Coder"
    }
}
```

The `User` object could be retrieved from the `user` claim with the following code:

```java
Pasetos.parserBuilder()

    .setDeserializer(new JacksonDeserializer(Map.of("user", User.class))) // <-----
    .build()
    .parse(token)
    .getClaims()
    .get("user", User.class); // <-----
```

<a name="learn-more"></a>
## Learn More

- [Paseto RFC (draft)](https://paseto.io/rfc/)
- [Paseto.io](https://paseto.io/)

<a name="license"></a>
## License

This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).