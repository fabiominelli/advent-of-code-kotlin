package aoc23

import Problem
import aoc23.AoC23Day23.Direction.*
import java.util.*

class AoC23Day23 : Problem(23, 2023, "A Long Walk") {

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
        fun opposite():Direction = when(this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }

    data class Position(val row:Int, val col:Int) {
        fun neighbours():Map<Direction,Position> = mapOf(
            UP to Position(row-1,col),
            DOWN to Position(row+1,col),
            LEFT to Position(row,col-1),
            RIGHT to Position(row,col+1))
    }
    sealed class Tile(val position:Position)
    class HikePath(position:Position):Tile(position)
    class Forest(position:Position):Tile(position)
    class Slope(position:Position, val direction:Direction):Tile(position) {
        override fun toString(): String {
            return "Slope $position $direction)"
        }
    }
    class TrailsMap(private val tiles:Array<Array<Tile>>) {
        companion object {
            fun from(lines:List<String>) = TrailsMap(Array(lines.size) { row ->
                    Array(lines[0].length) { col ->
                        val pos = Position(row, col)
                        when (lines[row][col]) {
                            '#' -> Forest(pos)
                            '.' -> HikePath(pos)
                            '^' -> Slope(pos, UP)
                            '>' -> Slope(pos, RIGHT)
                            'v' -> Slope(pos, DOWN)
                            '<' -> Slope(pos, LEFT)
                            else -> throw Exception("Unknown tile")
                        }
                    }
            })
        }

        val height = tiles.size
        val width = tiles[0].size

        fun tile(position:Position) = tiles[position.row][position.col]

        fun hikeNeighbours(position: Position):Map<Direction,Position> =
            position.neighbours().filter {
                (0..<height).contains(it.value.row)
                        && (0..<width).contains(it.value.col)
                        && tiles[it.value.row][it.value.col] !is Forest
            }
    }

    class HikeGraph(private val trailsMap:TrailsMap, val ignoreSlopes:Boolean=false) {

        data class Arc(val from:Position, val to:Position, val len:Int)

        private val nodePositions:MutableSet<Position> = mutableSetOf()
        private val arcs:MutableList<Arc> = mutableListOf()

        private val start = Position(0,1)
        private val end = Position(trailsMap.height-1, trailsMap.width-2)

        init {
            val toExplore: Queue<Position> = LinkedList()
            toExplore.add(start)
            nodePositions.add(start)
            while(toExplore.isNotEmpty()) {
                val nodePosition = toExplore.remove()
                trailsMap.hikeNeighbours(nodePosition)
                    .forEach { (direction,_) ->
                        val nextNode = findNextNode(nodePosition,direction)
                        if (nextNode!=null) {
                            if (!nodePositions.contains(nextNode.first)) {
                                nodePositions.add(nextNode.first)
                                if (nextNode.first!=end) toExplore.add(nextNode.first)
                            }
                            arcs.add(Arc(nodePosition,nextNode.first, nextNode.second))
                        }
                    }
            }
        }

        private fun findNextNode(from:Position, initialDirection:Direction):Pair<Position,Int>? {
            var previousPos = from
            var steps = 0
            var direction = initialDirection
            while(true) {
                val previousTile = trailsMap.tile(previousPos)
                steps++
                val next = previousPos.neighbours()[direction]
                if (next==null || next==start) {
                    return null
                }
                if (!ignoreSlopes && previousTile.let { it is Slope && it.direction!=direction }) {
                    return null
                }
                val nextDirections = trailsMap.hikeNeighbours(next).map { it.key }
                if (nextDirections.size>2 || next==end) {
                    // node found
                    return Pair(next,steps)
                }

                direction = nextDirections.find { it!=direction.opposite() }!!
                previousPos = next
            }
        }

        fun lengthOfLongestPath() = lengthOfLongestPath(start, end, arcs)

        private fun lengthOfLongestPath(source:Position, target:Position, arcs:List<Arc>):Int {
            if (target==source) return 0
            val candidateArcs = arcs.filter { it.from == source }
            if (candidateArcs.isEmpty()) return Int.MIN_VALUE
            val result = candidateArcs.maxOf { arc ->
                lengthOfLongestPath(arc.to, end, arcs.filterNot { it.to == arc.to }) + arc.len
            }
            return result
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val trailsMap = TrailsMap.from(lines)
        return HikeGraph(trailsMap).lengthOfLongestPath().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val trailsMap = TrailsMap.from(lines)
        return HikeGraph(trailsMap,true).lengthOfLongestPath().toString()
    }

}
