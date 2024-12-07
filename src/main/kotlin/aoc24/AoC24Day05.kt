package aoc24

import Problem

class AoC24Day05: Problem(5, 2024, "Print Queue") {


    class Rule(s:String) {
        val first:Int
        val second:Int
        init {
            val parts = s.split("|")
            first = parts[0].toInt()
            second = parts[1].toInt()
        }
        override fun toString():String {
            return "$first|$second"
        }
    }

    class Ruleset(private val rules:List<Rule>) {
        private val cachedRules = rules.map { it.toString() }.toSet()
        fun isCorrect(update:Update):Boolean {
            update.pages.zip(update.pages.drop(1)).forEach {
                if (cachedRules.contains("${it.second}|${it.first}")) {
                    return false
                }
            }
            return true
        }
        fun reorder(update:Update):Update {
            val result = mutableListOf<Int>()
            val remaining = update.pages.toMutableList()
            while (remaining.isNotEmpty()) {
                val first = this.findFirst(remaining)
                result.add(first)
                remaining.remove(first)
            }
            return Update(result)
        }
        private fun findFirst(items:List<Int>):Int {
            val first = items.find { it -> items.none { other -> cachedRules.contains("$other|$it") } }
            if (first==null) {
                throw Exception("No first found")
            }
            return first
        }
    }

    class Update(val pages: List<Int>) {
        constructor(s:String):this(s.split(",").map { it.toInt() })
        fun middleValue():Int {
            return pages[(pages.size-1) / 2]
        }
    }

    class Document(lines: List<String>) {
        private val breakIndex = lines.indexOf("")
        private val ruleSet = Ruleset(lines.take(breakIndex).map { Rule(it) })
        private val updates = lines.drop(breakIndex + 1).map { Update(it) }

        val correctUpdates:List<Update>
        val reorderedUpdates:List<Update>

        init {
            val correct:MutableList<Update> = mutableListOf()
            val reordered:MutableList<Update> = mutableListOf()
            updates.forEach {
                if (ruleSet.isCorrect(it)) {
                    correct.add(it)
                } else {
                    reordered.add(ruleSet.reorder(it))
                }
            }
            correctUpdates = correct
            reorderedUpdates = reordered
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val document = Document(lines)
        return document.correctUpdates.sumOf { it.middleValue() }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val document = Document(lines)
        return document.reorderedUpdates.sumOf { it.middleValue() }.toString()
    }

}