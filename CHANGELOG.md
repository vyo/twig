change log
========================================
This project tries to adhere to the [semantic versioning](http://semver.org/) and [change log](http://keepachangelog.com/) guidelines.

#   v0.5.1
### Added
- minimal test specification via the [Spek](http://jetbrains.github.io/spek/) library

#   v0.5.0
### Fixed
- logger initialisation possible again (root logger initialisation competed with Logger instance initialisation)

### Added
- new interface LoggerInterface
### Changed
- Logger implements LoggerInterface
- root logger implements LoggerInterface instead of
   extending Logger
- root logger manually delegates calls to private logger

#   v0.4.1
### Added
- log configuration information on initialisation
- Disruptor support
    - queue size of 1024 (default)
    - worker thread counts equals number of available processors


#   v0.3.0
### Changed
- Logger.root object is now a Logger itself

#   v0.2.0
### Changed
- root log level no longer takes precedence

#   v0.1.2
### Changed
- newly created loggers now inherit the log level of the root logger

#  v0.1.1
### Changed
- default root log level change:
    - from DEBUG
    - to INFO

#   v0.1.0
### Added
- console logging
- JSON log entries
- global log level threshold
- per-logger log level threshold (root log level takes precedence)