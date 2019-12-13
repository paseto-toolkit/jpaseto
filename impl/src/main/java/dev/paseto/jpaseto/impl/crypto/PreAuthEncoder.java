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
package dev.paseto.jpaseto.impl.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class PreAuthEncoder {

    public static byte[] encode(byte[]... inputs) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(toLongLe(inputs.length));

            for (byte[] input : inputs) {
                stream.write(toLongLe(input.length));
                stream.write(input);
            }
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode preAuth", e);
        }
    }

//    public static byte[] encode(List<byte[]> inputs) {
//        try {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            stream.write(toLongLe(inputs.size()));
//
//            for (byte[] input : inputs) {
//                stream.write(toLongLe(input.length));
//                stream.write(input);
//            }
//            return stream.toByteArray();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to encode preAuth", e);
//        }
//    }

    private static byte[] toLongLe(long input) {
        long unsignedLong = input & Long.MAX_VALUE;
        ByteBuffer buffer = ByteBuffer.allocate(java.lang.Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(unsignedLong);
        return buffer.array();
    }

//    private byte[] byteValue(int input) {
//        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
//        buffer.putInt(input);
//        return buffer.array();
//    }
//
//    private byte[] byteValue(long input) {
//        ByteBuffer buffer = ByteBuffer.allocate(java.lang.Long.BYTES);
//        buffer.putLong(input);
//        return buffer.array();
//    }
}