package fr.esgi.al.funprog

import fr.esgi.al.funprog.direction.Direction
import fr.esgi.al.funprog.models._
import fr.esgi.al.funprog.utils.ConfigReader
import scala.annotation.tailrec
import java.io.{File, FileWriter}

@main
def Main(): Unit = {
  def processInstructions(mower: Mower, lawnLimit: LawnLimit): MowerState = {
    def executeInstruction(state: MowerState, instruction: Char): MowerState = {
      instruction match {
        case 'A' =>
          val newPosition =
            Direction.moveForward(state.position, state.direction)
          if (
            newPosition.x < 0 || newPosition.x > lawnLimit.xMax || newPosition.y < 0 || newPosition.y > lawnLimit.yMax
          ) {
            state
          } else {
            MowerState(newPosition, state.direction)
          }
        case 'D' =>
          MowerState(state.position, Direction.turnRight(state.direction))
        case 'G' =>
          MowerState(state.position, Direction.turnLeft(state.direction))
        case _ =>
          println(s"Unknown instruction: $instruction")
          state
      }
    }

    mower.instructions.foldLeft(MowerState(mower.start, mower.direction))(
      executeInstruction
    )
  }

  def saveToJson(
      lawnLimit: LawnLimit,
      mowers: List[Mower],
      finalStates: List[MowerState]
  ): String = {
    @tailrec
    def saveToJsonRec(
        lawnLimit: LawnLimit,
        mowers: List[Mower],
        finalStates: List[MowerState],
        json: String
    ): String = {
      mowers match {
        case Nil => json
        case mower :: tail =>
          val mowerState = finalStates.headOption.getOrElse(
            MowerState(mower.start, mower.direction)
          )
          val mowerJson = s"""
            {
              "debut": {
                "point": {
                  "x": ${mower.start.x},
                  "y": ${mower.start.y}
                },
                "direction": "${mower.direction}"
              },
              "instructions": ${mower.instructions.mkString(
              "[\"",
              "\", \"",
              "\"]"
            )},
              "fin": {
                "point": {
                  "x": ${mowerState.position.x},
                  "y": ${mowerState.position.y}
                },
                "direction": "${mowerState.direction}"
              }
            }
          """
          val newJson = if (json.isEmpty) mowerJson else s"$json,$mowerJson"
          saveToJsonRec(lawnLimit, tail, finalStates.drop(1), newJson)
      }
    }

    val mowersJson = saveToJsonRec(lawnLimit, mowers, finalStates, "")
    s"""
      {
        "limite": {
          "x": ${lawnLimit.xMax},
          "y": ${lawnLimit.yMax}
        },
        "tondeuses": [$mowersJson]
      }
    """
  }

  def saveToYaml(
      lawnLimit: LawnLimit,
      mowers: List[Mower],
      finalStates: List[MowerState]
  ): String = {
    @tailrec
    def saveToYamlRec(
        lawnLimit: LawnLimit,
        mowers: List[Mower],
        finalStates: List[MowerState],
        yaml: String
    ): String = {
      mowers match {
        case Nil => yaml
        case mower :: tail =>
          val mowerState = finalStates.headOption.getOrElse(
            MowerState(mower.start, mower.direction)
          )
          val mowerYaml = s"""
            - debut:
                point:
                  x: ${mower.start.x}
                  y: ${mower.start.y}
                direction: ${mower.direction}
              instructions: ${mower.instructions.mkString("[", ", ", "]")}
              fin:
                point:
                  x: ${mowerState.position.x}
                  y: ${mowerState.position.y}
                direction: ${mowerState.direction}
          """
          val newYaml = if (yaml.isEmpty) mowerYaml else s"$yaml$mowerYaml"
          saveToYamlRec(lawnLimit, tail, finalStates.drop(1), newYaml)
      }
    }

    val mowersYaml = saveToYamlRec(lawnLimit, mowers, finalStates, "")
    s"""
      limite:
        x: ${lawnLimit.xMax}
        y: ${lawnLimit.yMax}
      tondeuses:
        $mowersYaml
    """
  }

  def saveToCsv(
      lawnLimit: LawnLimit,
      mowers: List[Mower],
      finalStates: List[MowerState]
  ): String = {
    val header =
      "numéro;début_x;début_y;début_direction;fin_x;fin_y;fin_direction;instructions\n"
    val csvContent = mowers
      .zip(finalStates)
      .zipWithIndex
      .map { case ((mower, state), index) =>
        s"${index + 1};${mower.start.x};${mower.start.y};${mower.direction};${state.position.x};${state.position.y};${state.direction};${mower.instructions.mkString}"
      }
      .mkString("\n")
    header + csvContent
  }

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

  val finalStates = mowers.map(processInstructions(_, lawnLimit))

  finalStates.foreach { state =>
    println(
      s"Final position: (${state.position.x}, ${state.position.y}), Direction: ${state.direction}"
    )
  }

  val json = saveToJson(lawnLimit, mowers, finalStates)
  val jsonFileWriter = new FileWriter(new File(config.jsonPath))
  jsonFileWriter.write(json)
  jsonFileWriter.close()
  println(s"JSON output written to ${config.jsonPath}")

  val yaml = saveToYaml(lawnLimit, mowers, finalStates)
  val yamlFileWriter = new FileWriter(new File(config.yamlPath))
  yamlFileWriter.write(yaml)
  yamlFileWriter.close()
  println(s"YAML output written to ${config.yamlPath}")

  val csv = saveToCsv(lawnLimit, mowers, finalStates)
  val csvFileWriter = new FileWriter(new File(config.csvPath))
  csvFileWriter.write(csv)
  csvFileWriter.close()
  println(s"CSV output written to ${config.csvPath}")
}
