package aoc24

import Problem
import kotlin.math.abs

class AoC24Day25: Problem(25, 2024, "Code Chronicle") {

    class Lock(lines: List<String>) {
        val pins:Array<Int> = Array(5) { -1 }
        init {
            (0..4).forEach { col ->
                pins[col] = (0..5).filter {row -> lines[row][col] == '#' }.max()

            }
        }
        fun admitKey(key:Key):Boolean = (0..4).all { col -> pins[col] + key.heights[col] < 6 }
    }

    class Key(lines: List<String>) {
        val heights:Array<Int> = Array(5) { -1 }
        init {
            (0..4).forEach { col ->
                heights[col] = (0..5).filter { row -> lines[6-row][col] == '#' }.max()
            }
        }
    }

    private fun parse(lines: List<String>):Pair<List<Lock>,List<Key>> {
        val locks:MutableList<Lock> = mutableListOf()
        val keys:MutableList<Key> = mutableListOf()
        lines.joinToString("\n").split("\n\n").map { it.split("\n") }.forEach { item ->
            if (item[0]=="#####") {
                locks.add(Lock(item))
            } else {
                keys.add(Key(item))
            }
        }
        return Pair(locks,keys)
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val (locks,keys) = parse(lines)
        return locks.flatMap { l-> keys.filter { k -> l.admitKey(k) } }.count().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        return "n/a"
    }

}