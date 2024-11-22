package com.raquo.buildkit

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

object SourceDownloader {

  /** Downloads source file from `urlPattern(version)`
    * IF either of these conditions are true:
    *  - Contents of `versionFile` do not match `version`
    *  - `outputFile` does not currently exist
    * After downloading:
    *  - Writes `version` string into `versionFile`
    *  - Writes the downloaded file into `outputFile`
    * If succeeded, subsequent calls should not trigger any
    * downloads, unless you do something to trigger the
    * conditions above again.
    *
    * @throws Throwable in case of error.
    */
  def downloadVersionedFile(
    name: String,
    version: String,
    urlPattern: String => String,
    versionFile: File,
    outputFile: File,
    processOutput: String => String = identity
  ): Unit = {
    val versionFromFile = Try(FileUtils.read(versionFile.getAbsolutePath)).getOrElse("")

    if (version.isEmpty || version.exists(_.isWhitespace)) {
      Console.error(
        s"[$name $version] Version is empty or invalid.",
        andThrow = true
      )
    } else if (version == versionFromFile && outputFile.exists()) {
      Console.info(s"[$name $version] Version unchanged, and the downloaded file exists. Skipping download.")
    } else {
      val url = urlPattern(version)
      Console.info(s"[$name $version] Downloading sources from $url")

      Try(download(url)) match {
        case Success(content) =>
          try {
            FileUtils.write(
              path = outputFile.getAbsolutePath,
              content = processOutput(content)
            )
            FileUtils.write(
              path = versionFile.getAbsolutePath,
              content = version
            )
          } catch {
            case err: Throwable =>
              Console.error(s"[$name $version] Downloaded ok, but failed to write file: $err")
              throw err
          }
          Console.success(s"[$name $version] Downloaded and updated ${outputFile.getName}")
        case Failure(err) =>
          Console.error(s"[$name $version] Download failed. Source not updated: $err")
          throw err
      }
    }
  }

  /** @throws Exception in case of error */
  private def download(url: String): String = {
    val source = Source.fromURL(url)
    try {
      source.mkString
    } finally {
      source.close()
    }
  }
}

