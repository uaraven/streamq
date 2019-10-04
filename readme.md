# StreamQ

Hack to really stream data from service layer into presentation layer of spring 
applications. 
Allows returning `Stream<T>` from repository and actually stream data into the response
of the web layer.

Problem is that Spring Data (or JPA) closes `Steam` returned from repository as
soon as transaction ends, so that stream must be consumed inside transaction. 
Theoretically transaction can be extended into the view layer, but for some reason that 
didn't quite work for me.

Hack consists in passing Stream elements from service into the controller using a blocking queue.
This way whole stream need not to be consumed into a list, so there is no excessive memory consumption.

There are some requirements:
 - Service must accept `Queue` as a parameter.
 - Service call must be made run in a separate thread, otherwise whole stream will be consumed into a queue before queue would be read in the controller
 - Controller will use `BlockingQueueIterator` to read data from queue. Normal iterators will terminate as soon as queue
   is empty, so to avoid this, `BlockingQueueIterator` relies on a marker object which should indicate that queue has 
   been read completely. 

Example controller returning HTTP Service-sent event stream may look like

```java
@RestController
@RequestMapping("/transactions")
public class DataStreamController {

    private final DataStreamService dss;
    private final TaskExecutor executor;

    @Autowired
    public TransactionListController(final DataStreamService service,
                                     final TaskExecutor executor) {
        this.dss = service;
        this.executor = executor;
    }

    @GetMapping(value = "/data", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    Flux<DataItem> getTransactions(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final OffsetDateTime start,
                                   @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final OffsetDateTime end) {
        final BlockingQueue<DataItem> dataQueue = new LinkedBlockingQueue<>();
        executor.execute(() -> dss.getData(dataQueue, start, end));
        return Flux.fromIterable(BlockingQueueIterable.of(dataQueue, DataItem.empty()));
    }

}
```
If you don't have async functionality already configured in your project you can just add `net.ninjacat.streamq.AsyncConfig` 
as a configuration. It will create `AsyncTaskExecutor` bean and register it for Web MVC async support.

Controller creates a queue `dataQueue` and passes it to `DataStreamService`. Call to service is made inside runnable
passed to executor, so that it will run in separate thread. After that controller sets up Flux from 
BlockingQueueIterable, which will start reading data from the queue as it is pushed into queue from service.

`DataItem.empty()` creates a **singleton** marker object that will indicate that stream has been depleted. 
`BlockingQueueIterator` uses reference comparison to check for the stream end, not `.equals()` method!

Corresponding service may look like below
```java
@Service
public class DataStreamService {

    private final DataRepository repo;

    @Autowired
    public DataStreamService(final DataRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public void getData(final Queue<DataItem> queue, final OffsetDateTime start, final OffsetDateTime end) {
        StreamQ.read(repo.getDataItems(start, end))
                .withMarker(DataItem.empty())
                .into(queue);
    }
}
```
Service reads stream of data from repository. `repo.getDataItems()` returns `Stream<DataItem>`. It sets up source stream
with `StreamQ.read(Stream<T>)`, then configures marker object to indicate end of stream with `.withMarker(T)` and 
finally instructs it to read elements from stream into queue with `.into(Queue<T>)`.