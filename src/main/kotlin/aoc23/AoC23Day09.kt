package aoc23

import Problem


class AoC23Day09: Problem(9, 2023) {

    class History(val values:List<Int>) {

        constructor(line:String): this(line.split(' ').map { it.toInt() })

        fun diffs():History {
            return History( values.zip(values.drop(1)).map { (v1, v2) -> v2-v1 } )
        }
        fun nextValue():Int {
            return if (values.all { it==0 }) 0
                else values.last() + diffs().nextValue()
        }
        fun previousValue():Int {
            return if (values.all { it==0 }) 0
            else values.first() - diffs().previousValue()
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return lines.sumOf { History(it).nextValue() }.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return lines.sumOf { History(it).previousValue() }.toString()
    }


}