package net.ninjacat.streamq;

import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamProvider<T> {

    private final int size;
    private final IntFunction<T> supplier;

    public StreamProvider(final int size, final IntFunction<T> supplier) {
        this.size = size;
        this.supplier = supplier;
    }

    public Stream<T> produce() {
        return IntStream.range(0, size).mapToObj(supplier);
    }
}
