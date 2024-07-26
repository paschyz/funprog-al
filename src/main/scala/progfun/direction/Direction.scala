package fr.esgi.al.funprog.direction

import fr.esgi.al.funprog.models.Point

object Direction {
  private val directions = Vector('N', 'E', 'S', 'W')

  def turnLeft(current: Char): Char = {
    val index = directions.indexOf(current)
    val newIndex = (index - 1 + directions.length) % directions.length
    directions.lift(newIndex).getOrElse(current)
  }

  def turnRight(current: Char): Char = {
    val index = directions.indexOf(current)
    val newIndex = (index + 1) % directions.length
    directions.lift(newIndex).getOrElse(current)
  }

  def moveForward(point: Point, direction: Char): Point = {
    direction match {
      case 'N' => Point(point.x, point.y + 1)
      case 'E' => Point(point.x + 1, point.y)
      case 'S' => Point(point.x, point.y - 1)
      case 'W' => Point(point.x - 1, point.y)
      case _   => point
    }
  }
}
