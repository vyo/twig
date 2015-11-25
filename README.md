twig
========================================

minimal Kotlin logging inspired by node-bunyan

----------------------------------------

### features:
 - JSON formatted log entries
 - asynchronous output
 - console only

### example:

Having a TestObject
```kotlin
object TestObject {
    val logger: Logger = Logger(this)

    fun dummyFunction() {
        logger.info("dummy")
    }
}
```
and calling its dummyFunction will result in a log entry similar to
```json
{"thread":"main","time":"1448469913","level":"30","name":"TestObject","message":"dummy"}
```