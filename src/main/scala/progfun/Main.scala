package fr.esgi.al.funprog

import fr.esgi.al.funprog.direction.Direction
import fr.esgi.al.funprog.models._

@main
def Main(): Unit = {
  def processInstructions(mower: Mower, lawnLimit: LawnLimit): MowerState = {
    def executeInstruction(state: MowerState, instruction: Char): MowerState = {
      instruction match {
        case 'A' => // Move forward
          val newPosition =
            Direction.moveForward(state.position, state.direction)
          if (
            newPosition.x < 0 || newPosition.x > lawnLimit.xMax || newPosition.y < 0 || newPosition.y > lawnLimit.yMax
          ) {
            state // Out of bounds, don't move
          } else {
            MowerState(newPosition, state.direction)
          }
        case 'D' => // Turn right
          MowerState(state.position, Direction.turnRight(state.direction))
        case 'G' => // Turn left
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

  // Exemple de configuration du terrain et des tondeuses
  val lawnLimit = LawnLimit(5, 5)
  val mowers = List(
    Mower(Point(1, 2), 'N', List('G', 'A', 'G', 'A', 'G', 'A', 'G', 'A', 'A')),
    Mower(
      Point(3, 3),
      'E',
      List('A', 'A', 'D', 'A', 'A', 'D', 'A', 'D', 'D', 'A')
    )
  )

  // Traitement des tondeuses
  val finalStates = mowers.map(processInstructions(_, lawnLimit))

  // Affichage des résultats
  finalStates.foreach { state =>
    println(
      s"Final position: (${state.position.x}, ${state.position.y}), Direction: ${state.direction}"
    )
  }
}
