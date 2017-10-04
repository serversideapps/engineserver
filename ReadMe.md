# Purpose

Engineserver lets you run chess engines as a web service.

# Installing prerequisites

As a prerequisite you need Java SE JDK ( Java Platform, Standard Edition, Java Development Kit ) so that you can compile and run Java programs ( [download Java SE, JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) ).

Then you need the [Scala langauge](https://www.scala-lang.org/) with the [Sbt command line build tool](http://www.scala-sbt.org/) so that you can build and run this project ( [download Scala with Sbt](https://www.scala-lang.org/download/) ).

It is recommended that during the whole installation process you should be connected to the internet. Also be patient, installing Sbt may take a while.

# Downloading, compiling and running the program

First download the zipped version of this repository and unzip it.

Make sure you are connected to the internet, then open a console winodw, cd into the newly unzipped repository and in the root directory of the repository ( in which the build.sbt file can be found ) type:

`sbt`

This will start the sbt build tool. Once the sbt console has opened, type:

`compile`

This will compile and build the program.

When this is ready, you can run the program by typing:

`run`

For later runs simply type in the console:

`sbt run`

# Configuration

When the program is running, open your browser and navigate it to:

`http://localhost:9000/`

To add an engine fill in the `name`, `path` and `config` fields then press `Add engine`.

The name should be a unique name by which you will recognize the engine. The path should be the absolute path of the engine. Config is optional, this can be used to issue a sequence of configuration commands to the engine on startup.

You can edit and delete existing engines.

# Using the server for in browser analysis

Visit

[Chessapp - Analysis](https://chessapp.cleverapps.io/analysis/none)

In the Presentation tab select the engine you want to use.

Press the Green button and the analysis will start. To stop press the Red button. To make the analyzed move, press the Blue button. To store the analysis result in the book press the Yellow button. To search among moves not stored yet press the Cyan button.

# API specification

See : https://github.com/serversideapps/engineserver/wiki/API-specification