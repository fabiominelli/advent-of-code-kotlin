package aoc24

import Problem

class AoC24Day06: Problem(6, 2024, "Guard Gallivant") {

    enum class CellType {
        EMPTY, OBSTACLE, VISITED
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;
        fun rotate() = when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    data class Position(val x: Int, val y: Int)

    data class GuardState(val position: Position, val direction: Direction)

    class Grid(lines: List<String>, val obstacle: Position? = null) {

        val width = lines[0].length
        val height = lines.size
        val cells = Array(width) { Array(height) { CellType.EMPTY } }
        lateinit var guardState:GuardState

        init {
            for (y in 0..<height) {
                for (x in 0..<width) {
                    cells[x][y] = when (lines[y][x]) {
                        '.' -> CellType.EMPTY
                        '#' -> CellType.OBSTACLE
                        '^' -> CellType.VISITED.also { guardState = GuardState(Position(x, y), Direction.UP) }
                        else -> throw IllegalArgumentException("Invalid cell type")
                    }
                }
            }
        }

        val guardHistory = mutableListOf(guardState)
        val visited = mutableSetOf(guardState.position)
        var cycleDetected = false

        private fun isInBounds(pos:Position): Boolean {
            return pos.x in 0..<width && pos.y in 0..<height
        }


        private fun guardProceed():Boolean {
            val (x, y) = guardState.position
            val facing = when (guardState.direction) {
                Direction.UP -> Position(x, y - 1)
                Direction.DOWN -> Position(x, y + 1)
                Direction.LEFT -> Position(x - 1, y)
                Direction.RIGHT -> Position(x + 1, y)
            }
            if (!isInBounds(facing)) {
                return false
            }
            if (cells[facing.x][facing.y] == CellType.OBSTACLE || facing == obstacle) {
                guardState = guardState.copy(direction = guardState.direction.rotate())
                return guardProceed()
            }
            guardState = guardState.copy(position = facing)
            visited.add(facing)
            if (cells[facing.x][facing.y] == CellType.EMPTY) {
                cells[facing.x][facing.y] = CellType.VISITED
            }
            if (guardHistory.contains(guardState)) {
                cycleDetected = true
            }
            guardHistory.add(guardState)
            return true
        }

        fun proceedUntilExitingOrCycling() {
            while (guardProceed()) {
                if (cycleDetected) {
                    break
                }
            }
        }

        fun printGrid() {
            println()
            for (y in 0..<height) {
                for (x in 0..<width) {
                    print(when (cells[x][y]) {
                        CellType.EMPTY -> '.'
                        CellType.OBSTACLE -> '#'
                        CellType.VISITED -> 'X'
                    })
                }
                println()
            }
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        grid.proceedUntilExitingOrCycling()
        return grid.visited.size.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        grid.proceedUntilExitingOrCycling()
        val obstacleCandidates = grid.visited.filterNot { p -> p==grid.guardHistory[0].position }
        if (obstacleCandidates.size>50) {
            return "too long.."
        }
        val obstaclePositionsWithCycles = obstacleCandidates.filter { obs ->
            val newGrid = Grid(lines, obs)
            newGrid.proceedUntilExitingOrCycling()
            newGrid.cycleDetected
        }
        return obstaclePositionsWithCycles.size.toString()
    }

}