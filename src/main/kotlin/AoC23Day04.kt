class AoC23Day04: DayProblemSolver(4, 2023) {

    override fun isProblemSolutionBySumOfLines() = true



    //=======================
    //     FIRST STAR
    //=======================

    class Card(line:String) {
        private val winning:List<Int>
        private val found:List<Int>
        init {
            val (w,f) = line.split(":")[1].split("|")
            winning = w.trim().split(" +".toRegex()).map { it.toInt() }
            found = f.trim().split(" +".toRegex()).map { it.toInt() }
        }

        fun winCount() = found.count { winning.contains(it) }
    }

    override fun getFirstStarLineOutcome(line: String, row: Int): Int {
       return Card(line).winCount().let {if (it==0) 0 else (1 shl (it-1))}
    }



    //=======================
    //     SECOND STAR
    //=======================

    var cardCount: Array<Int> = arrayOf()

    override fun secondStarPreprocessInput(lines:List<String>) {
        cardCount = Array(lines.size) {1}
    }

    override fun getSecondStarLineOutcome(line: String, row: Int): Int {
        (1..Card(line).winCount()).forEach { delta -> cardCount[row+delta] += cardCount[row] }
        return cardCount[row]
    }

}