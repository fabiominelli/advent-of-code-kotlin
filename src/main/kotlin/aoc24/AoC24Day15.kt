package aoc24

import Problem

class AoC24Day15: Problem(15, 2024, "Warehouse Woes") {

    data class Position(val x: Int, val y: Int)

    enum class Direction(val ch:Char, val dx: Int, val dy: Int) {
        NORTH('^', 0,-1),
        EAST('>',1,0),
        SOUTH('v', 0,1),
        WEST('<', -1,0);
        companion object {
            fun of(ch: Char) = entries.first { it.ch == ch }
        }
    }

    enum class Cell(val ch: Char, val isBox:Boolean) {
        WALL('#', false),
        EMPTY('.', false),
        BOX('O', true),
        LEFT_BOX('[', true),
        RIGHT_BOX(']', true),
        ROBOT('@', false);
        companion object {
            fun of(ch: Char) = entries.first { it.ch == ch }
        }
    }

    class Warehouse(lines: List<String>, private val extended: Boolean =false) {

        private val blankLineIndex = lines.indexOf("")
        private var nextMoveIdx = 0
        private lateinit var robotPos:Position

        val grid = lines.subList(0,blankLineIndex)
            .mapIndexed { row, line ->
                line.map { ch -> Cell.of(ch) }
                    .flatMap { cell ->
                        if (!extended) listOf(cell)
                        else when (cell) {
                            Cell.ROBOT -> listOf(Cell.ROBOT, Cell.EMPTY)
                            Cell.EMPTY -> listOf(Cell.EMPTY, Cell.EMPTY)
                            Cell.BOX -> listOf(Cell.LEFT_BOX, Cell.RIGHT_BOX)
                            Cell.WALL -> listOf(Cell.WALL, Cell.WALL)
                            Cell.LEFT_BOX, Cell.RIGHT_BOX -> listOf(Cell.LEFT_BOX, Cell.RIGHT_BOX) // impossible
                    } }
                    .also { if (it.contains(Cell.ROBOT)) robotPos=Position(it.indexOf(Cell.ROBOT),row) }
                    .toTypedArray()
            }

        private val moves = lines.drop(blankLineIndex+1).joinToString(separator = "") { it }

        fun moveRobot(): Boolean {
            if (nextMoveIdx >= moves.length) return false
            val moveDir = Direction.of(moves[nextMoveIdx])
            val nextRobotPos = Position(robotPos.x + moveDir.dx, robotPos.y + moveDir.dy)
            val cell = grid[nextRobotPos.y][nextRobotPos.x]
            when (cell) {
                Cell.EMPTY -> {
                    grid[robotPos.y][robotPos.x] = Cell.EMPTY
                    grid[nextRobotPos.y][nextRobotPos.x] = Cell.ROBOT
                    robotPos = nextRobotPos
                }
                Cell.BOX, Cell.LEFT_BOX, Cell.RIGHT_BOX -> {
                    if (canMoveBox(cell,  nextRobotPos, moveDir)) {
                        moveBox(cell, nextRobotPos, moveDir)
                        grid[robotPos.y][robotPos.x] = Cell.EMPTY
                        grid[nextRobotPos.y][nextRobotPos.x] = Cell.ROBOT
                        robotPos = nextRobotPos
                    }
                }
                Cell.WALL -> { /* do nothing */ }
                Cell.ROBOT -> { /* impossible */ }
            }
            nextMoveIdx++
            return true
        }

        private fun canMoveBox(box:Cell, boxPos: Position, moveDir: Direction): Boolean {
            val emptySpacesNeeded: List<Position> = neededSpacesToMoveBox(box, boxPos, moveDir)
            val res = emptySpacesNeeded.all { pos ->
                val currentCell = grid[pos.y][pos.x]
                currentCell == Cell.EMPTY || (currentCell.isBox && canMoveBox(currentCell, pos, moveDir))
            }
            return res
        }

        private fun moveBox(box:Cell, boxPos: Position, moveDir: Direction) {
            val emptySpacesNeeded: List<Position> = neededSpacesToMoveBox(box, boxPos, moveDir)
            emptySpacesNeeded.forEach { pos ->
                val currentCell = grid[pos.y][pos.x]
                if (currentCell.isBox) {
                    moveBox(currentCell, pos, moveDir)
                }
            }
            grid[boxPos.y][boxPos.x] = Cell.EMPTY
            if (box == Cell.LEFT_BOX) {
                grid[boxPos.y][boxPos.x + 1] = Cell.EMPTY
            }
            if (box == Cell.RIGHT_BOX) {
                grid[boxPos.y][boxPos.x - 1] = Cell.EMPTY
            }

            grid[boxPos.y + moveDir.dy][boxPos.x + moveDir.dx] = box
            if (box == Cell.LEFT_BOX) {
                grid[boxPos.y + moveDir.dy][boxPos.x + 1 + moveDir.dx] = Cell.RIGHT_BOX
            }
            if (box == Cell.RIGHT_BOX) {
                grid[boxPos.y + moveDir.dy][boxPos.x -1 + moveDir.dx] = Cell.LEFT_BOX
            }
        }

        private fun neededSpacesToMoveBox(box: Cell, boxPos: Position, moveDir: Direction): List<Position> {
            val emptySpacesNeeded: MutableList<Position> = mutableListOf()
            when (moveDir) {
                Direction.NORTH, Direction.SOUTH -> {
                    emptySpacesNeeded.add(Position(boxPos.x, boxPos.y + moveDir.dy))
                    if (box == Cell.LEFT_BOX) emptySpacesNeeded.add(Position(boxPos.x + 1, boxPos.y + moveDir.dy))
                    if (box == Cell.RIGHT_BOX) emptySpacesNeeded.add(Position(boxPos.x - 1, boxPos.y + moveDir.dy))
                }
                Direction.EAST -> {
                    when (box) {
                        Cell.BOX, Cell.RIGHT_BOX -> emptySpacesNeeded.add(Position(boxPos.x + 1, boxPos.y))
                        Cell.LEFT_BOX -> emptySpacesNeeded.add(Position(boxPos.x + 2, boxPos.y))
                        else -> {}
                    }
                }
                Direction.WEST -> {
                    when (box) {
                        Cell.BOX, Cell.LEFT_BOX -> emptySpacesNeeded.add(Position(boxPos.x - 1, boxPos.y))
                        Cell.RIGHT_BOX -> emptySpacesNeeded.add(Position(boxPos.x - 2, boxPos.y))
                        else -> {}
                    }
                }
            }
            return emptySpacesNeeded.toList()
        }


        fun sumOfCoordinates(): Int {
            return grid.mapIndexed { y, row ->
                row.mapIndexed { x, cell ->
                    when (cell) {
                        Cell.BOX, Cell.LEFT_BOX -> x + 100*y
                        else -> 0
                    }
                }.sum()
            }.sum()
        }

        fun print() {
            println()
            println()
            grid.forEach { row ->
                row.forEach { cell ->
                    print(cell.ch)
                }
                println()
            }
        }
    }



    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val wh = Warehouse(lines)
//        wh.print()
        while (wh.moveRobot()) {
//            wh.print()
            // Continue moving the robot until it can no longer move
        }
        return wh.sumOfCoordinates().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val wh = Warehouse(lines, true)
//        wh.print()
        while (wh.moveRobot()) {
//            wh.print()
            // Continue moving the robot until it can no longer move
        }
        return wh.sumOfCoordinates().toString()
    }

}