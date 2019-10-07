/*
 * streamq: StreamQTest.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
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

package net.ninjacat.streamq;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class StreamQLinkedTest {

    /**
     * Not really a test, just sets up a base line
     */
    @Test
    public void shouldNotReadWholeStreamWithDefault() {
        final Queue<String> queue = new LinkedBlockingQueue<>();

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> collected = queue.stream().collect(Collectors.toList());
        assertThat(collected, not(hasItems("0", "1", "2", "3", "4")));
    }

    @Test
    public void shouldCreateLinkedQueue() {
        final MarkedQueue<String> queue = getQueue();

        assertThat(queue, Matchers.instanceOf(LinkedMarkedQueue.class));
    }

    @Test
    public void testReadingQueue() {
        final MarkedQueue<String> queue = getQueue();

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> collected = queue.stream().collect(Collectors.toList());

        assertThat(collected, Matchers.hasItems("0", "1", "2", "3", "4"));
    }

    @Test
    public void testReadingQueueWithParallelStream() {
        final MarkedQueue<String> queue = getQueue();

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> collected = queue.parallelStream().collect(Collectors.toList());

        assertThat(collected, Matchers.hasItems("0", "1", "2", "3", "4"));
    }

    @Test
    public void testIteratingOver() {
        final MarkedQueue<String> queue = getQueue();

        Executors.newCachedThreadPool().submit(() -> produceData(queue));

        final List<String> result = new ArrayList<>();
        for (final String item : queue) {
            result.add(item);
        }

        assertThat(result, Matchers.hasItems("0", "1", "2", "3", "4"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAcceptingNullAsMarker() {
        StreamQ.queueWithMarker(null);
    }

    private MarkedQueue<String> getQueue() {
        return MarkedQ.withMarker("").linked().build();
    }

    private void produceData(final Queue<String> dataQ) {
        final StreamProvider<String> provider = new StreamProvider<>(5, String::valueOf);
        StreamQ.read(provider.produce())
                .withMarker("")
                .into(dataQ);
    }

}