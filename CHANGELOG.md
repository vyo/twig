#   change log

This project tries to adhere to the [semantic versioning](http://semver.org/) and [change log](http://keepachangelog.com/) guidelines.

##  [Unreleased]

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