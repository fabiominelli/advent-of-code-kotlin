package aoc24

import Problem
import java.math.BigInteger

class AoC24Day11: Problem(11, 2024, "Plutonian Pebbles") {

    class Stones(st: List<Int>) {
        constructor(line:String) : this(line.split(" ").map { it.toInt() })

        private val stoneList = st.toMutableList()

        private val cache: MutableMap<Pair<BigInteger, Int>, BigInteger> = mutableMapOf()
        private fun countStones(v: BigInteger, numBlink:Int):BigInteger {
            val sv = v.toString()
            if (numBlink==0) return 1.toBigInteger()
            if (Pair(v, numBlink) in cache) return cache[Pair(v, numBlink)]!!
            return when {
                v==0.toBigInteger() -> countStones(1.toBigInteger(), numBlink-1)
                sv.length % 2 == 0 -> {
                    val half = sv.length / 2
                    countStones(sv.substring(0, half).toBigInteger(), numBlink-1) + countStones(sv.substring(half).toBigInteger(), numBlink-1)
                }
                else -> countStones(v.times(2024.toBigInteger()), numBlink-1)
            }.also { cache[Pair(v, numBlink)] = it }
        }
        fun totalStonesAfter(numBlink: Int):BigInteger {
            return stoneList.sumOf { countStones(it.toBigInteger(), numBlink) }
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val stones = Stones(lines[0])
        return stones.totalStonesAfter(25).toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val stones = Stones(lines[0])
        return stones.totalStonesAfter(75).toString()
    }

}