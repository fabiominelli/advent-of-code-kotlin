package aoc24

import Problem
import aoc24.AoC24Day16.TileType.*
import aoc24.AoC24Day16.Direction.*

class AoC24Day16: Problem(16, 2024, "Reindeer Maze") {

    enum class Direction(val dx:Int, val dy:Int) {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0)
    }

    data class Position(val x:Int, val y:Int) {
        fun next(dir:Direction):Position = Position(x + dir.dx, y + dir.dy)
        override fun toString(): String {
            return "[$x,$y]"
        }
    }


    enum class TileType(val ch:Char) {
        WALL('#'), EMPTY('.'), START('S'), GOAL('E');
        companion object {
            fun fromChar(ch:Char):TileType = entries.first { it.ch == ch }
        }
    }

    class Tile(val type:TileType) {
        val scores = mutableMapOf<Direction,Int>()
    }


    data class Path(val position: Position, val direction: Direction, val rotate:Boolean, val score: Int, val originPaths:MutableSet<Path>) : Comparable<Path> {
        override fun compareTo(other: Path): Int {
            return compareValuesBy(this, other, Path::score,
                { it.position.x }, { it.position.y }, Path::direction, Path::rotate
            )
        }

        override fun toString(): String {
            return "$position $direction ${if (rotate) "R" else "S"} $score"
        }
    }

    class Maze(lines:List<String>) {
        val width = lines[0].length
        val height = lines.size
        private val pathsToExplore = sortedSetOf<Path>()
        private val exploredPaths = HashSet<Path>()

        private val tiles:Array<Array<Tile>> = Array(width) { x-> Array(height) { y->
            Tile(TileType.fromChar(lines[y][x])).also { cell ->
                if (cell.type== START) {
                    cell.scores[RIGHT] = 0
                    pathsToExplore.add(Path(Position(x, y), RIGHT,false, 1, mutableSetOf()))
                    pathsToExplore.add(Path(Position(x, y), RIGHT,true, 1000, mutableSetOf()))
                }
            }
        }}
        private fun at(pos:Position):Tile = tiles[pos.x][pos.y]

        val bestTiles = mutableSetOf<Position>()

        fun computeMinimumScore():Int {
            var minScore:Int? = null
            while (pathsToExplore.isNotEmpty()) {
//                println()
//                println(pathsToExplore)
//                println()

                val path = pathsToExplore.first()
                pathsToExplore.remove(path)
                exploredPaths.add(path)

                if (minScore!=null && path.score>minScore) break // no more best paths

                val dir = path.direction
//                println("Analyse path $path ..")
                val newPaths:MutableSet<Path> = mutableSetOf()
                if (path.rotate) {
                    Direction.entries.filter { it != dir }.forEach { newDir ->
                        newPaths += Path(path.position, newDir, false, path.score + 1, mutableSetOf(path))
                        }
                } else { // straight
                    val candidatePos = path.position.next(dir)
                    val candidateTile:Tile = at(candidatePos)
                    when (candidateTile.type) {
                        WALL, START -> {}
                        EMPTY -> {
                            candidateTile.scores[dir] = path.score
                            newPaths +=  Path(candidatePos, dir, false, path.score + 1, mutableSetOf(path))
                            newPaths +=  Path(candidatePos, dir, true, path.score + 1000, mutableSetOf(path))
                        }
                        GOAL -> {
                            when {
                                minScore == null -> minScore = path.score
                                minScore != path.score -> throw IllegalStateException("Should not happen")
                            }
                            addToBestTiles(path)
                        }
                    }
                }
                newPaths.forEach { newPath ->
                    val exploredPath = exploredPaths.firstOrNull { it.position == newPath.position && it.direction == newPath.direction && it.rotate == newPath.rotate }
                    if (exploredPath!=null) {
                        // suboptimal, forget it
                    } else {
                        val existingPathToExplore =
                            pathsToExplore.firstOrNull { it.position == newPath.position && it.direction == newPath.direction && it.rotate == newPath.rotate }
                        if (existingPathToExplore == null) {
                            pathsToExplore.add(newPath)
                        } else {
                            if (newPath.score == existingPathToExplore.score) {
                                existingPathToExplore.originPaths.add(path)
                            }
                        }
                    }
                }
            }
            return minScore!!
        }

        fun addToBestTiles(path: Path) {
            bestTiles.add(path.position)
            path.originPaths.forEach(::addToBestTiles)
        }


        fun print() {
            println()
            for (y in 0..<height) {
                for (x in 0..<width) {
                    print(tiles[x][y].type.ch)
                }
                println()
            }
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        if (!isSample) return "n/a"
        val maze = Maze(lines)
        return maze.computeMinimumScore().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        if (!isSample) return "n/a"
        val maze = Maze(lines)
        maze.computeMinimumScore()
        return (maze.bestTiles.size + 1).toString()
    }

}