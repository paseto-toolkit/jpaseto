package dev.paseto.jpaseto.impl.lang;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class Bytes {
    private Bytes() {}

    public static byte[] concat(byte[]... inputs) {
        int size = Arrays.stream(inputs).mapToInt(it -> it.length).sum();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        Arrays.stream(inputs).forEach(buffer::put);
        return buffer.array();
    }
}
