class AoC23Day01: DayProblemSolver(1, 2023) {


    private val m = mapOf(
        "1" to 1, "one" to 1, "2" to 2, "two" to 2, "3" to 3, "three" to 3, "4" to 4, "four" to 4,
        "5" to 5, "five" to 5, "6" to 6, "six" to 6, "7" to 7, "seven" to 7, "8" to 8, "eight" to 8,
        "9" to 9, "nine" to 9
    )

    override fun getFirstStarLineOutcome(line:String): Int {
        // Implementation lost
        return 0
    }

    override fun getSecondStarLineOutcome(line: String): Int {
        val regEx = m.keys.joinToString("|", "(", ")" ).toRegex()
        val firstDigit = m[regEx.find(line)?.value?:'0']?:0
        val regExInverted = m.keys.map{ it.reversed()}.joinToString("|", "(", ")" ).toRegex()
        val lastDigit = m[(regExInverted.find(line.reversed())?.value?:"0").reversed()]?:0

        return 10*firstDigit+lastDigit
    }

}