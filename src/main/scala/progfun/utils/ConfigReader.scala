import scala.util.{Success, Try}

final case class Config(
    inputFile: String,
    outputJsonFile: String,
    outputCsvFile: String,
    outputYamlFile: String
)

object ConfigReader {
  // NOTE: ON RETOURNE UNE CONFIG PAR DEFAUT PCQ ON NE PEUT PAS THROW ?????
  private val defaultConfig = Config(
    inputFile = "/tmp/input.txt",
    outputJsonFile = "/tmp/output.json",
    outputCsvFile = "/tmp/output.csv",
    outputYamlFile = "/tmp/output.yaml"
  )

  def readConfig(configPath: String): Try[Config] = {
    println(
      s"Returning default configuration instead of reading from $configPath"
    )
    Success(defaultConfig)
  }
}
