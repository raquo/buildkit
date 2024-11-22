package com.raquo.buildkit

import java.io.{File, FileOutputStream, PrintStream}
import java.nio.file.{Files, Paths}

object FileUtils {

  /** @throws Throwable in case of error */
  def read(path: String): String = {
    val file = new File(path);
    if (file.exists()) {
      if (file.isFile) {
        val cachedKeyLines = Files.readAllLines(Paths.get(path))
        cachedKeyLines.toArray.mkString("\n")
      } else {
        throw new Exception(s"Can't read file `${path}` – it's not a file (must be a directory?)")
      }
    } else {
      ""
    }
  }

  /** @throws Throwable in case of error */
  def write(path: String, content: String): File = {
    val outputFile = new File(path)
    outputFile.getParentFile.mkdirs()

    val fileOutputStream = new FileOutputStream(outputFile)
    val outputPrintStream = new PrintStream(fileOutputStream)

    outputPrintStream.print(content)
    outputPrintStream.flush()

    // Flush written file contents to disk https://stackoverflow.com/a/4072895/2601788
    fileOutputStream.flush()
    fileOutputStream.getFD.sync()

    outputPrintStream.close()

    outputFile
  }
}
