# jpaseto-its-fips

This test module contains a set of tests for validating that jpaseto works with the Bouncy Castle
FIPS (Federal Information Processing Standard) library.  These tests require a separate module because the BC-FIPS
jar and regular BC jars cannot both be depended on in the same module 
([it causes a runtime error when adding the BC-FIPS provider](https://stackoverflow.com/questions/60930130/intellij-gradle-unable-to-find-method-org-bouncycastle-crypto-cryptoservicesre)).

Currently, we only have validation for the v1.public specification.

Note that the FIPS standard currently does not support the Ed25519 signature algorithm, so there are no tests here
to validate that v2.public tokens can be generated while using the BC-FIPS jar. When Ed25519 is added to the standard,
tests can be added here to validate that jpaseto operates properly in BC-FIPS approved mode.