
# Contributing Guide

## Development environment - draft

1. Eclipse - at least Luna (4.4.x) 
- Plugn-in Development Environment (PDE)
- Maven Integration for Eclipse (m2e)
- AspectJ Development Tools (ADJT)
- m2e connector for ADJT
- m2e connector for Tycho


## How to setup Redmine server for tests

Main goal is to ease plugin developers setup process of Redmine servers for tests - objectives:

* use technologies: VirtualBox, Vagrant, Puppet
* setup Redmine server with REST API enabled
* load with test data: sample users, projects, issues

Repository [https://github.com/ljader/redmine-mylyn-vagrant-boxes](https://github.com/ljader/redmine-mylyn-vagrant-boxes) contains scripts for Redmine test VMs.

TODO:

* figure out reliable static local IP assignment

### Redmine test VMs

**Important**: you must **MANUALLY** add IP to name mappings in hosts file!

* [http://vg-redmine26a.example.com/](https://github.com/ljader/redmine-mylyn-vagrant-boxes/tree/master/vg-redmine26.example.com)

## Performing a release

TODO Description based on [RoboVM Eclipse plugin - Performing a release](
https://github.com/robovm/robovm-eclipse/wiki/Performing-a-release)
