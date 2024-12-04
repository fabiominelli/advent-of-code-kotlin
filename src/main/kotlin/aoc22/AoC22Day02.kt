package aoc22

import Problem

class AoC22Day02: Problem(2, 2022, "Rock Paper Scissors") {


    enum class Move(val score:Int, val opponentId:String, val myId:String) {
        ROCK(1,"A", "X"),
        PAPER(2, "B", "Y"),
        SCISSORS(3, "C", "Z");
    }

    private val winMap = mapOf(
        Move.ROCK to Move.SCISSORS,
        Move.PAPER to Move.ROCK,
        Move.SCISSORS to Move.PAPER
    )

    private fun roundScore(opponentMove: Move, myMove: Move):Int {
        return myMove.score + when {
            myMove==opponentMove -> 3
            winMap[myMove]==opponentMove -> 6
            else -> 0
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    private fun roundScore1(s:String):Int {
        val (opponentMoveId, myMoveId) = s.split(" ")
        val opponentMove = Move.entries.first { it.opponentId==opponentMoveId }
        val myMove = Move.entries.first { it.myId==myMoveId }
        return roundScore(opponentMove, myMove)
    }

    override fun getFirstStarOutcome(lines: List<String>): String {
        return lines.sumOf { roundScore1(it) }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    private fun roundScore2(s:String):Int {
        val (opponentMoveId, myMoveId) = s.split(" ")
        val opponentMove = Move.entries.first { it.opponentId==opponentMoveId }

        val myMove = when(myMoveId) {
            "X" -> winMap[opponentMove]!!
            "Y" -> opponentMove
            "Z" -> Move.entries.first { winMap[it]==opponentMove }
            else -> throw IllegalArgumentException("Invalid move id")
        }
        return roundScore(opponentMove, myMove)
    }


    override fun getSecondStarOutcome(lines: List<String>): String {
        return lines.sumOf { roundScore2(it) }.toString()
    }

}