#   change log

This project tries to adhere to the [semantic versioning](http://semver.org/) and [change log](http://keepachangelog.com/) guidelines.

##  [Unreleased]
### Changed
- Logger.root is not a logger itself anymore
- Logger.root internals are now hidden, excepting
    -   appender
    -   threshold

##  [v0.10.0] - 2015-11-29
### Added
- log calls now return ```Promise<Unit, Exception>``` instead of ```Unit```:
    -   log entry creation can now be waited on
    -   log failures can now be manually handled in addition to the default diagnostic logging to STDERR
### Changed
- error log entries will always be written to STDERR instead of the root logger's appender
### Fixed
- no log entries were created if no custom fields were passed in
- log error handling was disabled

##  [v0.9.0] - 2015-11-29
### Changed
- log message may now be of type Any
- log message may not be empty
- custom field usage changed:
    -   custom fields are not specified in advance anymore
    -   custom fields are now passed on as a variable number of Pair<String, Any> representing pairs of custom field name and custom field value, e.g. as ```log.info("my message", Pair("customField", "my custom content"))``` or ```log.info("product bought", Pair("price", 5.99))```

##  [v0.8.0] - 2015-11-29
### Added
- log entries are bunyan-parseable
- log entries have additional fields
    -   hostname
    -   pid
    -   v
### Changed
- loggers inherit the root logger's Appender by default, i.e. ConsoleAppender
- log entry elememts changed
    -   value of key 'time' was in milliseconds, is now ISO-formatted:  yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    -   key 'message' changed to 'msg'

##  [v0.7.0] - 2015-11-28
### Added
- allow using of custom Appenders

##  [v0.6.0] - 2015-11-28
### Added
- allow adding of custom fields on loggers
- log changes of root log level
### Changed
- package structure adjusted for upcoming Appender extension

##  [v0.5.2] - 2015-11-28
### Fixed
- formatting bug which caused log entries not to be valid JSON objects
### Added
- log root log level on Logger initialisation

##   [v0.5.1] - 2015-11-28
### Added
- minimal test specification via the [Spek](http://jetbrains.github.io/spek/) library

##   [v0.5.0] - 2015-11-28
### Fixed
- logger initialisation possible again (root logger initialisation competed with Logger instance initialisation)

### Added
- new interface LoggerInterface
### Changed
- Logger implements LoggerInterface
- root logger implements LoggerInterface instead of
   extending Logger
- root logger manually delegates calls to private logger

##   [v0.4.1] - 2015-11-28
### Added
- log configuration information on initialisation
- Disruptor support
    - queue size of 1024 (default)
    - worker thread counts equals number of available processors


##   [v0.3.0] - 2015-11-28
### Changed
- Logger.root object is now a Logger itself

##   [v0.2.0] - 2015-11-26
### Changed
- root log level no longer takes precedence

##   [v0.1.2] - 2015-11-26
### Changed
- newly created loggers now inherit the log level of the root logger

##  [v0.1.1] - 2015-11-26
### Changed
- default root log level change:
    - from DEBUG
    - to INFO

##   [v0.1.0] - 2015-11-25
### Added
- console logging
- JSON log entries
- global log level threshold
- per-logger log level threshold (root log level takes precedence)