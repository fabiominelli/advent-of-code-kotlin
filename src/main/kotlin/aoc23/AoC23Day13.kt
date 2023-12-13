package aoc23

import Problem


class AoC23Day13: Problem(13, 2023, "Point of Incidence") {

    companion object {
        fun isReflectedAroundPos(line:String, pos:Int):Boolean {
            return line.substring(pos).zip(line.substring(0..<pos).reversed()).all { (a, b) -> a==b }
        }

        fun errorsInReflectionAroundPos(line:String, pos:Int):Int {
            return line.substring(pos).zip(line.substring(0..<pos).reversed()).count { (a, b) -> a!=b }
        }
    }


    class Pattern(val lines:List<String>) {

        private val height = lines.size
        private val width = lines[0].length

        private fun transposed():Pattern {
            return Pattern( (0..<width).map { col->
                (0..<height).map { row ->
                    lines[row][col] }.joinToString("") })
        }

        private fun verticalReflection():Int {
            return (1..<width).firstOrNull {pos ->
                lines.indices.all { isReflectedAroundPos(lines[it], pos) }} ?: 0
        }

        private fun verticalReflectionWithErrors(errorCount:Int):Int {
            val perfectIndex = verticalReflection()
            return (1..<width).firstOrNull {pos ->
                pos != perfectIndex &&
                lines.indices.sumOf { errorsInReflectionAroundPos(lines[it], pos) } == errorCount
            } ?:0
        }

        fun horizontalReflection() = transposed().verticalReflection()
        fun horizontalReflectionWithErrors(errorCount:Int) = transposed().verticalReflectionWithErrors(errorCount)

        fun summarize():Int {
            return verticalReflection() + 100*horizontalReflection()
        }
        fun summarizeWithErrors(countError:Int):Int {
            return verticalReflectionWithErrors(countError) + 100*horizontalReflectionWithErrors(countError)
        }
    }

    private fun readPatterns(lines:List<String>):List<Pattern> {
        val emptyLinesIndexes = listOf(-1) + lines.indices.filter { lines[it].isEmpty() } + listOf(lines.size)
        return emptyLinesIndexes.zip(emptyLinesIndexes.drop(1))
            .map { (after, before) -> Pattern(lines.subList(after+1, before)) }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return readPatterns(lines).sumOf { it.summarize() }.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return readPatterns(lines).sumOf { it.summarizeWithErrors(1) }.toString()
    }

}