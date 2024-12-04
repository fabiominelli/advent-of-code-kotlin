package aoc22

import Problem

class AoC22Day03: Problem(3, 2022, "Rucksack Reorganization") {


    private fun itemPriority(item:Char):Int = if (item.isLowerCase()) item - 'a' + 1 else item - 'A' + 27

    class RuckSack(val line:String) {
        val all = line
        val first = line.substring(0..<line.length/2)
        val second = line.substring(line.length/2)


        fun errorItem():Char {
            val sharedItems = first.filter { second.contains(it) }
            return sharedItems[0]
        }
    }

    //=======================
    //     FIRST STAR
    //=======================


    override fun getFirstStarOutcome(lines: List<String>): String {
        val rucksacks = lines.map { RuckSack(it) }
        return rucksacks.sumOf { itemPriority(it.errorItem()) }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================

    private fun badgeItemOfGroup(group:List<RuckSack>):Char {
        val sharedItems = group[0].all.filter { group[1].all.contains(it) && group[2].all.contains(it) }
        return sharedItems[0]
    }

    override fun getSecondStarOutcome(lines: List<String>): String {
        val rucksacks = lines.map { RuckSack(it) }
        val groups = rucksacks.chunked(3)
        return groups.sumOf { itemPriority(badgeItemOfGroup(it)) }.toString()
    }

}