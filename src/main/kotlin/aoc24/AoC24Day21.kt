package aoc24

import Problem
import kotlin.math.abs

class AoC24Day21: Problem(21, 2024, "Keypad Conundrum") {


    class Code(line:String) {
        val sequence = "A$line".zip(line)
        val numericValue = line.substring(0,line.length-1).toInt()
    }

    enum class Direction(val ch:Char) {
       UP('^'), DOWN('v'), LEFT('<'), RIGHT('>') }

    data class Position(val x:Int, val y:Int)

    sealed class Keypad(val name:String, val drivenBy:Keypad?) {

        private fun reset() {
            drivenBy?.reset()
        }

        fun complexityFor(code:Code):Long {
            reset()
            drivenBy?.reset()

            val sequence = code.sequence.sumOf { (from, to) -> topSequenceToMoveAndClick(from, to) }
            val result = code.numericValue * sequence

            return result
        }

        private val cache = mutableMapOf<Pair<Char,Char>,Long>()
        private fun topSequenceToMoveAndClick(moveFrom:Char, toClick:Char):Long {
            if (drivenBy == null) {
                return 1
            } else {
                val cached = cache[Pair(moveFrom, toClick)]
                if (cached != null) return cached

                val movesAlternatives = getMoves(moveFrom, toClick)
                val sequences = movesAlternatives.map { alt ->
                    val moves = alt.map { it.ch }
                    (listOf('A') + moves).zip(moves + listOf('A')).sumOf { it -> drivenBy.topSequenceToMoveAndClick(it.first, it.second) }
                }
                val minimumSequence = if (sequences.size==2 && sequences[1] < sequences[0]) {
                    sequences[1]
                } else if (sequences.isNotEmpty()){
                    sequences[0]
                } else {
                    1
                }
                cache[Pair(moveFrom, toClick)] = minimumSequence
                return minimumSequence
            }
        }
        abstract val char2Position:Map<Char, Position>

        private fun getMoves(from:Char, to:Char):List<List<Direction>> {
            val fromPos = char2Position[from]!!
            val toPos = char2Position[to]!!
            val dx = toPos.x - fromPos.x
            val dy = toPos.y - fromPos.y
            val horizontalMoves = (0..<abs(dx)).map { if (dx > 0) Direction.RIGHT else Direction.LEFT }
            val verticalMoves = (0..<abs(dy)).map { if (dy > 0) Direction.DOWN else Direction.UP }

            val res:MutableList<List<Direction>> = mutableListOf()

            if (Position(fromPos.x+dx, fromPos.y) != char2Position['.'] && dx!=0) {
                // ok to move horizontally first
                res += horizontalMoves.plus(verticalMoves)
            }
            if (Position(fromPos.x, fromPos.y+dy) != char2Position['.'] && dy!=0) {
                // ok to move vertically first
                res += verticalMoves.plus(horizontalMoves)
            }
            return res
        }
    }

    class NumericKeypad(name: String, drivenBy:Keypad): Keypad(name,drivenBy) {
        override val char2Position = mapOf(
            '7' to Position(0,0), '8' to Position(1,0), '9' to Position(2,0),
            '4' to Position(0,1), '5' to Position(1,1), '6' to Position(2,1),
            '1' to Position(0,2), '2' to Position(1,2), '3' to Position(2,2),
            '.' to Position(0,3), '0' to Position(1,3), 'A' to Position(2,3)
        )
    }

    class DirectionalKeyPad(name: String, drivenBy:Keypad?): Keypad(name, drivenBy) {
        override val char2Position = mapOf(
            '.' to Position(0,0), '^' to Position(1,0), 'A' to Position(2,0),
            '<' to Position(0,1), 'v' to Position(1,1), '>' to Position(2,1),
        )
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val codes = lines.map(::Code)
        val doorKeypad = NumericKeypad("door",
                            DirectionalKeyPad("   robot1",
                                DirectionalKeyPad("      robot2",
                                    DirectionalKeyPad("         human",null)
                                )
                            )
        )
        return codes.sumOf { doorKeypad.complexityFor(it) }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val codes = lines.map(::Code)
        val doorKeypad = NumericKeypad("door",
            DirectionalKeyPad(" robot1",
                DirectionalKeyPad("  robot2",
                    DirectionalKeyPad("   robot3",
                        DirectionalKeyPad("    robot4",
                            DirectionalKeyPad("      robot5",
                                DirectionalKeyPad("      robot6",
                                    DirectionalKeyPad("      robot7",
                                        DirectionalKeyPad("      robot8",
                                            DirectionalKeyPad("      robot9",
                                                DirectionalKeyPad("      robot10",
                                                    DirectionalKeyPad("      robot11",
                                                        DirectionalKeyPad("      robot12",
                                                            DirectionalKeyPad("      robot13",
                                                                DirectionalKeyPad("      robot14",
                                                                    DirectionalKeyPad("      robot15",
                                                                        DirectionalKeyPad("      robot16",
                                                                            DirectionalKeyPad("      robot17",
                                                                                DirectionalKeyPad("      robot18",
                                                                                    DirectionalKeyPad("      robot19",
                                                                                        DirectionalKeyPad("      robot20",
                                                                                            DirectionalKeyPad("      robot21",
                                                                                                DirectionalKeyPad("      robot22",
                                                                                                    DirectionalKeyPad("      robot23",
                                                                                                        DirectionalKeyPad("      robot24",
                                                                                                            DirectionalKeyPad("      robot25",
                                                                                                                DirectionalKeyPad("human",null)
                                                                                                            )
                                                                                                        )
                                                                                                    )
                                                                                                )
                                                                                            )
                                                                                        )
                                                                                    )
                                                                                )
                                                                            )
                                                                        )
                                                                    )
                                                                )
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        return codes.sumOf { doorKeypad.complexityFor(it) }.toString()
    }

}