package aoc24

import Problem

class AoC24Day19: Problem(19, 2024, "Linen Layout") {


    private fun parse(lines: List<String>): Pair<List<String>, Set<String>> {
        val patterns = lines[0].split(", ").toSet()
        val designs = lines.drop(2)
        return Pair(designs, patterns)
    }

    private val designsCache:MutableMap<String, Long> = mutableMapOf()

    private fun countWays(design:String, patterns:Set<String>):Long {
        val cached = designsCache[design]
        if (cached!=null) return cached

        var count = 0L

        for (i in 1..design.length) {
            val left = design.substring(0, i)
            val right = design.substring(i)
            if (patterns.contains(left)) {
                if (right.isEmpty()) count++
                else {
                    count += countWays(right, patterns)
                }
            }
        }
        designsCache[design] = count
        return count
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        designsCache.clear()
        val (designs, patterns) = parse(lines)
        return designs.count { countWays(it, patterns)>0 }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        designsCache.clear()
        val (designs, patterns) = parse(lines)
        return designs.sumOf { countWays(it, patterns) }.toString()
    }

}