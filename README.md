# little-io

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/little-io_3.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22little-io_3%22)

The Scala library that provides extension methods to _java.io_ and _java.nio_.

## Getting Started
To get started, add **little-io** as a dependency to your project:

```scala
libraryDependencies += "com.github.losizm" %% "little-io" % "6.0.0"
```

_**NOTE:** Starting with 5.0, **little-io** is written for Scala 3. See
previous releases for compatibility with Scala 2.12 and Scala 2.13._

## A Taste of little-io

Here's a taste of what **little-io** offers.

### Getting and Setting File Content

If you have a `File`, you can easily get and set its content.

```scala
import little.io.{ FileExt, StringExt }

val file = "greeting.txt".toFile

// Open writer to file, set content, and close writer
file.setText("Hello, world!")

// Open reader to file, get content, and close reader
println(file.getText()) // Hello, world!
```

And here's another way of going at it.

```scala
import little.io.{ FileExt, StringExt }

// Append text to file and return reference to file
val file = "greeting.txt".toFile << "Hello, world!"

println(file.getText())
```

The same applies to `java.nio.file.Path`.

```scala
import little.io.{ PathExt, StringExt }

val path = "greeting.txt".toPath << "Hello, world!"
println(path.getText())
```

And, if you prefer working with bytes, there are extension methods for those
too.

```scala
import little.io.{ FileExt, StringExt }

val file = "greeting.txt".toFile
val data = "Hello, world!".getBytes("utf-8")

file.setBytes(data)
file << "\n" << data.reverse

println(String(file.getBytes(), "utf-8"))
```

### Reading and Writing File Content

If you have a `File` or `Path`, you can open an `OutputStream` or `Writer` to
write its content, and you can open an `InputStream` or `Reader` to read its
content, all with automatic resource management.

```scala
import little.io.{ FileExt, StringExt, WriterExt }

val file = "numbers.txt".toFile

// Open file, write 3 lines of text, and close file
file.withWriter { out =>
  out write "One\n"

  // WriterExt adds writeLine to Writer
  out writeLine "Two"

  // WriterExt adds << to Writer
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
import little.io.{ FileExt, StringExt }

// Open file, print each line, and close file
"numbers.txt".toFile.forEachLine(println)
```

### Filtering, Mapping, and Folding Lines in File

There are other comprehension methods for processing files line by line. You can
filter and map the lines in a file to build a collection. Or you can fold them
to a single value.

```scala
import little.io.*

val file = "test.txt".toFile << "abc\n123\nxyz\n789"

// Filter lines with numbers only
val filtered = file.filterLines(_.matches("\\d+"))
assert { filtered == Seq("123", "789") }

// Map lines to uppercase
val mapped = file.mapLines(_.toUpperCase)
assert { mapped == Seq("ABC", "123", "XYZ", "789") }

// Fold lines to single, concatenated string
val folded = file.foldLines("") { _ + _ }
assert(folded == "abc123xyz789")
```

### Mapping and Folding Files in Directory

If you have a `File` or `Path` to a directory, you can map the files in the
directory. Or you can fold them to generate a single value.

```scala
import little.io.{ FileExt, StringExt }

val home = sys.props("user.home").toFile

// Get file names in home directory
val fileNames = home.mapFiles(_.getName)

// Total file sizes in home directory
val totalSize = home.foldFiles(0L) { _ + _.length }
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
import little.io.{ FileVisitEvent, StringExt, PathExt }
import FileVisitEvent.{ PreVisitDirectory, VisitFile }

val sourceDir = "src".toPath

// Traverse directories starting at 'src'
sourceDir.withVisitor {
  // Go deeper if not 'test' directory
  case PreVisitDirectory(dir, attrs) =>
    if dir.getFileName.toString == "test" then
      FileVisitResult.SKIP_SUBTREE
    else
      println(s"Listing files in ${dir.getFileName} directory...")
      FileVisitResult.CONTINUE

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
the service while specifying the kinds of `WatchEvent` you wish to track. A
`WatchKey` is returned when the path is registered, and you use this key to poll
for file events.

With **little-io**, it's straight to the point.

```scala
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import little.io.{ PathExt, StringExt }

val dir = "/tmp".toPath

// Print message when file is created
val handle = dir.withWatcher(ENTRY_CREATE) { evt =>
  println(s"${evt.context} was created.")
}

try
  Thread.sleep(60 * 1000)
finally
  handle.close() // Close handle when finished
```

### File Compression

The `Compressor` object provides various compression methods. For example, you
can compress a file using `gzip`.

```scala
import java.io.File
import little.io.BufferSize
import little.io.Compressor.gzip

// Specify buffer size for I/O operations
given BufferSize = BufferSize(1024)

// Specify input and output files
val in  = File("/path/to/file.txt")
val out = File("/path/to/file.txt.gz")

// Gzip input to output
gzip(in, out)
```

And decompress it with `gunzip`.

```scala
import java.io.File
import little.io.BufferSize
import little.io.Compressor.gunzip

// Specify buffer size for I/O operations
given BufferSize = BufferSize(1024)

// Specify input and output files
val in  = File("/path/to/file.txt.gz")
val out = File("/path/to/file.txt")

// Gunzip input to output
gunzip(in, out)
```

Or, to build an archive, you can `zip` a directory.

```scala
import java.io.{ File, FileFilter }
import little.io.Compressor.zip

// Specify input directory and output file
val in  = File("./src")
val out = File("/tmp/src.zip")

// Set file filter
given FileFilter = file =>
  file.isDirectory || file.getName.endsWith(".scala")

// Zip .scala files in all directories
zip(in, out)
```

And extract the files to a directory using `unzip`.

```scala
import java.io.File
import little.io.AcceptAnyFile
import little.io.Compressor.unzip

// Specify input file and output directory
val in  = File("/tmp/src.zip")
val out = File("/tmp/src")

// Unzip all files
unzip(in, out)(using AcceptAnyFile)
```

## API Documentation

See [scaladoc](https://losizm.github.io/little-io/latest/api/little/io.html)
for additional details.

## License
**little-io** is licensed under the Apache License, Version 2. See [LICENSE](LICENSE)
for more information.
