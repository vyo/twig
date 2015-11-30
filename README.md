twig
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

```kotlin
logger.info("dummy", Pair("my custom field", "my custom content"), Pair("my logger", logger))
```

```json
{"hostname":"vyo-pc","pid":6156,"thread":"Thread[main,5,]","time":"2015-11-30T19:09:58.507Z","level":30,"name":"io.github.vyo.twig.TestObject@16e898f","msg":"dummy","my custom field":"my custom content","my logger":"io.github.vyo.twig.logger.Logger@1a97992","v":0}
```

### Bunyan CLI

Piped into the bunyan cli the preceding log entry will be pretty-printed like this

```
[2015-11-30T19:09:58.507Z]  INFO: io.github.vyo.twig.TestObject@16e898f/6156 on vyo-pc: dummy (thread=Thread[main,5,], my custom field="my custom content", my logger=io.github.vyo.twig.logger.Logger@1a97992)
```

### Configuration

The configuration is logged on startup; by default the global log level will be INFO, the worker amount will be set to the system's available processors and the log queue size to 1024

```json
{"hostname":"vyo-pc","pid":1144,"thread":"Thread[main,5,]","time":"2015-11-30T19:36:28.220Z","level":30,"name":"twig","msg":"logging worker count: 4","v":0}
{"hostname":"vyo-pc","pid":1144,"thread":"Thread[main,5,]","time":"2015-11-30T19:36:28.220Z","level":30,"name":"twig","msg":"logging work queue size: 1024","v":0}
{"hostname":"vyo-pc","pid":1144,"thread":"Thread[main,5,]","time":"2015-11-30T19:36:28.220Z","level":30,"name":"twig","msg":"global log level: INFO","v":0}
```

Setting up the following environment variables in advance
```sh
TWIG_LEVEL=TRACE
TWIG_QUEUE=64
TWIG_WORKERS=1
```

```json
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"global log level TRACE","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"logging work queue size: 64","v":0}
{"hostname":"vyo-pc"","pid":6580,"thread":"Thread[main,5,]","time":"2015-11-30T19:38:45.037Z","level":30,"name":"twig","msg":"logging worker count: 1","v":0}
```

Re-assigning the global log level or appender will also be logged
```kotlin
Logger.global.appender = ConsoleAppender()
Logger.global.level = Level.WARN
```

```json
{"hostname":"vyo-pc"","pid":6364,"thread":"Thread[main,5,]","time":"2015-11-30T19:40:36.876Z","level":30,"name":"twig","msg":"global appender io.github.vyo.twig.appender.ConsoleAppender@1ae6ba4","v":0}
{"hostname":"vyo-pc"","pid":6364,"thread":"Thread[main,5,]","time":"2015-11-30T19:40:36.876Z","level":30,"name":"twig","msg":"global log level WARN","v":0}
```

### Exception Behaviour

If an exception occurs during the actual logging process, e.g. because the underlying Appender fails, we try to log a diagnostic entry to STDERR
```kotlin
logger.fatal("exceptional")
```
```json
{"hostname":"vyo-pc","pid":3092,"thread":"Thread[main,5,]","time":"2015-11-30T19:47:15.128Z","level":60"name":"io.github.vyo.twig.logger.Logger@50ea2a","msg":"logging failed: null","original level":30,"original name":"io.github.vyo.twig.TestObject@970b10","original message":"exceptional","v":0
```

##  Notes

This project adheres to the [semantic versioning](http://semver.org/) and [change log](http://keepachangelog.com/) guidelines.

##  Author

Manuel Weidmann (@vyo)

##  License

MIT License