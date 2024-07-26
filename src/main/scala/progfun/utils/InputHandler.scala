package utils

import better.files._
import scala.io.StdIn
import scala.annotation.tailrec
import java.util.Locale

final case class Coordinate(x: Int, y: Int)
final case class CoordinateAndDirection(
    coordinate: Coordinate,
    direction: String)
final case class InputData(limit: Coordinate, tondeuses: List[MowerInput])

final case class MowerInput(start: CoordinateAndDirection, sequence: String)

class InputHandler() {
  def readFile(filePath: String): InputData = {
    val f = File(filePath)

    val lines = f.lines.toList

    val limit = lines.headOption match {
      case Some(line) =>
        val limitParts = line.split(" ").nn.map(_.nn)
        Coordinate(limitParts(0).toInt, limitParts(1).toInt)
      case None =>
        println("Missing lawn limit")
        sys.exit(1)
    }

    val tondeuses = lines
      .drop(1)
      .grouped(2)
      .collect {
        case List(startPost, sequence) => {
          makeInputData(startPost, sequence)
        }
        case _ => {
          println("Missing some mower data")
          sys.exit(1)
        }
      }
      .toList

    InputData(limit, tondeuses)

  }

  def cmdData(): InputData = {
    println("Map size x > ")
    val mapSizeXStr = StdIn.readLine()

    println("\nMap size y > ")
    val mapSizeYStr = StdIn.readLine()

    try {
      val mapSizeX = mapSizeXStr.toInt
      val mapSizeY = mapSizeYStr.toInt

      val userMower = inputLoop(List.empty[String])

      val mowers = userMower
        .grouped(2)
        .collect {
          case List(startPos, sequence) => { makeInputData(startPos, sequence) }
          case _ => {
            println("Missing some mower data")
            sys.exit(1)
          }
        }
        .toList

      InputData(
        Coordinate(mapSizeX, mapSizeY),
        mowers
      )
    } catch {
      case _: java.lang.NumberFormatException => {
        println("Please enter a number")
        sys.exit(1)
      }
      case _ => {
        println("An error has occurred while processing your input")
        sys.exit()
      }
    }
  }

  @tailrec
  private def inputLoop(acc: List[String]): List[String] = {
    println("Start possition (as X Y Direction) (press <ENTER> if you'r done)")
    val startPosition = StdIn.readLine()
    if (
      startPosition match
        case "" => false
        case _  => true
    ) {
      println("Motion sequence ")
      val motionSequence = StdIn.readLine()
      inputLoop(
        acc ::: startPosition :: motionSequence.toUpperCase(Locale.ROOT).nn
          :: Nil
      )
    } else {
      acc
    }
  }

  private def makeInputData(startPos: String, sequence: String): MowerInput = {
    val coordinate = startPos.split(" ").nn.map(_.nn)
    try {
      MowerInput(
        CoordinateAndDirection(
          Coordinate(coordinate(0).toInt, coordinate(1).toInt),
          coordinate(2).toUpperCase(Locale.ROOT).nn
        ),
        sequence.mkString
      )
    } catch {
      case e: NumberFormatException => {
        println("The starting coordinates of a mower are wrong")
        sys.exit(1)
      }
      case _ => {
        println("Something went wrong")
        sys.exit(1)
      }
    }

  }
}
