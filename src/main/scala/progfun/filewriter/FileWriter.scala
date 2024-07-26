package fr.esgi.al.funprog.filewriter

import fr.esgi.al.funprog.models.{LawnLimit, Mower, MowerState}
import scala.annotation.tailrec

object FileWrite {

  def saveToJson(
      lawnLimit: LawnLimit,
      mowers: List[Mower],
      finalStates: List[MowerState]
  ): String = {
    @tailrec
    def saveToJsonRec(
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
          saveToJsonRec(tail, finalStates.drop(1), newJson)
      }
    }

    val mowersJson = saveToJsonRec(mowers, finalStates, "")
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
          saveToYamlRec(tail, finalStates.drop(1), newYaml)
      }
    }

    val mowersYaml = saveToYamlRec(mowers, finalStates, "")
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

}
