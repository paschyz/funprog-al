package fr.esgi.al.funprog.utils

import scala.io.Source
import upickle.default._

final case class Config(
    inputPath: String,
    jsonPath: String,
    csvPath: String,
    yamlPath: String
)

object ConfigReader {
  private val configFilePath = "config.json"

  implicit val configReader: Reader[Config] = macroRW[Config]

  def getOutputFilePathYAML(): String = {
    val confFile = readConfig()
    confFile.yamlPath
  }

  def readConfig(): Config = {
    val source = Source.fromFile(configFilePath)(scala.io.Codec.UTF8)
    val fileContent =
      try source.mkString
      finally source.close()
    read[Config](fileContent)
  }
}
