package aoc24

import Problem

class AoC24Day12: Problem(12, 2024, "Garden Groups") {

    enum class Direction(val dx:Int, val dy:Int) {
        NORTH(0,-1),
        SOUTH(0,1),
        EAST(1,0),
        WEST(-1,0)
    }

    data class Position(val x: Int, val y: Int) {
        override fun toString(): String {
            return "($x,$y)"
        }
    }


    class Plot(val plantType:Char) {
        var region:Region? = null
        var fenceDirections = mutableSetOf<Direction>()
    }
    class Region(val plantType:Char, val positions:MutableSet<Position>) {
        var numSides = 0
    }

    class Garden(val lines: List<String>) {

        private val regions:MutableSet<Region> = mutableSetOf()

        private val remaining:MutableSet<Position> = mutableSetOf()
        private val grid:Array<Array<Plot>> = Array(lines[0].length) {
                x -> Array(lines.size) {
                    y -> Plot(lines[y][x]).also { remaining.add(Position(x,y)) }
            }
        }

        private fun at(position:Position):Plot {
            return grid[position.x][position.y]
        }

        init {
            while (remaining.isNotEmpty()) {
                val regionStart = remaining.first()
                val newRegion = Region(at(regionStart).plantType, mutableSetOf(regionStart))
                at(regionStart).region = newRegion
                remaining.remove(regionStart)
                regions.add(newRegion)
                val plantType = at(regionStart).plantType

                val toCheck = mutableSetOf(regionStart)
                while (toCheck.isNotEmpty()) {
                    val expanding = toCheck.first()
                    toCheck.remove(expanding)

                    val regionNeighbours:MutableSet<Position> = mutableSetOf()

                    Direction.entries.forEach {
                        val neighbourPos = Position(expanding.x+it.dx, expanding.y+it.dy)
                        if (neighbourPos.x >= 0 && neighbourPos.x < grid.size
                                && neighbourPos.y >= 0 && neighbourPos.y < grid[0].size
                                && at(neighbourPos).plantType == plantType) {
                            regionNeighbours.add(neighbourPos)
                        } else {
                            at(expanding).fenceDirections.add(it)
                        }
                    }

                    regionNeighbours
                        .filter { it in remaining }
                        .forEach { neighbour ->
                            newRegion.positions.add(neighbour)
                            at(neighbour).region = newRegion
                            remaining.remove(neighbour)
                            toCheck.add(neighbour)
                        }
                }
            }
        }


        private fun computeRegionSides() {
            // horizontal sides
            for (y in 0..<grid[0].size) {
                var previousNorthFence:Char? = null
                var previousSouthFence:Char? = null
                for (x in grid.indices) {
                    val plot = at(Position(x,y))
                    if (plot.fenceDirections.contains(Direction.NORTH) && previousNorthFence!=plot.plantType) {
                        plot.region!!.numSides++
                    }
                    previousNorthFence = if (plot.fenceDirections.contains(Direction.NORTH)) plot.plantType else null
                    if (plot.fenceDirections.contains(Direction.SOUTH) && previousSouthFence!=plot.plantType) {
                        plot.region!!.numSides++
                    }
                    previousSouthFence = if (plot.fenceDirections.contains(Direction.SOUTH)) plot.plantType else null
                }
            }
            // vertical sides
            for (x in grid.indices) {
                var previousEastFence:Char? = null
                var previousWestFence:Char? = null
                for (y in 0..<grid[0].size) {
                    val plot = at(Position(x,y))
                    if (plot.fenceDirections.contains(Direction.WEST) && previousWestFence!=plot.plantType) {
                        plot.region!!.numSides++
                    }
                    previousWestFence = if (plot.fenceDirections.contains(Direction.WEST)) plot.plantType else null
                    if (plot.fenceDirections.contains(Direction.EAST) && previousEastFence!=plot.plantType) {
                        plot.region!!.numSides++
                    }
                    previousEastFence = if (plot.fenceDirections.contains(Direction.EAST)) plot.plantType else null
                }
            }
        }

        fun totalPrices1():Int {
            return regions.sumOf { region ->
                region.positions.size * region.positions.sumOf { pos -> at(pos).fenceDirections.size }
            }
        }

        fun totalPrices2():Int {
            computeRegionSides()
            return regions.sumOf { region ->
                region.positions.size * region.numSides
            }
        }

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val garden = Garden(lines)
        return garden.totalPrices1().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val garden = Garden(lines)
        return garden.totalPrices2().toString()
    }

}