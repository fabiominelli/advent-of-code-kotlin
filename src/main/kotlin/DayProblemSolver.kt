import java.io.File


abstract class DayProblemSolver(private val dayNumber:Int, private val year:Int) {

    private fun getFileName(year:Int, dayNumber:Int, set:String) = "$year/day$dayNumber-$set.txt"

    fun solveForExample(star:Int) = solveForInput(getFileName(year, dayNumber, "sample-$star"), star)

    fun solveForMyInput(star:Int) = solveForInput(getFileName(year, dayNumber, "myInput"), star)

    private fun solveForInput(fileName: String, star: Int): String {
        val resource = javaClass.classLoader?.getResource(fileName)
            ?: throw IllegalArgumentException("File not found: $fileName")

        val lines:List<String> = File(resource.toURI()).readLines()

        return if (star==1) {

            firstStarPreprocessInput(lines)
            if (isProblemSolutionBySumOfLines())
                lines.mapIndexed { row, line -> getFirstStarLineOutcome(line, row)}.sum().toString()
            else
                getFirstStarOutcome(lines)
        } else {

            secondStarPreprocessInput(lines)
            if (isProblemSolutionBySumOfLines())
                lines.mapIndexed { row, line -> getSecondStarLineOutcome(line, row)}.sum().toString()
            else
                getSecondStarOutcome(lines)
        }

    }

    open fun isProblemSolutionBySumOfLines():Boolean = false


    open fun firstStarPreprocessInput(lines:List<String>) {}

    open fun getFirstStarLineOutcome(line: String, row: Int):Int = throw Exception("Not implemented for the problem")

    open fun getFirstStarOutcome(lines:List<String>): String = throw Exception("Not implemented for the problem")


    open fun secondStarPreprocessInput(lines:List<String>) {}

    open fun getSecondStarLineOutcome(line: String, row: Int):Int = throw Exception("Not implemented for the problem")

    open fun getSecondStarOutcome(lines:List<String>): String = throw Exception("Not implemented for the problem")

}