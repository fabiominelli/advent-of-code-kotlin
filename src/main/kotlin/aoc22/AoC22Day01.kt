package aoc22

import Problem

class AoC22Day01: Problem(1, 2022, "Calorie Counting") {

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val groupSums = elfSums(lines)
        return groupSums.maxOrNull().toString()
    }


    private fun elfSums(lines: List<String>): List<Int> {
        // split the list into a list of sublists using empty lines as separators
        val groups = lines.joinToString("\n").split("\n\n").map { it.split("\n") }

        // for each groups item, parse its lines into integers and map to the sum of the group
        val groupSums = groups.map { gr -> gr.sumOf { it.toInt() } }
        return groupSums
    }


    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val groupSums = elfSums(lines)
        return groupSums.sortedDescending().take(3).sum().toString()
    }

}