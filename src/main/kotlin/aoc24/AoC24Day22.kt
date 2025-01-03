package aoc24

import Problem

class AoC24Day22: Problem(22, 2024, "Monkey Market") {

    val iterations = 2000

    private val pruneVal = 16777216
    private fun nextSecret(n: Long): Long {
        var n1 = n
        n1 = (n1 xor (n1 * 64)) % pruneVal
        n1 = (n1 xor (n1 / 32)) % pruneVal
        n1 = (n1 xor (n1 * 2048)) % pruneVal
        return n1
    }


    private fun secretNumber(initial:Int):Long {
        var n = initial.toLong()
        for (i in 1..iterations) {
            n = nextSecret(n)
        }
        return n
    }

    private fun maximumBananas(initialSecrets:List<Int>):Int {
        val changesMap:MutableMap<List<Int>,Int> = mutableMapOf()
        initialSecrets.forEach { initial ->
            val secretChangesMap:MutableMap<List<Int>,Int> = mutableMapOf()
            var n = initial.toLong()
            var price = n.mod( 10)
            var changes:List<Int> = emptyList()
            for (i in 1..iterations) {
                val n1 = nextSecret(n)
                val prevPrice = price
                price = n1.mod( 10)
                changes = changes.drop(if (changes.size==4) 1 else 0).plus(price-prevPrice)
                if (changes.size == 4 && !secretChangesMap.containsKey(changes)) {
                    secretChangesMap[changes] = price
                }
                n = n1
            }
            secretChangesMap.forEach { (changes,price) ->
                changesMap[changes] = (changesMap[changes]?:0) + price
            }
        }
        return changesMap.maxOf { it.value }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return lines.map { it.toInt() }.map { secretNumber(it) }.sumOf { it }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        return maximumBananas(lines.map { it.toInt() }).toString()
    }

}