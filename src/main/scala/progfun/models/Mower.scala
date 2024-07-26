package fr.esgi.al.funprog.models

final case class Mower(start: Point, direction: Char, instructions: List[Char])
final case class MowerState(position: Point, direction: Char)
