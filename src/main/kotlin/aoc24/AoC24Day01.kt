package aoc24

import Problem
import kotlin.math.abs

class AoC24Day01: Problem(1, 2024, "Historian Hysteria") {

    class Locations(lines:List<String>) {
        private val list1 = mutableListOf<Int>()
        private val list2 = mutableListOf<Int>()
        init {
            for (line in lines) {
                val parts = line.split("   ")
                list1.add(parts[0].toInt())
                list2.add(parts[1].toInt())
            }
        }
        fun pairs() = list1.sorted().zip(list2.sorted())
        fun similarityIndex() = list1.sumOf { first -> first*list2.count { second -> first==second } }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val locations = Locations(lines)
        return locations.pairs().sumOf { abs(it.first - it.second) }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val locations = Locations(lines)
        return locations.similarityIndex().toString()
    }

}