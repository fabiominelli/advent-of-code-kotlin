import java.io.File


abstract class DayProblemSolver(val dayNumber:Int, val year:Int) {

    fun solveFirstStarForExample() {
        solveForExample(1)
    }

    fun solveSecondStarForExample() {
        solveForExample(2)
    }

    private fun getFileName(year:Int, dayNumber:Int, set:String) = "$year/day$dayNumber-$set.txt"

    private fun solveForExample(star:Int) {
        val result = solveForInput(getFileName(year, dayNumber, "sample"), star)
        println("==============================================================================")
        println("AoC $year - Day $dayNumber - Star $star - Solution for sample is: $result")
        println("==============================================================================")
    }

    fun solveFirstStarForMyInput() {
        solveForMyInput(1)
    }

    fun solveSecondStarForMyInput() {
        solveForMyInput(2)
    }

    private fun solveForMyInput(star:Int) {
        val result = solveForInput(getFileName(year, dayNumber, "myInput"), star)
        println("==============================================================================")
        println("AoC $year - Day $dayNumber - Star $star - Solution for my input is: $result")
        println("==============================================================================")
    }

    private fun solveForInput(fileName: String, star: Int):Int {
        val resource = javaClass.classLoader?.getResource(fileName)
            ?: throw IllegalArgumentException("File not found: $fileName")

        val lines:List<String> = File(resource.toURI()).readLines()

        return if (star==1) {
            firstStarPreprocessInput(lines)
            if (isProblemSolutionBySumOfLines())
                lines.mapIndexed { row, line -> getFirstStarLineOutcome(line, row)}.sum()
            else
                getFirstStarOutcome(lines)
        } else {
            secondStarPreprocessInput(lines)
            if (isProblemSolutionBySumOfLines())
                lines.mapIndexed { row, line -> getSecondStarLineOutcome(line, row)}.sum()
            else
                getSecondStarOutcome(lines)
        }

    }

    abstract fun isProblemSolutionBySumOfLines():Boolean

    open fun firstStarPreprocessInput(lines:List<String>) {}

    open fun secondStarPreprocessInput(lines:List<String>) {}

    open fun getFirstStarLineOutcome(line: String, row: Int):Int = throw Exception("Not implemented for the problem")

    open fun getSecondStarLineOutcome(line: String, row: Int):Int = throw Exception("Not implemented for the problem")

    open fun getFirstStarOutcome(lines:List<String>):Int = throw Exception("Not implemented for the problem")

    open fun getSecondStarOutcome(lines:List<String>):Int = throw Exception("Not implemented for the problem")

}