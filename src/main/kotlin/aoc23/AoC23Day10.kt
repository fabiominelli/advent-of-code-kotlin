package aoc23

import Problem
import aoc23.AoC23Day10.InsideOrOutside.*
import aoc23.AoC23Day10.Sketch.Direction.*
import aoc23.AoC23Day10.Sketch.Pipe.*


class AoC23Day10: Problem(10, 2023) {

    class Sketch(lines:List<String>) {

        enum class Direction(val dr:Int, val dc:Int) {
            NORTH(-1,0), EAST(0,1), SOUTH(1,0), WEST(0,-1);
        }

        enum class Pipe(val directions:List<Direction>) {
            START(emptyList()),
            EMPTY(emptyList()),
            PIPE_H(listOf(WEST,EAST)),
            PIPE_V(listOf(NORTH,SOUTH)),
            PIPE_L(listOf(NORTH,EAST)),
            PIPE_F(listOf(EAST,SOUTH)),
            PIPE_7(listOf(SOUTH,WEST)),
            PIPE_J(listOf(WEST,NORTH));
            companion object {
                fun fromChar(ch: Char): Pipe {
                    return when(ch) {
                        'S' -> START
                        '.' -> EMPTY
                        '|' -> PIPE_V
                        '-' -> PIPE_H
                        else -> valueOf("PIPE_$ch")
                    }
                }
            }
        }

        data class Cell(val row:Int, val col:Int, val pipe:Pipe) {
            fun isStart() = pipe==START
            override fun toString(): String {
                return "($row,$col) $pipe"
            }
        }

        val height = lines.size
        val width = lines[0].length
        lateinit var startCell:Cell
        val cells = Array(height) { row -> Array(width) { col ->
            val pipe = Pipe.fromChar(lines[row][col])
            val cell = Cell(row,col,pipe)
            if (pipe==START) startCell=cell
            cell
        }}

        fun allNeighborsOf(cell:Cell):List<Cell> = listOfNotNull(
            neighborForDirection(cell, NORTH),
            neighborForDirection(cell, EAST),
            neighborForDirection(cell, SOUTH),
            neighborForDirection(cell, WEST)
        )

        private fun neighborForDirection(cell:Cell, dir:Direction):Cell? {
            val nr = cell.row + dir.dr
            val nc = cell.col + dir.dc
            return if (nr in 0..<height && nc in 0..<width) cells[nr][nc] else null
        }
        fun connectedNeighboursOf(cell:Cell):List<Cell> {
            return if (cell.isStart()) {
                allNeighborsOf(cell).filter { connectedNeighboursOf(it).contains(cell) }
            } else {
                cell.pipe.directions.mapNotNull { neighborForDirection(cell, it) }
            }
        }

        fun effectivePipe(row:Int, col:Int):Pipe {
            val cell = cells[row][col]
            return if(cell==startCell) {
                val directions = connectedNeighboursOf(startCell)
                    .map { Direction.entries.first { dir -> dir.dr == it.row - startCell.row && dir.dc == it.col - startCell.col } }
                    .toSet()
                Pipe.entries.first { it.directions.containsAll(directions) }
                }
            else cell.pipe
        }
        fun nextConnected(fromCell:Cell, currentCell:Cell):Cell =
            connectedNeighboursOf(currentCell).first { it != fromCell }

        class Loop(val cells:Set<Cell>) {
            companion object {
                fun forSketch(sketch:Sketch):Loop {
                    with(sketch) {
                        var exploringCells = connectedNeighboursOf(startCell).map { Pair(startCell, it) }
                        val foundCells:MutableList<Cell> = mutableListOf(startCell)
                        foundCells.addAll(connectedNeighboursOf(startCell))
                        while (exploringCells[0].second != exploringCells[1].second) {
                            exploringCells = exploringCells.map {
                                val next = nextConnected(it.first, it.second)
                                foundCells.add(next)
                                Pair(it.second, next)
                            }
                        }
                        return Loop(foundCells.toSet())
                    }
                }
            }

            fun maxDistance() =  cells.size/2
        }
    }



    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val sketch = Sketch(lines)
        val loop = Sketch.Loop.forSketch(sketch)
        return loop.maxDistance().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val sketch = Sketch(lines)
        val loop = Sketch.Loop.forSketch(sketch)
        return countInside(sketch, loop).toString()
    }


    enum class InsideOrOutside {
        OUTSIDE, INSIDE, NORTH_INSIDE, SOUTH_INSIDE
    }

    private val mapInsideOrOutside:Map<Pair<InsideOrOutside, Sketch.Pipe>,InsideOrOutside> = mapOf(
        Pair(OUTSIDE, EMPTY) to OUTSIDE,
        Pair(OUTSIDE, PIPE_V) to INSIDE,
        Pair(OUTSIDE, PIPE_L) to NORTH_INSIDE,
        Pair(OUTSIDE, PIPE_F) to SOUTH_INSIDE,
        Pair(INSIDE, PIPE_V) to OUTSIDE,
        Pair(INSIDE, PIPE_L) to SOUTH_INSIDE,
        Pair(INSIDE, PIPE_F) to NORTH_INSIDE,
        Pair(INSIDE, EMPTY) to INSIDE,
        Pair(NORTH_INSIDE, PIPE_H) to NORTH_INSIDE,
        Pair(NORTH_INSIDE, PIPE_J) to OUTSIDE,
        Pair(NORTH_INSIDE, PIPE_7) to INSIDE,
        Pair(SOUTH_INSIDE, PIPE_H) to SOUTH_INSIDE,
        Pair(SOUTH_INSIDE, PIPE_J) to INSIDE,
        Pair(SOUTH_INSIDE, PIPE_7) to OUTSIDE,
    )

    private fun countInside(sketch:Sketch, loop:Sketch.Loop):Int {

        fun nextInsideOurOutside(current:InsideOrOutside, r:Int, c:Int):InsideOrOutside =
            if (loop.cells.contains(sketch.cells[r][c])) {
                mapInsideOrOutside[Pair(current, sketch.effectivePipe(r, c))] ?: current
            } else {
                current
            }

        var countInside = 0

        (0..<sketch.height).forEach { r ->
            var insideOrOutside:InsideOrOutside = OUTSIDE
            (0..<sketch.width).forEach { c ->
                val isLoopBorder = loop.cells.any { it.row==r && it.col==c }
                if (insideOrOutside==INSIDE && !isLoopBorder) countInside++
                insideOrOutside = nextInsideOurOutside(insideOrOutside, r,c)
            }
        }
        return countInside
    }

}