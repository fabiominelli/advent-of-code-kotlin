package aoc23

import Problem

class AoC23Day07: Problem(7, 2023, "Camel Cards") {

    enum class Category(val signature:String) {
        HIGH_CARD("11111"),
        ONE_PAIR("2111"),
        TWO_PAIR("221"),
        THREE_OF_A_KIND("311"),
        FULL_HOUSE("32"),
        FOUR_OF_A_KIND("41"),
        FIVE_OF_A_KIND("5");

        companion object {
            fun forSignature(signature: String): Category {
                return entries.first { it.signature == signature }
            }
        }
    }


    enum class Card {
        C_JOKER,  // Joker card in Part 2 only, has minimum value
        C2, C3, C4, C5, C6, C7, C8, C9, CT,
        CJ, // never present in Part 2 (all J are transformed to jokers)
        CQ, CK, CA;

        companion object {
            fun forChar1(ch: Char): Card {
                return valueOf("C$ch")
            }
            fun forChar2(ch: Char): Card {
                return if (ch=='J') C_JOKER
                    else valueOf("C$ch")
            }
        }
    }



    //=======================
    //     FIRST STAR
    //=======================

    class Hand(private val label:String, val bid:Int):Comparable<Hand>{

        constructor(line:String):this(line.split(" ")[0], line.split(" ")[1].toInt())

        private fun getSignature():String =
            // compute hand signature, which is a String with descending char frequencies
            // e.g. "5", "311", "221"  (sum of digits in signature is always 5)
            label.groupBy { it }.map { it.value.size }.sortedDescending().joinToString("") { it.toString() }

        private val category: Category = Category.forSignature(getSignature())

        override fun compareTo(other: Hand): Int {
            return (this.category.compareTo(other.category)).let { cmp ->
                if (cmp!=0) cmp// first criteria is category ranking
                else label.zip(other.label)  // otherwise, the first same-index and different chars from the two labels
                    .map { (l1, l2) -> Card.forChar1(l1).compareTo(Card.forChar1(l2)) }.first { it!=0 } }
        }
    }


    override fun getFirstStarOutcome(lines: List<String>): String {
        val hands = lines.map { Hand(it) }.sorted()
        return hands.foldIndexed(0) {index, acc, hand -> acc + (index+1)*hand.bid}.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    class HandWithJoker(private val label:String, val bid:Int):Comparable<HandWithJoker>{
        constructor(line:String):this(line.split(" ")[0], line.split(" ")[1].toInt())

        private fun getEffectiveSignature():String {
            val jokersCount = label.count { it=='J' }
            return if (jokersCount==5) {
                "5" // special case, all jokers
            } else {
                label.filter { it != 'J' } // remove jokers
                    .groupBy { it }
                    .map { it.value.size }.sortedDescending() // list of descending frequencies
                    .mapIndexed { index, counter ->
                        if (index == 0) counter + jokersCount else counter // add joker count to first (higher) frequency
                    }
                    .joinToString("")  // this is the "corrected" signature
            }
        }

        private val category: Category = Category.forSignature(getEffectiveSignature())

        override fun compareTo(other: HandWithJoker): Int {
            return (this.category.compareTo(other.category)).let { cmp ->
                if (cmp!=0) cmp
                else label.zip(other.label)
                    .map { (l1, l2) -> Card.forChar2(l1).compareTo(Card.forChar2(l2)) }.first { it!=0 } }
        }
    }

    override fun getSecondStarOutcome(lines: List<String>): String {
        val hands = lines.map { HandWithJoker(it) }.sorted()
        return hands.foldIndexed(0) {index, acc, hand -> acc + (index+1)*hand.bid}.toString()
    }

}