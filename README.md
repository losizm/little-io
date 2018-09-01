# little-io &ndash; Scala library for java.io and java.nio

**little-io** is a Scala library that provides extension methods to _java.io_
and _java.nio_.

## Getting Started
To use **little-io**, add it as a dependency to your project:

* sbt
```scala
libraryDependencies += "losizm" %% "little-io" % "0.5.0"
```
* Gradle
```groovy
compile group: 'losizm', name: 'little-io_2.12', version: '0.5.0'
```
* Maven
```xml
<dependency>
  <groupId>losizm</groupId>
  <artifactId>little-io_2.12</artifactId>
  <version>0.5.0</version>
</dependency>
```

## A Taste of little-io

Here's a taste of what **little-io** has to offer.

### Getting and Setting File Content

If you have a reference to a `File`, you can set its content without the
boilerplate of opening and closing a `FileOutputStream` or `FileWriter`. And
getting its content is just as concise.

```scala
import java.io.File
// Adds methods to java.io.File
import little.io.Implicits.FileType

val file = new File("greeting.txt")

// Open writer to file, write text, and close writer
file.setText("Hello, world!")

// Open reader to file, read text, and close reader
println(file.getText()) // Hello, world!
```

Or you can create a file reference and set the file's content all in one swoop.

```scala
import java.io.File
import little.io.Implicits.FileType

// Append text to file and return reference to file
val file = new File("greeting.txt") << "Hello, world!"

// Open reader to file, read text, and close reader
println(file.getText()) // Hello, world!
```

The same applies to `java.nio.file.Path`.

```scala
import java.nio.file.Paths
// Adds methods to java.nio.file.Path
import little.io.Implicits.PathType

val path = Paths.get("greeting.txt") << "Hello, world!"
println(path.getText()) // Hello, world!
```

### Reading and Writing File Content

If you have a reference to a `File` or `Path`, you can open an `OutputStream` or
a `Writer` to the file to write file content, and you can open an `InputStream`
or a `Reader` to read its content, all with automatic resource management.

```scala
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE
// Adds methods to java.nio.file.Path and java.io.Writer
import little.io.Implicits.{ PathType, WriterType }

val file = Paths.get("numbers.txt")

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

Or if you'll be reading file content line by line, there's an even simpler way.

```scala
import java.nio.file.Paths
// Adds methods to java.nio.file.Path and java.io.Writer
import little.io.Implicits.PathType

val file = Paths.get("numbers.txt")

// Open file, print each line, and close file
file.forEachLine(line => println(line))
```

### Traversing File Directories

One feature available since Java 7 is builtin library support for walking a file
tree starting at a particular `Path`. This is accomplished by specifying a
`FileVisitor` as a callback to handle a set of events.

**little-io** makes this process a little more Scala-like. You make a method
call to an extension method of `Path`, passing in a `PartialFunction` to handle
the events you're interested in.

```scala
import java.nio.file.{ FileVisitResult, Paths }
// Import events corresponding to methods of java.nio.file.FileVisitor
import little.io.FileVisitEvent.{ PreVisitDirectory, VisitFile }
import little.io.Implicits.PathType

val sourceDir = Paths.get("src")

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

Another feature available since Java 7 is `java.nio.file.WatchService`, which
allows you to monitor a directory for changes. You can poll the service to check
for new, modified, and deleted files.

With pure Java, you create a `Path` to a directory, create a `WatchService`
using a reference to the path's `FileSystem`, and then register the path with
the service while specifying the kinds of `WatchEvent`s you wish to track. A
`WatchKey` is returned when the path is registered, which you then use to
poll for file events.

With **little-io**, it's concise and straight to the point.

```scala
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import little.io.Implicits.PathType

val dir = Paths.get(".")

// Specify events of interest and supply event handler
val handle = dir.watch(ENTRY_CREATE) { evt =>
  println(s"${evt.context} was created.")
}

// Close handle when finished
//handle.close()
```


## License
**little-io** is licensed under the Apache License, Version 2. See LICENSE
file for more information.
