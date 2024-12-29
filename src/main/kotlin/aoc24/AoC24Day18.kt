package aoc24

import Problem
import aoc24.AoC24Day18.MemoryState.*
import java.util.*

class AoC24Day18: Problem(18, 2024, "RAM Run") {

    enum class MemoryState {
        SAFE,
        CORRUPTED
    }

    enum class Direction(val dx:Int, val dy:Int) {
        UP(0,-1),
        DOWN(0,1),
        LEFT(-1,0),
        RIGHT(1,0)
    }

    data class Position(val x:Int, val y:Int) {
        override fun toString() = "$x,$y"
    }

    class MemorySpace(val width: Int, val height: Int, lines: List<String>, fallen: Int) {
        var grid = Array(width) { Array(height) { SAFE } }
        var fallingBytes:List<Position> = lines.map { line ->
            val (x,y) = line.split(",").map { it.toInt() }
            Position(x,y)
        }

        fun hasFallen(number:Int) {
            grid = Array(width) { Array(height) { SAFE } }
            fallingBytes.take(number).forEach { pos ->
                grid[pos.x][pos.y] = CORRUPTED
            }
        }

        fun shortestPathLength():Int {
            val distance = Array(width) { Array(height) { -1 } }
            distance[0][0] = 0
            val queue: PriorityQueue<Pair<Position, Int>> = PriorityQueue(compareBy { it.second })
            queue.add(Pair(Position(0, 0), 0))
            while (queue.isNotEmpty()) {
                val (pos, dist) = queue.first()
                queue.remove(queue.first())
                if (pos.x == width-1 && pos.y == height-1) {
                    return dist
                }
                Direction.entries.forEach {
                    val nextPos = Position(pos.x + it.dx, pos.y + it.dy)
                    if (nextPos.x in 0..<width && nextPos.y in 0..<height &&
                            grid[nextPos.x][nextPos.y]==SAFE && distance[nextPos.x][nextPos.y] == -1) {
                        distance[nextPos.x][nextPos.y] = dist + 1
                        queue.add(Pair(nextPos, dist + 1))
                    }
                }
            }
            return -1
        }

        fun firstByteBlockingTheExit():Position {
            var open = 0
            var closed = fallingBytes.size-1

            while (open +1 < closed) {
                val candidate = (open+closed)/2
                hasFallen(candidate)
                val shortest = shortestPathLength()
                if (shortest == -1) {
                    closed = candidate
                } else {
                    open = candidate
                }
            }
            return fallingBytes[closed-1]
        }

    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val size = if (isSample) 7 else 71
        val fallen = if (isSample) 12 else 1024
        val space = MemorySpace(size, size, lines, fallen)
        val shortest = space.shortestPathLength()
        return shortest.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val size = if (isSample) 7 else 71
        val fallen = if (isSample) 12 else 1024
        val space = MemorySpace(size, size, lines, fallen)
        return space.firstByteBlockingTheExit().toString()
    }

}