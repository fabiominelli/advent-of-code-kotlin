import java.io.File


abstract class Problem(private val dayNumber:Int, private val year:Int, private val title: String = "n/a") {

    companion object {
        fun solve(problems:List<Problem>) {
            println()
            println ("==================================================================================================================")
            println ("|     |                                  ||          Part 1                  ||          Part 2                  |")
            println ("|     |                                  ||----------------------------------||----------------------------------|")
            println ("| Day |              Title               ||  Sample   | My Input             ||  Sample   | My Input             |")
            println ("|=====|==================================||===========|======================||===========|======================|")

            problems.forEach { p ->
                print ("| %3s |".format(p.dayNumber))
                print ("| %31s ||".format(p.title))
                print (" %9s |".format(p.solveForInput(true, star = 1)))
                print (" %20s ||".format(p.solveForInput(false, star = 1)))
                print (" %9s |".format(p.solveForInput(true, star = 2)))
                println (" %20s |".format(p.solveForInput(false, star = 2)))
            }

            println ("==================================================================================================================")
            println()
        }
    }

    private fun readFile(fileName:String):List<String>? =
        javaClass.classLoader?.getResource(fileName)?.let { File(it.toURI()).readLines() }


    private fun solveForInput(isSample: Boolean, star: Int): String {
        val lines = if (isSample) {
                        readFile("$year/day$dayNumber-sample.txt") ?: readFile("$year/day$dayNumber-sample-${star}.txt")
                    } else {
                        readFile("$year/day$dayNumber-myInput.txt")
                    } ?: throw Exception("Input not found")

        return if (star==1) getFirstStarOutcome(lines) else getSecondStarOutcome(lines)
    }

    abstract fun getFirstStarOutcome(lines:List<String>): String
    abstract fun getSecondStarOutcome(lines:List<String>): String
}