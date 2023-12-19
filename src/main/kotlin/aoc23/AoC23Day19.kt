package aoc23

import Problem
import aoc23.AoC23Day19.Condition.Companion.countCombinationsForConditions
import aoc23.AoC23Day19.Diseq.*
import kotlin.math.max
import kotlin.math.min

class AoC23Day19: Problem(19, 2023, "Aplenty") {

    sealed class Condition {

        companion object {
            fun countCombinationsForConditions(conditions:List<Condition>):Long {
                val count:Long = "xmas".fold(1.toLong()) { acc, category ->
                    val res:Long = acc * countCombinationsForCategoryAndConditions(category,conditions)
                    res
                }
                return count
            }
            private fun countCombinationsForCategoryAndConditions(category:Char, conditions:List<Condition>):Int {
                data class Accumulator(val lowest:Int, val highest:Int)
                val categoryConditions = conditions.filter { it is CheckCondition && it.category==category }
                return categoryConditions.fold(Accumulator(1,4000)) { acc, condition ->
                    when(condition) {
                        is Always -> acc
                        is Never -> Accumulator(0,-1)
                        is CheckCondition -> when(condition.sign) {
                            GREATER_THAN -> Accumulator(max(condition.threshold+1, acc.lowest), acc.highest)
                            LESS_THAN -> Accumulator(acc.lowest, min(condition.threshold-1, acc.highest))
                            GREATER_OR_EQUAL -> Accumulator(max(condition.threshold, acc.lowest), acc.highest)
                            LESS_OR_EQUAL -> Accumulator(acc.lowest, min(condition.threshold, acc.highest))
                        }
                    }
                }.let {
                    if (it.highest>=it.lowest)
                        it.highest-it.lowest+1
                    else 0}
            }
        }
        abstract fun appliesTo(part:Part):Boolean
        abstract fun negate():Condition
    }
    data object Always:Condition() {
        override fun appliesTo(part: Part) = true
        override fun negate() = Never
    }

    data object Never: Condition() {
        override fun appliesTo(part: Part) = false
        override fun negate() = Always
    }

    enum class Diseq { GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL, LESS_OR_EQUAL;

        fun negate(): Diseq =
            when(this) {
                GREATER_THAN -> LESS_OR_EQUAL
                LESS_THAN -> GREATER_OR_EQUAL
                GREATER_OR_EQUAL -> LESS_THAN
                LESS_OR_EQUAL -> GREATER_THAN
            }
    }

    class CheckCondition(val category:Char, val sign:Diseq, val threshold:Int):Condition() {

        companion object {
            private val regex = "([xmas])([><])(\\d+)".toRegex()
            fun from(str:String):CheckCondition {
                val (c,s,t) = regex.find(str)!!.groupValues.drop(1)
                return CheckCondition(c[0], if (s[0]=='>') GREATER_THAN else LESS_THAN, t.toInt())
            }
        }

        override fun appliesTo(part: Part) = when(sign) {
            GREATER_THAN -> part.attrs[category]!! > threshold
            LESS_THAN -> part.attrs[category]!! < threshold
            GREATER_OR_EQUAL -> part.attrs[category]!! >= threshold
            LESS_OR_EQUAL -> part.attrs[category]!! <= threshold
        }

        override fun negate(): CheckCondition = CheckCondition(category, sign.negate(), threshold)

        override fun toString(): String {
            return "$category $sign $threshold"
        }
    }


    sealed class Outcome {

        companion object {
            fun of(s:String):Outcome {
                return when(s) {
                    "A" -> Accepted
                    "R" -> Rejected
                    else -> GotoWorkflow(s)
                }
            }
        }
    }
    class GotoWorkflow(val wfName:String): Outcome() {
        override fun toString(): String = wfName
    }
    sealed class FinalOutcome:Outcome()
    data object Accepted : FinalOutcome() {
        override fun toString(): String = "Accepted"
    }
    data object Rejected : FinalOutcome() {
        override fun toString(): String = "Rejected"
    }


    class Rule(s:String) {
        val condition:Condition
        val outcome:Outcome
        init {
            val s1 = if (s.contains(":")) s else ":$s"
            val spl = s1.split(":")
            condition = if (spl[0].isEmpty()) Always else CheckCondition.from(spl[0])
            outcome = Outcome.of(spl[1])
        }
        fun outcome(part:Part):Outcome? = if (condition.appliesTo(part)) outcome else null
        override fun toString(): String {
            return "$condition=>$outcome"
        }

    }

    class Workflow(line:String) {
        val name:String = line.split("{")[0]
        val rules:List<Rule> = line.split("[{}]".toRegex())[1].split(",").map { Rule(it) }

        fun outcome(part:Part):Outcome = rules.firstNotNullOf { it.outcome(part) }

        override fun toString(): String {
            return "$name => $rules"
        }

    }

    class System(lines:List<String>) {
        val workflows = lines.associate {
            val w = Workflow(it)
            Pair(w.name, w)
        }

        val inWorkflow = workflows["in"]!!

        fun isAccepted(part:Part):Boolean {
            var nextWorkflow = inWorkflow
            var finalOutcome:FinalOutcome? = null
            do {
                val outcome = nextWorkflow.outcome(part)
                when (outcome) {
                    is GotoWorkflow -> nextWorkflow = workflows[outcome.wfName]!!
                    is FinalOutcome -> finalOutcome = outcome
                }
            } while (finalOutcome==null)
            return finalOutcome==Accepted
        }
        fun rate(part:Part):Int = if (isAccepted(part)) part.rating else 0

        fun countCombinations():Long = countCombinationsWhen("in", emptyList())

        private fun countCombinationsWhen(wfName:String, conditions:List<Condition>):Long {
            data class Accumulator(val combinations:Long, val conditions:List<Condition>)

            return workflows[wfName]!!.rules.fold(Accumulator(0, conditions)) { acc, rule ->
                val andConditions = acc.conditions + rule.condition
                val notConditions = acc.conditions + rule.condition.negate()
                when(rule.outcome) {
                    is Accepted -> Accumulator(
                        acc.combinations + countCombinationsForConditions(andConditions),
                        notConditions)
                    is Rejected -> Accumulator(
                        acc.combinations,
                        notConditions)
                    is GotoWorkflow -> Accumulator(
                        acc.combinations + countCombinationsWhen(rule.outcome.wfName, andConditions),
                        notConditions)
                }
            }.combinations
        }
    }


    class Part(line:String) {
        val attrs:Map<Char, Int>
        init {
            attrs = buildMap {
                line.substring(1..line.length-2).split(",").forEach {
                    put(it[0], it.substring(2).toInt())
                }
            }
        }
        val rating = attrs.map { it.value }.sum()
        override fun toString(): String {
            return "$attrs  [$rating]"
        }

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val splitLineIndex = lines.indexOf("")
        val system = System(lines.subList(0,splitLineIndex))
        val parts:List<Part> = lines.subList(splitLineIndex+1, lines.size).map { Part(it) }
        return parts.sumOf { system.rate(it) }.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val splitLineIndex = lines.indexOf("")
        val system = System(lines.subList(0,splitLineIndex))
        return system.countCombinations().toString()
    }

}