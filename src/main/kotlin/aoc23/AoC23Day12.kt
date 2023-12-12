package aoc23

import Problem


class AoC23Day12: Problem(12, 2023, "Hot Springs") {

    private val memory:MutableMap<String, Long> = mutableMapOf()

    class Row(line:String, unfold:Int=1) {

        val springs = Array(unfold) {line.split(' ')[0]}.joinToString("?")
        val groups = Array(unfold) {line.split(' ')[1]}.joinToString(",").split(',').map { it.toInt() }

        override fun toString(): String {
            return "$springs  $groups"
        }
    }


    fun countArrangements(line:String, expand:Int=1):Long = countArrangements(Row(line,expand))

    private fun countArrangements(row:Row):Long = countArrangements(row.springs, row.groups)

    fun countArrangements(springs:String, groups:List<Int>):Long {

        val line = "$springs ${groups.joinToString(",")}"

        // try memoization
        var count:Long? = memory[line]
        if (count!=null) return count

        // nope.. compute the number of arrangements
        count = 0.toLong()
        val minumumSizeRequiredByGroups = groups.sum()+groups.size-1

        if (groups.isEmpty()) {
            // no broken springs to be placed; there is one arrangement if there is no broken spring in the line
            count = if (springs.count { it=='#'}==0) 1 else 0

        } else if (springs.count { "#?".contains(it) } < groups.sum()) {  // not enough springs in the line
            count = 0
        } else {
            run breaking@ {
                // exploring the solution space by iterating on the position of the first broken spring in the line
                // the position can
                (0..springs.length-minumumSizeRequiredByGroups).forEach { idx ->
                    // count arrangements when first group starts from index idx
                    if (idx>0 && springs.substring(0..<idx).contains('#')) {
                        // cannot skip a broken spring in the line
                        return@breaking
                    } else if (springs.substring(idx, idx+groups[0]).all { "#?".contains(it) }) {
                        if (springs.substring(idx).length==groups[0]) {
                            if (groups.size==1) count++
                        } else if (springs[idx+groups[0]]!='#') {
                            val arrangements = countArrangements(springs.substring(idx+groups[0]+1), groups.drop(1))
                            count += arrangements
                        } else {
                            // impossible
                        }
                    }
                }
            }
        }
        // memoization: will never count the arrangements for the same springs+groups again
        memory[line] = count
        return count
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return lines.map { Row(it) }.sumOf { row -> countArrangements(row.springs, row.groups)}.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return lines.map { Row(it,5) }.sumOf { row -> countArrangements(row.springs, row.groups)}.toString()
    }

}