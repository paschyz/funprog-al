package fr.esgi.al.funprog

import fr.esgi.al.funprog.instruction.Instruction
import fr.esgi.al.funprog.models._
import fr.esgi.al.funprog.filewriter.FileWrite
import fr.esgi.al.funprog.utils.ConfigReader
import java.io.{File, FileWriter}

@main
def Main(): Unit = {
  val config = ConfigReader.readConfig()

  val lawnLimit = LawnLimit(5, 5)
  val mowers = List(
    Mower(Point(1, 2), 'N', List('G', 'A', 'G', 'A', 'G', 'A', 'G', 'A', 'A')),
    Mower(
      Point(3, 3),
      'E',
      List('A', 'A', 'D', 'A', 'A', 'D', 'A', 'D', 'D', 'A')
    )
  )

  val finalStates = mowers.map(Instruction.processInstructions(_, lawnLimit))

  finalStates.foreach { state =>
    println(
      s"Final position: (${state.position.x}, ${state.position.y}), Direction: ${state.direction}"
    )
  }

  val json = FileWrite.saveToJson(lawnLimit, mowers, finalStates)
  val jsonFileWriter = new FileWriter(new File(config.jsonPath))
  jsonFileWriter.write(json)
  jsonFileWriter.close()
  println(s"JSON output written to ${config.jsonPath}")

  val yaml = FileWrite.saveToYaml(lawnLimit, mowers, finalStates)
  val yamlFileWriter = new FileWriter(new File(config.yamlPath))
  yamlFileWriter.write(yaml)
  yamlFileWriter.close()
  println(s"YAML output written to ${config.yamlPath}")

  val csv = FileWrite.saveToCsv(lawnLimit, mowers, finalStates)
  val csvFileWriter = new FileWriter(new File(config.csvPath))
  csvFileWriter.write(csv)
  csvFileWriter.close()
  println(s"CSV output written to ${config.csvPath}")
}
