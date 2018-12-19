# Changelog for ArchiveDiffer-Kotlin

## Unreleased changes

* Can't handle nested multi-volume yet

## 0.1.0.2  -- 2018/12/19

### Added

* itemID for ArchiveSet

### Fixed
* Fix bug that labeling wrong parentArchiveSetID and itemID on existence of theItemTable

## 0.1.0.1  -- 2018/12/19

### Fixed
* Fix bug with IgnoringList.match
* Close ArchiveSets after analyzing - was implemented in tested code
* Fix some error messages - was indicate wrong function

## 0.1.0.0  -- 2018/12/19

First release

* Could compare every files in each archive set
  * Even in nested archive
* Could handle executable archive
* Could open/compare multi-volume archive
* Items on IgnoringList will be ignored when comparing
