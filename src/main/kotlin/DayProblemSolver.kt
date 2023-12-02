import java.io.File


abstract class DayProblemSolver(val dayNumber:Int, val year:Int) {

    fun solveFirstStarForExample() {
        solveForExample(1)
    }

    fun solveSecondStarForExample() {
        solveForExample(2)
    }

    private fun solveForExample(star:Int) {
        val result = solveForInput("day$dayNumber-$year-sample.txt", star)
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
        val result = solveForInput("day$dayNumber-$year-myInput.txt", star)
        println("==============================================================================")
        println("AoC $year - Day $dayNumber - Star $star - Solution for my input is: $result")
        println("==============================================================================")
    }

    private fun solveForInput(fileName: String, star: Int):Int {
        val resource = javaClass.classLoader?.getResource(fileName)
            ?: throw IllegalArgumentException("File not found: $fileName")

        val lines:List<String> = File(resource.toURI()).readLines()

        return lines.sumOf { if (star==1) getFirstStarLineOutcome(it) else getSecondStarLineOutcome(it) }
    }

    abstract fun getFirstStarLineOutcome(line:String):Int
    abstract fun getSecondStarLineOutcome(line:String):Int

}