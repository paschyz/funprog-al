package fr.esgi.al.funprog

import fr.esgi.al.funprog.direction.Direction
import fr.esgi.al.funprog.models._
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

  val lawnLimit = LawnLimit(5, 5)
  val mowers = List(
    Mower(Point(1, 2), 'N', List('G', 'A', 'G', 'A', 'G', 'A', 'G', 'A', 'A')),
    Mower(
      Point(3, 3),
      'E',
      List('A', 'A', 'D', 'A', 'A', 'D', 'A', 'D', 'D', 'A')
    )
  )

  val end = mowers.map(processInstructions(_, lawnLimit))

  end.foreach { state =>
    println(
      s"Final position: (${state.position.x}, ${state.position.y}), Direction: ${state.direction}"
    )
  }

  val json = saveToJson(lawnLimit, mowers, finalStates)
  val jsonFileWriter = new FileWriter(new File("/tmp/output.json"))
  jsonFileWriter.write(json)
  jsonFileWriter.close()
  println(s"JSON output written to /tmp/output.json")

  val yaml = saveToYaml(lawnLimit, mowers, finalStates)
  val yamlFileWriter = new FileWriter(new File("/tmp/output.yaml"))
  yamlFileWriter.write(yaml)
  yamlFileWriter.close()
  println(s"YAML output written to /tmp/output.yaml")
}
