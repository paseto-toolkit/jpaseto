#!/usr/bin/env bash

echo "installing libsodium"
curl https://download.libsodium.org/libsodium/releases/libsodium-1.0.18.tar.gz | tar xvz -C ../
pushd ../libsodium-1.0.18
./configure
make
make install
popd
