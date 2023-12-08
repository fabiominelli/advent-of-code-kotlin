package aoc22

import ProblemSolver
import java.util.*
import kotlin.Comparator

class AoC22Day24: ProblemSolver(24, 2022) {

    class Maze(val lines: List<String>) {
        val width = lines[0].length - 2
        val height = lines.size - 2

        fun isCellFreeAtTime(minute:Int, row:Int, col:Int):Boolean {
            return lines[row+1][((col - minute%width + width) % width) + 1] != '>'
                    && lines[row+1][((col + minute) % width) + 1] != '<'
                    && lines[((row - minute%height + height) % height) + 1][col+1] != 'v'
                    && lines[((row+minute) % height) + 1][col+1] != '^'
        }

        fun distanceToExit(row:Int, col:Int) = height-row + width-col-1

        fun reverse():Maze {
            val chMap = mapOf('>' to '<', '<' to '>', '^' to 'v', 'v' to '^')
            return Maze(lines.reversed().map { line -> line.reversed().map { ch -> chMap[ch] ?: ch }.joinToString("") })
        }
    }

    sealed class ExplorationNode(val time:Int,val cost:Int):Comparable<ExplorationNode> {
        companion object {
            fun computeCost(time:Int, row:Int, col:Int, maze:Maze):Int = time+maze.distanceToExit(row,col)
        }
        abstract fun spaceTimeNode():SpaceTimeNode
        override fun compareTo(other: ExplorationNode) = cost.compareTo(other.cost)
        open fun samePlaceAndTime(other:ExplorationNode):Boolean = false
    }

    class MazeExplorationNode(time:Int, val row:Int, val col:Int, maze:Maze): ExplorationNode(time, computeCost(time,row,col,maze)) {
        override fun spaceTimeNode() = SpaceTimeNode(time, row, col)
        override fun samePlaceAndTime(other: ExplorationNode) =
            other is MazeExplorationNode && time==other.time && row==other.row && col==other.col
        override fun toString(): String = "[Row $row, Col $col]  Time $time, Cost $cost"
    }

    class StartExplorationNode(time:Int): ExplorationNode(time, Int.MAX_VALUE) {
        override fun spaceTimeNode() = SpaceTimeNode(time, -1, -1)
        override fun toString(): String = "Time $time: START"
    }

    class EndExplorationNode(time:Int): ExplorationNode(time, time) {
        override fun spaceTimeNode() = SpaceTimeNode(time, Int.MAX_VALUE, Int.MAX_VALUE)
        override fun toString(): String = "Time $time: END"
    }

    data class SpaceTimeNode(val time:Int, val row:Int, val col:Int)

    private fun solveMaze(fromMinute: Int, maze: Maze): Int {
        val compareByCost: Comparator<ExplorationNode> = compareBy { it.cost }
        val openNodes = PriorityQueue(compareByCost)
        openNodes.add(StartExplorationNode(fromMinute))

        val closedNodes: MutableSet<SpaceTimeNode> = mutableSetOf()

        var minimumTimeToEnd: Int? = null

        while (minimumTimeToEnd == null && openNodes.isNotEmpty()) {
            val node = openNodes.remove()
            val successors = when (node) {
                is StartExplorationNode -> listOfNotNull(
                    StartExplorationNode(node.time + 1),
                    MazeExplorationNode(node.time + 1, 0, 0, maze),
                )

                is MazeExplorationNode -> listOfNotNull(
                    if (node.row < 1) null else MazeExplorationNode(node.time + 1, node.row - 1, node.col, maze),
                    if (node.row > maze.height - 2) null else MazeExplorationNode(
                        node.time + 1,
                        node.row + 1,
                        node.col,
                        maze
                    ),
                    if (node.col > maze.width - 2) null else MazeExplorationNode(
                        node.time + 1,
                        node.row,
                        node.col + 1,
                        maze
                    ),
                    if (node.col < 1) null else MazeExplorationNode(node.time + 1, node.row, node.col - 1, maze),
                    MazeExplorationNode(node.time + 1, node.row, node.col, maze),
                    if (node.row == maze.height - 1 && node.col == maze.width - 1) EndExplorationNode(node.time + 1).also {
                        minimumTimeToEnd = node.time + 1
                    } else null
                )

                is EndExplorationNode -> emptyList() // unreachable
            }.filter { successor -> // is successor already explored or in queue?
                openNodes.none { it.samePlaceAndTime(successor) } && !closedNodes.contains(successor.spaceTimeNode())
            }.filter { successor -> // is spacetime node without Blizzards?
                when (successor) {
                    is StartExplorationNode -> true
                    is MazeExplorationNode -> maze.isCellFreeAtTime(successor.time, successor.row, successor.col)
                    is EndExplorationNode -> true
                }
            }
            openNodes.addAll(successors)
            closedNodes.add(node.spaceTimeNode())
        }
        return minimumTimeToEnd?:throw Exception("Could not find a solution")
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val maze = Maze(lines)
        return "${solveMaze(0, maze)}"
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val maze = Maze(lines)
        val firstLegTime = solveMaze(0, maze)
        val secondLegTime = solveMaze(firstLegTime, maze.reverse())
        val thirdLegTime = solveMaze(secondLegTime, maze)
        return "$thirdLegTime"
    }

}