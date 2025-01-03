package aoc24

import Problem
import kotlin.math.abs

class AoC24Day20: Problem(20, 2024, "Race Condition") {

    data class Position(val x:Int, val y:Int)

    enum class Direction(val dx:Int, val dy:Int) {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);
    }

    enum class CellType(val ch:Char) {
        WALL('#'), EMPTY('.'), START('S'), FINISH('E');

        companion object {
            fun of(ch:Char) = entries.find { it.ch == ch }!!
        }
    }

    class Racetrack(lines:List<String>) {

        private var start:Position? = null
        private var finish:Position? = null

        val grid = Array (lines[0].length) { x ->
            Array (lines.size) { y ->
                CellType.of(lines[y][x]).also {
                    if (it == CellType.START) start = Position(x, y)
                    if (it == CellType.FINISH) finish = Position(x, y)
                }
            }
        }
        val width = grid[0].size
        val height = grid.size

        private val distance:MutableMap<Position, Int> = mutableMapOf()
        private val sequence:MutableList<Position> = mutableListOf()

        init {
            distance[start!!] = 0
            sequence.add(start!!)
            var step = 0
            var current = start!!
            var prev:Position? = null
            while(true) {
                step++
                val nextPos = Direction.entries.firstNotNullOf { dir ->
                    val next = Position(current.x + dir.dx, current.y + dir.dy)
                    if (next==prev || grid[next.x][next.y] == CellType.WALL) null else next
                }
                distance[nextPos] = step
                sequence.add(nextPos)
                if (nextPos == finish) break
                prev = current
                current = nextPos
            }
        }

        fun countShortCheatsSavingAtLeast(minimum:Int):Int {
            return (0..sequence.size-minimum).sumOf { step ->
                val pos = sequence[step]
                Direction.entries.count { dir ->
                    val target = Position(pos.x + 2*dir.dx, pos.y + 2*dir.dy)
                    val ok = target.x >= 0 && target.x < grid.size && target.y >= 0 && target.y < grid[0].size
                        && grid[target.x][target.y] != CellType.WALL
                        && distance[target]!! - (distance[pos]!! + 2) >= minimum
                    ok
                }
            }
        }


        fun countLongCheatsSavingAtLeast(minimum:Int):Int {

            val totalCheats = (0..sequence.size - minimum).sumOf { step ->

                val pos = sequence[step]
                (-20..20).sumOf { dx ->
                    (-20+abs(dx)..20-abs(dx)).count { dy ->
                        if ((0..<width).contains(pos.x+dx) && (0..<height).contains(pos.y+dy)) {
                            val target = Position(pos.x + dx, pos.y + dy)
                            (grid[target.x][target.y] != CellType.WALL
                                    && distance[target]!! - distance[pos]!! - abs(dx) - abs(dy) >= minimum)
                        } else false
                    }
                }
            }

            return totalCheats
        }

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val racetrack = Racetrack(lines)
        return racetrack.countShortCheatsSavingAtLeast(if (isSample) 12 else 100).toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val racetrack = Racetrack(lines)
        return racetrack.countLongCheatsSavingAtLeast(if (isSample) 70 else 100).toString()
    }

}