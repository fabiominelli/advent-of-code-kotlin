class AoC23Day01: DayProblemSolver(1, 2023) {

    override fun isProblemSolutionBySumOfLines() = true

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarLineOutcome(line: String, row: Int): Int {
        val firstDigit = line.first {it.isDigit()} - '0'
        val lastDigit = line.last() {it.isDigit()} - '0'
        return 10*firstDigit+lastDigit
    }

    //=======================
    //     SECOND STAR
    //=======================

    private val numberMap = mapOf(
        "1" to 1, "one" to 1, "2" to 2, "two" to 2, "3" to 3, "three" to 3, "4" to 4, "four" to 4,
        "5" to 5, "five" to 5, "6" to 6, "six" to 6, "7" to 7, "seven" to 7, "8" to 8, "eight" to 8,
        "9" to 9, "nine" to 9
    )

    private val numbersRegEx = numberMap.keys.joinToString("|", "(", ")" ).toRegex()
    private val numbersInvertedRegEx = numberMap.keys.map{ it.reversed()}.joinToString("|", "(", ")" ).toRegex()

    override fun getSecondStarLineOutcome(line: String, row: Int): Int {
        val firstDigit = numberMap[numbersRegEx.find(line)?.value?:'0']?:0
        val lastDigit = numberMap[(numbersInvertedRegEx.find(line.reversed())?.value?:"0").reversed()]?:0
        return 10*firstDigit+lastDigit
    }

}