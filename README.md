Twig
========================================

Opinionated minimal logging inspired by and compatible with [node-bunyan](https://github.com/trentm/node-bunyan)

----------------------------------------

##  Features

 - JSON formatted, [bunyan](https://github.com/trentm/node-bunyan)-conformant log entries
 - asynchronous logging, powered by [Kovenant](http://kovenant.komponents.nl/) and [Disruptor](https://lmax-exchange.github.io/disruptor/)
 - console only by default
 - easily extendable and customisable via simple Appender interface
 - startup configuration via optional environment variables


### Basic

Having a TestObject
```kotlin
object TestObject {
    val logger: Logger = Logger(this)

    fun dummyFunction() {
        logger.info("dummy")
    }
}
```
and calling its ```dummyFunction``` will result in a log entry similar to
```json
{"hostname":"vyo-pc","pid":6484,"thread":"Thread[main,5,]","time":"2015-11-30T18:55:35.092Z","level":30,"name":"io.github.vyo.twig.TestObject@12aba81","msg":"dummy","v":0}
```

### Custom Fields

You may pass in an arbitrary number of ```Pair<String, Any>``` to create additional custom log entry fields

```
logger.info("dummy", Pair("my custom field", "my custom content"), Pair("my logger", logger))
```

```
{"hostname":"vyo-pc","pid":6156,"thread":"Thread[main,5,]","time":"2015-11-30T19:09:58.507Z","level":30,"name":"io.github.vyo.twig.TestObject@16e898f","msg":"dummy","my custom field":"my custom content","my logger":"io.github.vyo.twig.logger.Logger@1a97992","v":0}
```

### Auto-expanded Throwables

You may pass throwables, i.e. exceptions and errors, and they will be automatically expanded to both
a short message and an accompanying stacktrace.

Stacktraces will only be logged out at DEBUG level (configurable) or below. They will be put into a custom field
called '*stacktrace*' in the form of an array. 

```
val logger = Logger("twig")

try {
    Integer.parseInt("not an int")
} catch (e: Exception) {
    logger.error(e)
    logger.debug(e)
}
```
will lead to
```
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":50,"name":"twig","msg":"java.lang.NumberFormatException: For input string: \"not an int\"","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":20,"name":"twig","msg":"java.lang.NumberFormatException: For input string: \"not an int\"","stacktrace":["java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)","java.lang.Integer.parseInt(Integer.java:580)","java.lang.Integer.parseInt(Integer.java:615)"],"v":0}
```

### Bunyan CLI

Piped into the bunyan cli the preceding log entry will be pretty-printed like this

```
[2015-11-30T19:09:58.507Z]  INFO: io.github.vyo.twig.TestObject@16e898f/6156 on vyo-pc: dummy (thread=Thread[main,5,], my custom field="my custom content", my logger=io.github.vyo.twig.logger.Logger@1a97992)
```

### Configuration

__*Note*__: You may configure global options by referencing either ```Logger.*``` or ```Logger.global.*```.

The configuration is logged on startup; by default 
 - the global log level will be INFO, 
 - the worker amount will be set to the system's available processors,
 - the log queue size will be set to 1024,
 - throwables will be auto-expanded at DEBUG level or below,
 - and auto-expanded throwables will come with a stacktrace of 50 lines at most.

```
{"hostname":"vyo-pc","pid":1144,"thread":"Thread[main,5,]","time":"2015-11-30T19:36:28.220Z","level":30,"name":"twig","msg":"logging worker count: 4","v":0}
{"hostname":"vyo-pc","pid":1144,"thread":"Thread[main,5,]","time":"2015-11-30T19:36:28.220Z","level":30,"name":"twig","msg":"logging work queue size: 1024","v":0}
{"hostname":"vyo-pc","pid":1144,"thread":"Thread[main,5,]","time":"2015-11-30T19:36:28.220Z","level":30,"name":"twig","msg":"global log level: INFO","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"throwable expansion level: DEBUG","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"throwable expansion depth: 50","v":0}
```

Setting up the following environment variables in advance
```sh
TWIG_LEVEL=TRACE
TWIG_QUEUE=64
TWIG_WORKERS=1
TWIG_EXPANSION_LEVEL=TRACE
TWIG_EXPANSION_DEPTH=100
```

```
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"global log level TRACE","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"logging work queue size: 64","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"logging worker count: 1","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"throwable expansion level: TRACE","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"throwable expansion depth: 100","v":0}
```

Re-assigning the global log level or appender will also be logged
```
Logger.global.appender = ConsoleAppender()
Logger.global.level = Level.WARN
```

```
{"hostname":"vyo-pc"","pid":6364,"thread":"Thread[main,5,]","time":"2015-11-30T19:40:36.876Z","level":30,"name":"twig","msg":"global appender io.github.vyo.twig.appender.ConsoleAppender@1ae6ba4","v":0}
{"hostname":"vyo-pc"","pid":6364,"thread":"Thread[main,5,]","time":"2015-11-30T19:40:36.876Z","level":30,"name":"twig","msg":"global log level WARN","v":0}
```

You may also specify your own serialiser function if the built in JSON-serialiser does not fit your needs.

####Default (built in):
```
Logger.global.serialiser = Logger.global.simpleSerialiser
```
####Jodd:
```
val jodd = JsonSerializer()
Logger.global.serialiser = { any: Any -> jodd.serialize(any) }
```
####Jackson:
```
val jackson = ObjectMapper()
Logger.global.serialiser = { any: Any -> jackson.writeValueAsString(any) }
```
####Custom function:
```
fun customToJson( any: Any) : String {
    return any.toString()
}
Logger.global.serialiser = { any: Any -> customToJson(any) }

// or:

val lambdaJson = { any: Any -> any.toString() }
Logger.global.serialiser = { any: Any -> lambdaJson(any) }

// or:
Logger.global.serialiser = { any: Any -> any.toString() }
```
__*Note*__: You should ensure that your custom function produces valid JSON.
You may also want to have arrays and collections be automatically expanded,
especially when making use of **Twig**'s auto-expanded throwables.

__*Note*__: Google's **GSON** seems to be unable to handle cyclic references, making it unsuitable for **Twig** for the time being.


### Per-logger settings:

Log level, appender and serialiser can be set globally, as well as individually for each logger:
```
val loggerOne = Logger("one")
val loggerTwo = Logger("two")

loggerOne.level = Level.WARN
loggerTwo.level = Level.INFO

loggerOne.appender = FileAppender()
loggerTwo.appender = ConsoleAppender()

loggerOne.serialiser = Logger.simpleSerialiser
loggerTwo.serialiser = { any: Any -> any.toString() }

```

### Exception Behaviour

If an exception occurs during the actual logging process, e.g. because the underlying Appender fails, we try to log a diagnostic entry to STDERR
```
logger.fatal("exceptional")
```
```
{"hostname":"vyo-pc","pid":3092,"thread":"Thread[main,5,]","time":"2015-11-30T19:47:15.128Z","level":60"name":"io.github.vyo.twig.logger.Logger@50ea2a","msg":"logging failed: null","original level":30,"original name":"io.github.vyo.twig.TestObject@970b10","original message":"exceptional","v":0
```

##  Notes

This project adheres to the [semantic versioning](http://semver.org/) and [change log](http://keepachangelog.com/) guidelines.

##  Author

Manuel Weidmann (@vyo)

##  License

MIT License