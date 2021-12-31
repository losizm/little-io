pages = [{"l":"index.html","n":"little-io","t":"little-io","d":"","k":"static"},
{"l":"little/io.html","n":"little.io","t":"package little.io","d":"little/io","k":"package"},
{"l":"little/io.html","n":"ByteArrayMethods","t":"def ByteArrayMethods(bytes: Array[Byte]): ByteArrayMethods","d":"little/io","k":"def"},
{"l":"little/io.html","n":"FileMethods","t":"def FileMethods(file: File): FileMethods","d":"little/io","k":"def"},
{"l":"little/io.html","n":"InputStreamMethods","t":"def InputStreamMethods[T <: InputStream](in: T): InputStreamMethods[T]","d":"little/io","k":"def"},
{"l":"little/io.html","n":"IoStringMethods","t":"def IoStringMethods(s: String): IoStringMethods","d":"little/io","k":"def"},
{"l":"little/io.html","n":"OutputStreamMethods","t":"def OutputStreamMethods[T <: OutputStream](out: T): OutputStreamMethods[T]","d":"little/io","k":"def"},
{"l":"little/io.html","n":"PathMethods","t":"def PathMethods(path: Path): PathMethods","d":"little/io","k":"def"},
{"l":"little/io.html","n":"ReaderMethods","t":"def ReaderMethods[T <: Reader](reader: T): ReaderMethods[T]","d":"little/io","k":"def"},
{"l":"little/io.html","n":"WriterMethods","t":"def WriterMethods[T <: Writer](writer: T): WriterMethods[T]","d":"little/io","k":"def"},
{"l":"little/io.html","n":"bufferSize","t":"val bufferSize: BufferSize","d":"little/io","k":"val"},
{"l":"little/io/AcceptAnyFile$.html","n":"AcceptAnyFile","t":"object AcceptAnyFile extends FileFilter","d":"little/io/AcceptAnyFile$","k":"object"},
{"l":"little/io/AcceptAnyFile$.html","n":"accept","t":"def accept(file: File): Boolean","d":"little/io/AcceptAnyFile$","k":"def"},
{"l":"little/io/BufferSize.html","n":"BufferSize","t":"class BufferSize(value: Int)","d":"little/io/BufferSize","k":"class"},
{"l":"little/io/ByteArrayMethods.html","n":"ByteArrayMethods","t":"class ByteArrayMethods(bytes: Array[Byte]) extends AnyVal","d":"little/io/ByteArrayMethods","k":"class"},
{"l":"little/io/ByteArrayMethods.html","n":"toBase64Decoded","t":"def toBase64Decoded: Array[Byte]","d":"little/io/ByteArrayMethods","k":"def"},
{"l":"little/io/ByteArrayMethods.html","n":"toBase64Encoded","t":"def toBase64Encoded: Array[Byte]","d":"little/io/ByteArrayMethods","k":"def"},
{"l":"little/io/Compressor$.html","n":"Compressor","t":"object Compressor","d":"little/io/Compressor$","k":"object"},
{"l":"little/io/Compressor$.html","n":"deflate","t":"def deflate(in: File, out: File)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"deflate","t":"def deflate(in: Path, out: Path)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"deflate","t":"def deflate(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"gunzip","t":"def gunzip(in: File, out: File)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"gunzip","t":"def gunzip(in: Path, out: Path)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"gunzip","t":"def gunzip(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"gzip","t":"def gzip(in: File, out: File)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"gzip","t":"def gzip(in: Path, out: Path)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"gzip","t":"def gzip(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"inflate","t":"def inflate(in: File, out: File)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"inflate","t":"def inflate(in: Path, out: Path)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"inflate","t":"def inflate(in: InputStream, out: OutputStream)(using bufferSize: BufferSize): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"unzip","t":"def unzip(in: File, out: File)(using filter: FileFilter): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"unzip","t":"def unzip(in: Path, out: Path)(using matcher: PathMatcher): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"zip","t":"def zip(in: File, out: File)(using filter: FileFilter): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/Compressor$.html","n":"zip","t":"def zip(in: Path, out: Path)(using matcher: PathMatcher): Unit","d":"little/io/Compressor$","k":"def"},
{"l":"little/io/FileMethods.html","n":"FileMethods","t":"class FileMethods(file: File) extends AnyVal","d":"little/io/FileMethods","k":"class"},
{"l":"little/io/FileMethods.html","n":"/","t":"def /(child: String): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"<<","t":"def <<(bytes: Array[Byte]): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"<<","t":"def <<(chars: Array[Char]): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"<<","t":"def <<(chars: CharSequence): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"<<","t":"def <<(in: InputStream)(using bufferSize: BufferSize): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"<<","t":"def <<(in: Reader)(using bufferSize: BufferSize): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"<<","t":"def <<(source: File): File","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"filterLines","t":"def filterLines(p: String => Boolean): Seq[String]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"flatMapFiles","t":"def flatMapFiles[T](f: File => Iterable[T]): Seq[T]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"flatMapLines","t":"def flatMapLines[T](f: String => Iterable[T]): Seq[T]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"foldFiles","t":"def foldFiles[T](init: T)(op: (T, File) => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"foldLines","t":"def foldLines[T](init: T)(op: (T, String) => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"forEachFile","t":"def forEachFile(f: File => Unit): Unit","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"forEachLine","t":"def forEachLine(f: String => Unit): Unit","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"getBytes","t":"def getBytes(): Array[Byte]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"getLines","t":"def getLines(): Seq[String]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"getText","t":"def getText(): String","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"mapFiles","t":"def mapFiles[T](f: File => T): Seq[T]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"mapLines","t":"def mapLines[T](f: String => T): Seq[T]","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"setBytes","t":"def setBytes(bytes: Array[Byte]): Unit","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"setText","t":"def setText(text: String): Unit","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withInputStream","t":"def withInputStream[T](f: InputStream => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withOutputStream","t":"def withOutputStream[T](f: OutputStream => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withOutputStream","t":"def withOutputStream[T](append: Boolean)(f: OutputStream => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withPrintWriter","t":"def withPrintWriter[T](f: PrintWriter => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withPrintWriter","t":"def withPrintWriter[T](append: Boolean)(f: PrintWriter => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withRandomAccess","t":"def withRandomAccess[T](mode: String)(f: RandomAccessFile => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withReader","t":"def withReader[T](f: BufferedReader => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withWriter","t":"def withWriter[T](f: BufferedWriter => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileMethods.html","n":"withWriter","t":"def withWriter[T](append: Boolean)(f: BufferedWriter => T): T","d":"little/io/FileMethods","k":"def"},
{"l":"little/io/FileVisitEvent.html","n":"FileVisitEvent","t":"enum FileVisitEvent","d":"little/io/FileVisitEvent","k":"enum"},
{"l":"little/io/FileVisitEvent$$PreVisitDirectory.html","n":"PreVisitDirectory","t":"case PreVisitDirectory(directory: Path, attributes: BasicFileAttributes) extends FileVisitEvent","d":"little/io/FileVisitEvent","k":"case"},
{"l":"little/io/FileVisitEvent$$PostVisitDirectory.html","n":"PostVisitDirectory","t":"case PostVisitDirectory(directory: Path, exception: Option[IOException]) extends FileVisitEvent","d":"little/io/FileVisitEvent","k":"case"},
{"l":"little/io/FileVisitEvent$$VisitFile.html","n":"VisitFile","t":"case VisitFile(file: Path, attributes: BasicFileAttributes) extends FileVisitEvent","d":"little/io/FileVisitEvent","k":"case"},
{"l":"little/io/FileVisitEvent$$VisitFileFailed.html","n":"VisitFileFailed","t":"case VisitFileFailed(file: Path, exception: IOException) extends FileVisitEvent","d":"little/io/FileVisitEvent","k":"case"},
{"l":"little/io/FileVisitEvent$$PreVisitDirectory.html","n":"PreVisitDirectory","t":"case PreVisitDirectory(directory: Path, attributes: BasicFileAttributes) extends FileVisitEvent","d":"little/io/FileVisitEvent$$PreVisitDirectory","k":"case"},
{"l":"little/io/FileVisitEvent$$PostVisitDirectory.html","n":"PostVisitDirectory","t":"case PostVisitDirectory(directory: Path, exception: Option[IOException]) extends FileVisitEvent","d":"little/io/FileVisitEvent$$PostVisitDirectory","k":"case"},
{"l":"little/io/FileVisitEvent$$VisitFile.html","n":"VisitFile","t":"case VisitFile(file: Path, attributes: BasicFileAttributes) extends FileVisitEvent","d":"little/io/FileVisitEvent$$VisitFile","k":"case"},
{"l":"little/io/FileVisitEvent$$VisitFileFailed.html","n":"VisitFileFailed","t":"case VisitFileFailed(file: Path, exception: IOException) extends FileVisitEvent","d":"little/io/FileVisitEvent$$VisitFileFailed","k":"case"},
{"l":"little/io/InputStreamMethods.html","n":"InputStreamMethods","t":"class InputStreamMethods[T <: InputStream](in: T) extends AnyVal","d":"little/io/InputStreamMethods","k":"class"},
{"l":"little/io/InputStreamMethods.html","n":"getBytes","t":"def getBytes(): Array[Byte]","d":"little/io/InputStreamMethods","k":"def"},
{"l":"little/io/IoStringMethods.html","n":"IoStringMethods","t":"class IoStringMethods(s: String) extends AnyVal","d":"little/io/IoStringMethods","k":"class"},
{"l":"little/io/IoStringMethods.html","n":"toFile","t":"def toFile: File","d":"little/io/IoStringMethods","k":"def"},
{"l":"little/io/IoStringMethods.html","n":"toPath","t":"def toPath: Path","d":"little/io/IoStringMethods","k":"def"},
{"l":"little/io/IoStringMethods.html","n":"toUrlDecoded","t":"def toUrlDecoded: String","d":"little/io/IoStringMethods","k":"def"},
{"l":"little/io/IoStringMethods.html","n":"toUrlDecoded","t":"def toUrlDecoded(charset: String): String","d":"little/io/IoStringMethods","k":"def"},
{"l":"little/io/IoStringMethods.html","n":"toUrlEncoded","t":"def toUrlEncoded: String","d":"little/io/IoStringMethods","k":"def"},
{"l":"little/io/IoStringMethods.html","n":"toUrlEncoded","t":"def toUrlEncoded(charset: String): String","d":"little/io/IoStringMethods","k":"def"},
{"l":"little/io/MatchAnyPath$.html","n":"MatchAnyPath","t":"object MatchAnyPath extends PathMatcher","d":"little/io/MatchAnyPath$","k":"object"},
{"l":"little/io/MatchAnyPath$.html","n":"matches","t":"def matches(path: Path): Boolean","d":"little/io/MatchAnyPath$","k":"def"},
{"l":"little/io/OutputStreamMethods.html","n":"OutputStreamMethods","t":"class OutputStreamMethods[T <: OutputStream](out: T) extends AnyVal","d":"little/io/OutputStreamMethods","k":"class"},
{"l":"little/io/OutputStreamMethods.html","n":"<<","t":"def <<(bytes: Array[Byte]): T","d":"little/io/OutputStreamMethods","k":"def"},
{"l":"little/io/OutputStreamMethods.html","n":"<<","t":"def <<(in: InputStream)(using bufferSize: BufferSize): T","d":"little/io/OutputStreamMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"PathMethods","t":"class PathMethods(path: Path) extends AnyVal","d":"little/io/PathMethods","k":"class"},
{"l":"little/io/PathMethods.html","n":"/","t":"def /(child: String): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"<<","t":"def <<(bytes: Array[Byte]): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"<<","t":"def <<(chars: Array[Char]): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"<<","t":"def <<(chars: CharSequence): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"<<","t":"def <<(in: InputStream)(using bufferSize: BufferSize): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"<<","t":"def <<(in: Reader)(using bufferSize: BufferSize): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"<<","t":"def <<(source: Path): Path","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"filterLines","t":"def filterLines(p: String => Boolean): Seq[String]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"flatMapFiles","t":"def flatMapFiles[T](f: Path => Iterable[T]): Seq[T]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"flatMapLines","t":"def flatMapLines[T](f: String => Iterable[T]): Seq[T]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"foldFiles","t":"def foldFiles[T](init: T)(op: (T, Path) => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"foldLines","t":"def foldLines[T](init: T)(op: (T, String) => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"forEachFile","t":"def forEachFile(f: Path => Unit): Unit","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"forEachLine","t":"def forEachLine(f: String => Unit): Unit","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"forFiles","t":"def forFiles(glob: String)(f: Path => Unit): Unit","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"getBytes","t":"def getBytes(): Array[Byte]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"getLines","t":"def getLines(): Seq[String]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"getText","t":"def getText(): String","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"mapFiles","t":"def mapFiles[T](f: Path => T): Seq[T]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"mapLines","t":"def mapLines[T](f: String => T): Seq[T]","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"setBytes","t":"def setBytes(bytes: Array[Byte]): Unit","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"setText","t":"def setText(text: String): Unit","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withChannel","t":"def withChannel[T](options: OpenOption*)(f: FileChannel => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withInputStream","t":"def withInputStream[T](options: OpenOption*)(f: InputStream => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withOutputStream","t":"def withOutputStream[T](options: OpenOption*)(f: OutputStream => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withPrintWriter","t":"def withPrintWriter[T](options: OpenOption*)(f: PrintWriter => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withReader","t":"def withReader[T](options: OpenOption*)(f: BufferedReader => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withVisitor","t":"def withVisitor(visitor: PartialFunction[FileVisitEvent, FileVisitResult]): Unit","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withWatcher","t":"def withWatcher(events: Kind[_]*)(watcher: WatchEvent[_] => Unit): WatchHandle","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/PathMethods.html","n":"withWriter","t":"def withWriter[T](options: OpenOption*)(f: BufferedWriter => T): T","d":"little/io/PathMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"ReaderMethods","t":"class ReaderMethods[T <: Reader](reader: T) extends AnyVal","d":"little/io/ReaderMethods","k":"class"},
{"l":"little/io/ReaderMethods.html","n":"filterLines","t":"def filterLines(p: String => Boolean): Seq[String]","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"flatMapLines","t":"def flatMapLines[T](f: String => Iterable[T]): Seq[T]","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"foldLines","t":"def foldLines[T](init: T)(op: (T, String) => T): T","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"forEachLine","t":"def forEachLine(f: String => Unit): Unit","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"getLines","t":"def getLines(): Seq[String]","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"getText","t":"def getText(): String","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/ReaderMethods.html","n":"mapLines","t":"def mapLines[T](f: String => T): Seq[T]","d":"little/io/ReaderMethods","k":"def"},
{"l":"little/io/WatchHandle.html","n":"WatchHandle","t":"class WatchHandle","d":"little/io/WatchHandle","k":"class"},
{"l":"little/io/WatchHandle.html","n":"close","t":"def close(): Unit","d":"little/io/WatchHandle","k":"def"},
{"l":"little/io/WatchHandle.html","n":"isClosed","t":"def isClosed: Boolean","d":"little/io/WatchHandle","k":"def"},
{"l":"little/io/WriterMethods.html","n":"WriterMethods","t":"class WriterMethods[T <: Writer](writer: T) extends AnyVal","d":"little/io/WriterMethods","k":"class"},
{"l":"little/io/WriterMethods.html","n":"<<","t":"def <<(chars: Array[Char]): T","d":"little/io/WriterMethods","k":"def"},
{"l":"little/io/WriterMethods.html","n":"<<","t":"def <<(chars: CharSequence): T","d":"little/io/WriterMethods","k":"def"},
{"l":"little/io/WriterMethods.html","n":"<<","t":"def <<(in: Reader)(using bufferSize: BufferSize): T","d":"little/io/WriterMethods","k":"def"},
{"l":"little/io/WriterMethods.html","n":"writeLine","t":"def writeLine(text: String): Unit","d":"little/io/WriterMethods","k":"def"},
{"l":"docs/index.html","n":"little-io","t":"little-io","d":"","k":"static"}];