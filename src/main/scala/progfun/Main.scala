package fr.esgi.al.funprog

import fr.esgi.al.funprog.models._

@main
def Main(): Unit = {
  def processInstructions(mower: Mower, mapLimit: MapLimit): MowerState = {
    def executeInstruction(state: MowerState, instruction: Char): MowerState = {
      instruction match {
        case 'A' =>
          val newPosition =
            Direction.moveForward(state.position, state.direction)
          if (
            newPosition.x < 0 || newPosition.x > mapLimit.max || newPosition.y < 0 || newPosition.y > mapLimit.max
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

  val mapLimit = MapLimit(5, 5)
  val mowers = List(
    Mower(Point(1, 2), 'N', List('G', 'A', 'G', 'A', 'G', 'A', 'G', 'A', 'A')),
    Mower(
      Point(3, 3),
      'E',
      List('A', 'A', 'D', 'A', 'A', 'D', 'A', 'D', 'D', 'A')
    )
  )

  val finalStates = mowers.map(processInstructions(_, mapLimit))

  finalStates.foreach { state =>
    println(
      s"Final position: (${state.position.x}, ${state.position.y}), Direction: ${state.direction}"
    )
  }
}
