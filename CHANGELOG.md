# Changelog
All notable changes to this project will be documented in this file. For previous versions see CHANGES file

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

All non released changes should be in CHANGELOG_UNRELEASED.md file

---------

## [0.1.1] - 2026-04-24

### Added

- Added some alerts() in the inertial event detection in example/ app.

### Changed

- Rename the plugin to @situm/cordova-mrm-safety.
- Simplified the plugin npm integration step. Now INERTIAL_EVENTS_VERSION and INERTIAL_EVENTS_MAVEN_REPO are not required to specify when installing the plugin in a cordova app.

## [0.1.0] - 2026-04-23

Initial version of the cordova library that detects inertial events (taptaptap, fall, inactivity) and reports them to host app.
