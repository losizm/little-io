# little-io

The Scala library that provides extension methods to _java.io_ and _java.nio_.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/little-io_2.12.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22little-io_2.12%22)

## Getting Started
To use **little-io**, add it as a dependency to your project:

* sbt
```scala
libraryDependencies += "com.github.losizm" %% "little-io" % "2.5.0"
```
* Gradle
```groovy
compile group: 'com.github.losizm', name: 'little-io_2.12', version: '2.5.0'
```
* Maven
```xml
<dependency>
  <groupId>com.github.losizm</groupId>
  <artifactId>little-io_2.12</artifactId>
  <version>2.5.0</version>
</dependency>
```

## A Taste of little-io

Here's a taste of what **little-io** offers.

### Getting and Setting File Content

If you have a reference to a `File`, you can set its content without the
boilerplate of opening and closing a `FileOutputStream` or a `FileWriter`. And
getting file content is a breeze.

```scala
// Add methods to java.io.File and String
import little.io.Implicits.{ FileType, IoStringType }

val file = "greeting.txt".toFile

// Open writer to file, write text, and close writer
file.setText("Hello, world!")

// Open reader to file, read text, and close reader
println(file.getText()) // Hello, world!
```

You can even create a file reference and set the file content all in one swoop.

```scala
import little.io.Implicits.{ FileType, IoStringType }

// Append text to file and return reference to file
val file = "greeting.txt".toFile << "Hello, world!"

println(file.getText())
```

The same applies to `java.nio.file.Path`.

```scala
// Add methods to String and java.nio.file.Path
import little.io.Implicits.{ IoStringType, PathType }

val path = "greeting.txt".toPath << "Hello, world!"
println(path.getText())
```

And, if you prefer working with bytes, there are extensions for those too.

```scala
import little.io.Implicits.{ FileType, IoStringType }

val file = "greeting.txt".toFile
val data = "Hello, world!".getBytes("utf-8")

file.setBytes(data)
file << "\n" << data.reverse

println(new String(file.getBytes(), "utf-8"))
```

### Reading and Writing File Content

If you have a reference to a `File` or a `Path`, you can open an `OutputStream`
or a `Writer` to the file to write its content, and you can open an
`InputStream` or a `Reader` to read its content, all with automatic resource
management.

```scala
import java.nio.file.StandardOpenOption.CREATE
// Add methods to java.nio.file.Path and java.io.Writer
import little.io.Implicits.{ IoStringType, PathType, WriterType }

val file = "numbers.txt".toPath

// Open file, write 3 lines of text, and close file
file.withWriter(CREATE) { out =>
  out write "One\n"

  // WriterType adds writeLine to Writer
  out writeLine "Two"

  // WriterType adds << to Writer
  out << "Three\n"
}

// Open file, read 3 lines of text, and close file
file.withReader { in =>
  println(in.readLine()) // One
  println(in.readLine()) // Two
  println(in.readLine()) // Three
}
```

Or, if you'll be reading file content line by line, there's an even simpler way.

```scala
import little.io.Implicits.{ IoStringType, PathType }

// Open file, print each line, and close file
"numbers.txt".toPath.forEachLine(line => println(line))
```

### Traversing File Directories

A feature available since Java 7 is builtin library support for walking a file
tree starting at a particular `Path`. This is carried out by specifying a
`FileVisitor` as a callback to handle a set of events.

**little-io** makes this feature a little more Scala-like. You make a method
call to a `Path` extension method, passing in a `PartialFunction` to handle
events of interest.

```scala
import java.nio.file.FileVisitResult
// Import file events for walking file tree
import little.io.FileVisitEvent.{ PreVisitDirectory, VisitFile }
import little.io.Implicits.{ IoStringType, PathType }

val sourceDir = "src".toPath

// Traverse directories starting at 'src'
sourceDir.walkFileTree {
  // Go deeper if not 'test' directory
  case PreVisitDirectory(dir, attrs) =>
    if (dir.getFileName.toString == "test")
      FileVisitResult.SKIP_SUBTREE
    else {
      println(s"Listing files in ${dir.getFileName} directory...")
      FileVisitResult.CONTINUE
    }

  // Print file name and size
  case VisitFile(file, attrs) =>
    println(s"${file.getFileName} is ${attrs.size} bytes.")
    FileVisitResult.CONTINUE
}
```

### Watching File Events

Another feature available since Java 7 is `WatchService`, which allows you to
monitor a directory for changes. You can poll the service to check for new,
modified, and deleted files.

With pure Java, you create a `Path` to a directory, create a `WatchService`
using a reference to the path's `FileSystem`, and then register the path with
the service while specifying the kinds of `WatchEvent`s you wish to track. A
`WatchKey` is returned when the path is registered, and you use this key to poll
for file events.

With **little-io**, it's straight to the point.

```scala
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import little.io.Implicits.{ IoStringType, PathType }

val dir = ".".toPath

// Print message when file is created
val handle = dir.watch(ENTRY_CREATE) { evt =>
  println(s"${evt.context} was created.")
}

Thread.sleep(60 * 1000)

// Close handle when finished
handle.close()
```

## API Documentation

See [scaladoc](https://losizm.github.io/little-io/latest/api/little/io/index.html)
for additional details.

## License
**little-io** is licensed under the Apache License, Version 2. See LICENSE
file for more information.
