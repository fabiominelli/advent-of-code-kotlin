package aoc23

import Problem
import aoc23.AoC23Day16.Direction.*
import aoc23.AoC23Day16.TileType.*


class AoC23Day16: Problem(16, 2023, "The Floor Will Be Lava") {

    enum class Direction {
        UP, RIGHT, DOWN, LEFT
    }

    data class Position(val row:Int, val col:Int) {
        fun next(dir:Direction) = when(dir) {
            UP -> Position(row-1, col)
            RIGHT -> Position(row, col+1)
            DOWN -> Position(row+1, col)
            LEFT -> Position(row, col-1)
        }
    }

    enum class TileType(private val exitMap:Map<Direction,Set<Direction>>) {
        EMPTY(mapOf(UP to setOf(UP), RIGHT to setOf(RIGHT), DOWN to setOf(DOWN), LEFT to setOf(LEFT))),
        RIGHT_MIRROR(mapOf(UP to setOf(RIGHT), RIGHT to setOf(UP), DOWN to setOf(LEFT), LEFT to setOf(DOWN))),
        LEFT_MIRROR(mapOf(UP to setOf(LEFT), RIGHT to setOf(DOWN), DOWN to setOf(RIGHT), LEFT to setOf(UP))),
        VERT_SPLITTER(mapOf(UP to setOf(UP), RIGHT to setOf(UP,DOWN), DOWN to setOf(DOWN), LEFT to setOf(UP,DOWN))),
        HOR_SPLITTER(mapOf(UP to setOf(LEFT,RIGHT), RIGHT to setOf(RIGHT), DOWN to setOf(LEFT,RIGHT), LEFT to setOf(LEFT)));

        fun exitFor(enterDir:Direction) = exitMap[enterDir]?:throw Exception("No exit?")
    }

    class Tile(ch:Char) {
        val type:TileType = when(ch) {
            '.' -> EMPTY
            '/' -> RIGHT_MIRROR
            '\\' -> LEFT_MIRROR
            '|' -> VERT_SPLITTER
            '-' -> HOR_SPLITTER
            else -> throw Exception("unknown tile type")
        }
    }

    class Contraption(lines:List<String>) {
        val height = lines.size
        val width = lines[0].length
        val tiles:Array<Array<Tile>> = Array(height) { r-> Array(width) { c-> Tile(lines[r][c])} }

        fun allEnteringBeams():List<Pair<Position,Direction>> = buildList {
            (0..<width).forEach { col ->
                add(Pair(Position(0,col), DOWN))
                add(Pair(Position(height-1,col), UP))
            }
            (0..<height).forEach { row ->
                add(Pair(Position(row,0), RIGHT))
                add(Pair(Position(row,width-1), LEFT))
            }
        }
    }


    class TileState() {
        val lightDirs:MutableSet<Direction> = mutableSetOf()
        fun energized() = lightDirs.isNotEmpty()
    }

    class BeamExperiment(val contr:Contraption) {
        private val tileStates:Array<Array<TileState>> = Array(contr.height) { r-> Array(contr.width) { c-> TileState()} }
        fun beamEnters(pos:Position, dir:Direction) {
            if ((0..<contr.height).contains(pos.row) && (0..<contr.width).contains(pos.col)) {
                contr.tiles[pos.row][pos.col].let { tile ->
                    val state = tileStates[pos.row][pos.col]
                    if (!state.lightDirs.contains(dir)) {
                        state.lightDirs.add(dir)
                        tile.type.exitFor(dir).forEach { exitDir ->
                            beamEnters(pos.next(exitDir), exitDir)
                        }
                    }
                }
            }
        }

        fun energizedCount() = tileStates.sumOf { tr -> tr.count { it.energized() } }

    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val exp = BeamExperiment(Contraption(lines))
        exp.beamEnters(Position(0,0), RIGHT)
        return exp.energizedCount().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val con = Contraption(lines)
        return con.allEnteringBeams().maxOf { (position, direction) ->
            val exp = BeamExperiment(con)
            exp.beamEnters(position, direction)
            exp.energizedCount()
        }.toString()
    }

}