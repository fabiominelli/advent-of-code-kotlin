package aoc24

import Problem

class AoC24Day14: Problem(14, 2024, "Restroom Redoubt") {

    data class Position(val x:Int, val y:Int)
    data class Velocity(val x:Int, val y:Int)

    class Robot(val initPos: Position, val vel: Velocity) {
        companion object {
            private val robotPattern = """p=([0-9]+),([0-9]+) v=(-?[0-9]+),(-?[0-9]+)""".toRegex()
        }

        var pos = initPos
        constructor(line: String) : this(
            robotPattern.matchEntire(line)?.destructured?.let { (x, y, _, _) -> Position(x.toInt(), y.toInt()) }
                ?: throw Exception("Invalid input"),
            robotPattern.matchEntire(line)?.destructured?.let { (_, _, vx, vy) -> Velocity(vx.toInt(), vy.toInt()) }
                ?: throw Exception("Invalid input")
        )

        override fun toString(): String {
            return "Robot(${pos.x},${pos.y}))"
        }
    }

    class Space(val width:Int, val height:Int, val robots:List<Robot>) {
        fun tick(seconds:Int) {
            robots.forEach {
                val positiveVelX = if (it.vel.x>0) it.vel.x else it.vel.x+width
                val positiveVelY = if (it.vel.y>0) it.vel.y else it.vel.y+height
                it.pos = Position(
                    (it.pos.x+seconds*positiveVelX)%width,
                    (it.pos.y+seconds*positiveVelY)%height
                )
            }
        }
        fun safetyFactor():Int {
            return robots
                .mapNotNull { quadrant(it.pos) }
                .groupingBy { it }
                .eachCount()
                .values
                .fold(1, {acc, i -> acc*i})
        }
        private fun quadrant(pos: Position):Int? {
            return when {
                pos.x<width/2 && pos.y<height/2 -> 1
                pos.x>width/2 && pos.y<height/2 -> 2
                pos.x<width/2 && pos.y>height/2 -> 3
                pos.x>width/2 && pos.y>height/2 -> 4
                else -> null
            }
        }

        fun isChristmasTree():Boolean {
            val grid = Array(height) { CharArray(width) {' '} }
            robots.forEach {
                grid[it.pos.y][it.pos.x] = '#'
            }
            (0..<height).forEach { y ->
                val line = (grid[y].joinToString(""))
                if (line.contains("#".repeat(10))) {
                    return true
                }
            }
            return false
        }
    }

    var isSample = false
    override fun setIssSample(b: Boolean) {
        isSample = b
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val space = Space(
            if (isSample) 11 else 101,
            if (isSample) 7 else 103,
            lines.map { Robot(it) })
        space.tick(100)
        return space.safetyFactor().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        if (isSample) {
            return "n/a"
        }
        val space = Space(101, 103, lines.map { Robot(it) })
        var count = 0
        while (true) {
            space.tick(1)
            count++
            if (space.isChristmasTree()) break
        }
        return count.toString()
    }

}