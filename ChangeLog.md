# Changelog for ArchiveDiffer-Kotlin

## Unreleased changes

## 0.3.0.0  -- 2018/12/23

### Changed
* Add more indicators and Redesign GUI
* Use simpleString for `differenceLabel`
* Rename `rawFileAnalyze` as `filePathAnalyze` and returns more simple result

## 0.2.4.0  -- 2018/12/22

### Fixed
* Exclude CAB archive as ArchiveSet when testing executable file is an archive or not
* Add missing async/await for `makeTheTable`
* Remove `openArchive` from `rawFileAnalyze`

## 0.2.3.0  -- 2018/12/22

### Added
* Threading opening process
* Treat 7z
* Add manifest for building executable jar

## 0.2.2.0  -- 2018/12/21

### Added
* Show progress message for runOnce phases

### Fixed
* UI is freezing mo more: Introducing coroutines

## 0.2.1.0  -- 2018/12/20

### Fixed
* Implement `queryInsensitive`
* Printing miss of `ItemRecord.toString()` when treating `isEXtracted`
* When opening an ArchiveSet which is contained by more then one ArchiveSet,
  fail to marking on existence
* Typo: existance -> existence

## 0.2.0.1  -- 2018/12/19

### Fixed
* Fix bug when opening exe type archive

## 0.2.0.0  -- 2018/12/19

### Added
* Could extract/test with nested multi-volume archive

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
