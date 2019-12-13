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