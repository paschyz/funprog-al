package fr.esgi.al.funprog.instruction
import fr.esgi.al.funprog.models.Mower
import fr.esgi.al.funprog.models.MowerState
import fr.esgi.al.funprog.models.LawnLimit
import fr.esgi.al.funprog.direction.Direction
object Instruction {
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
}
