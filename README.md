# codemodel-rifle

[![Build Status](https://travis-ci.org/FTSRG/codemodel-rifle.svg?branch=master)](https://travis-ci.org/FTSRG/codemodel-rifle)

Graph-based static analysis of ECMAScript 6 source code repositories.

## Getting started

### Testing with the reactive driver

Clone the following repository and publish it to the local Maven repository:

* https://github.com/szarnyasg/neo4j-reactive-driver (Gradle)

You can use these script for convenience:

```
scripts/get-reactive-driver.sh
```

### Testing with a server

To setup a Neo4j server, issue the following command:

```
scripts/init-neo4j.sh
```

## IDE support

The project is implemented in Java 8. We recommend [Eclipse](http://www.eclipse.org/downloads/eclipse-packages/) with the [Buildship Gradle plug-in](https://projects.eclipse.org/projects/tools.buildship) or [IntelliJ IDEA](https://www.jetbrains.com/idea/) for editing the code.

## License

All code in this repository is available under the [Eclipse Public License v1.0](http://www.eclipse.org/legal/epl-v10.html) and is supported by the MTA-BME Lendület Research Group on Cyber-Physical Systems.
