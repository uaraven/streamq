package net.ninjacat.streamq;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;

public class StreamQTest {
    @Test
    public void testReadingQueue() {
        final IterableQ<String> queue = StreamQ.queueWithMarker("");

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> collected = queue.stream().collect(Collectors.toList());

        assertThat(collected, Matchers.hasItems("0", "1", "2", "3", "4"));
    }

    @Test
    public void testReadingQueueWithParallelStream() {
        final IterableQ<String> queue = StreamQ.queueWithMarker("");

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> collected = queue.parallelStream().collect(Collectors.toList());

        assertThat(collected, Matchers.hasItems("0", "1", "2", "3", "4"));
    }

    @Test
    public void testIteratingOver() {
        final IterableQ<String> queue = StreamQ.queueWithMarker("");

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> result = new ArrayList<>();
        for (String item : queue) {
            result.add(item);
        }

        assertThat(result, Matchers.hasItems("0", "1", "2", "3", "4"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAcceptingNullAsMarker() {
        StreamQ.queueWithMarker(null);
    }

    private void produceData(final Queue<String> dataQ) {
        final StreamProvider<String> provider = new StreamProvider<>(5, String::valueOf);
        StreamQ.read(provider.produce())
                .withMarker("")
                .into(dataQ);
    }


}