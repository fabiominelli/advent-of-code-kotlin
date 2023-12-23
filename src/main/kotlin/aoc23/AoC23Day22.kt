package aoc23

import Problem
import java.util.*

class AoC23Day22: Problem(22, 2023, "Sand Slabs") {

    data class Position(val x:Int, val y:Int, val z:Int) {
        fun descend(deltaZ:Int) = Position(x,y,z-deltaZ)
    }
    data class Brick(val first:Position, val last:Position, val supportedBy:List<Brick>) {
        companion object {
            fun from(line:String):Brick {
                val (f,l) = line.split("~").map { pos ->
                    val (x,y,z) = pos.split(",").map(String::toInt)
                    Position(x,y,z)
                }
                return Brick(f,l, emptyList())
            }
        }
        fun overlap(other:Brick) =
            // x range overlaps
            first.x<=other.last.x && other.first.x<=last.x  &&
            // y range overlaps
            first.y<=other.last.y && other.first.y<=last.y

    }
    class Snapshot(val bricks:List<Brick>) {
        companion object {
            fun from(lines:List<String>):Snapshot = Snapshot(lines.map { Brick.from(it) })
        }

        fun fall():Pair<Snapshot,Int> {
            val stillFalling = PriorityQueue<Brick>(compareBy { it.first.z })
            stillFalling.addAll(bricks)
            val afterFallBricks:MutableList<Brick> = mutableListOf()

            var fallenCount = 0
            while(stillFalling.isNotEmpty()) {
                val brick = stillFalling.remove()
                val newSupportingZ = afterFallBricks.filter { it.overlap(brick) }.maxOfOrNull { it.last.z } ?: 0
                val supportingBricks = afterFallBricks.filter { it.overlap(brick) && it.last.z==newSupportingZ}
                val deltaZ = brick.first.z - newSupportingZ - 1
                afterFallBricks.add(Brick(brick.first.descend(deltaZ), brick.last.descend(deltaZ), supportingBricks))
                if (deltaZ>0) fallenCount++
            }

            return Pair(Snapshot(afterFallBricks),fallenCount)
        }

        fun safeDisintegratingBlocks():List<Brick> =
            bricks.filter { candidate -> bricks.none { it.supportedBy==listOf(candidate) } }

        fun minus(brick:Brick):Snapshot = Snapshot(bricks.minus(brick))
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val fallingSnapshot = Snapshot.from(lines)
        val (afterFall, _) = fallingSnapshot.fall()
        return afterFall.safeDisintegratingBlocks().size.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val fallingSnapshot = Snapshot.from(lines)
        val (afterFall, _) = fallingSnapshot.fall()
        val res = afterFall.bricks.sumOf {brick ->
            afterFall.minus(brick).let {snap ->
                val (_, fallenCount) = snap.fall()
                fallenCount
            }
        }
        return res.toString()
    }

}