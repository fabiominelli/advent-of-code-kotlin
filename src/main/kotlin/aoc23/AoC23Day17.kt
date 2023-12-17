package aoc23

import Problem
import aoc23.AoC23Day17.Direction.*
import java.util.PriorityQueue

class AoC23Day17: Problem(17, 2023, "Clumsy Crucible") {

    data class Position(val row:Int, val col:Int)
    enum class Direction(val symbol:Char) {
        UP('^'),
        RIGHT('>'),
        DOWN('v'),
        LEFT('<');

        fun opposite():Direction = when(this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }

    class City(lines:List<String>) {

        val height = lines.size
        val width = lines[0].length
        val blocksCount = height*width
        private val heatLoss = Array(height) { r-> Array(width) { c-> lines[r][c] - '0' }  }

        fun heatLossEnteringBlock(position:Position) = heatLoss[position.row][position.col]

        data class Neighbour(val position:Position, val direction:Direction)
        fun neighboursOf(p:Position) = buildSet<Neighbour> {
            if (p.row>0) add(Neighbour(Position(p.row-1,p.col), Direction.UP))
            if (p.row<height-1) add(Neighbour(Position(p.row+1,p.col), DOWN))
            if (p.col>0) add(Neighbour(Position(p.row,p.col-1), Direction.LEFT))
            if (p.col<width-1) add(Neighbour(Position(p.row,p.col+1), Direction.RIGHT))
        }

        private val heatLossHeuristic = HeatLossHeuristic(this)
        fun bestLossToTarget(pos:Position) = heatLossHeuristic.bestLossToTarget[pos]!!

    }

    // Compute minimum heat loss from any block to target, without considering any constraint on direction changing
    class HeatLossHeuristic(private val city:City) {

        data class PositionToExplore(val position:Position, val lossPassingFromHere:Int)

        val bestLossToTarget:MutableMap<Position,Int> = mutableMapOf()

        init {
            val exploreQueue:PriorityQueue<PositionToExplore> = PriorityQueue(compareBy { it.lossPassingFromHere })

            val start = Position(city.height-1,city.width-1)
            bestLossToTarget[start] = 0
            exploreQueue.add(PositionToExplore(start, city.heatLossEnteringBlock(start)))
            var found = 1

            while (found<city.blocksCount) {
                val posToExplore = exploreQueue.remove()
                city.neighboursOf(posToExplore.position).forEach { prevNeighbour->
                    if (!bestLossToTarget.containsKey(prevNeighbour.position)) {
                        found++
                        bestLossToTarget[prevNeighbour.position] = posToExplore.lossPassingFromHere
                        exploreQueue.add(PositionToExplore(
                            prevNeighbour.position,
                            posToExplore.lossPassingFromHere + city.heatLossEnteringBlock(prevNeighbour.position)
                        ))
                    }
                }
            }
        }

        fun print() {
            println()
            (0..<city.height).forEach { r->
                (0..<city.width).forEach { c->
                    print ("%4d ".format(bestLossToTarget[Position(r,c)]))
                }
                println()
            }
            println()
        }
    }

    class Solver(val city:City, val minConsecutive:Int, val maxConsecutive:Int) {

        data class Constraint(val direction: Direction, val min: Int, val max: Int)
        data class ExploreNode(
            val position: Position,
            val previous: ExploreNode?,
            val constraint: Constraint?,
            val heatLossTo: Int,
            val minimumTotalLoss: Int
        ) {
            override fun toString(): String {
                return "$position $constraint, heatLossTo=$heatLossTo, minimumTotalLoss=$minimumTotalLoss"
            }
        }

        private var solutionExploreNode:ExploreNode? = null
        fun minimumTotalHeatLoss() = solutionExploreNode?.heatLossTo ?: Int.MAX_VALUE

        init {
            val exploreNodes:MutableMap<Position,MutableSet<ExploreNode>> = mutableMapOf()
            val exploreQueue:PriorityQueue<ExploreNode> = PriorityQueue(compareBy { it.minimumTotalLoss })

            val start = Position(0, 0)
            val startConstraint:Constraint? = null
            val target = Position(city.height-1, city.width-1)

            exploreQueue.add(ExploreNode(start, null, startConstraint, 0, city.bestLossToTarget(start)))

            while (exploreQueue.isNotEmpty()) {
                val exploreNode = exploreQueue.remove()

                if (exploreNode.minimumTotalLoss >= minimumTotalHeatLoss()) {
                    // the queue contains no more exploration nodes that can be better
                    // of the solution found so far
                    break
                }

                city.neighboursOf(exploreNode.position).forEach { next ->

                    val keepingDirection = next.direction==exploreNode.constraint?.direction
                    if (   ( next.direction.opposite()==exploreNode.constraint?.direction)     // cannot turn 180 degrees
                        || ( keepingDirection && exploreNode.constraint!!.max<1 )              // cannot go straight
                        || ( !keepingDirection && (exploreNode.constraint?.min?:0) >0 )        // cannot turn
                        ) {
                        // discard neighbour
                    } else{

                        val constraint = if (next.direction==exploreNode.constraint?.direction)
                            Constraint(next.direction, exploreNode.constraint.min-1, exploreNode.constraint.max-1)
                        else
                            Constraint(next.direction, minConsecutive-1, maxConsecutive-1)

                        val heatLossToHere = exploreNode.heatLossTo + city.heatLossEnteringBlock(next.position)

                        var currentPositionNodes = exploreNodes[next.position]
                        if (currentPositionNodes==null) {
                            currentPositionNodes = mutableSetOf()
                            exploreNodes[next.position] = currentPositionNodes
                        }
                        if (currentPositionNodes.none {it.constraint?.direction==next.direction
                                                            && it.constraint.max >= constraint.max
                                                            && it.constraint.min <= constraint.min
                                                            && it.heatLossTo <=heatLossToHere
                                                            }) {
                                    val newExploreNode = ExploreNode(
                                        next.position,
                                        exploreNode,
                                        constraint,
                                        heatLossToHere,
                                        heatLossToHere+city.bestLossToTarget(next.position)
                                    )
                                    currentPositionNodes.add(newExploreNode)
                                    if (next.position==target && constraint.min<=0) {
                                        if (heatLossToHere < minimumTotalHeatLoss())
                                            solutionExploreNode = newExploreNode
                                    } else if (next.position!=start) {
                                        exploreQueue.add(newExploreNode)
                                    }
                                }
                    }
                }
            }
        }

        fun print() {
            val path:MutableMap<Position,Char> = mutableMapOf()
            var node = solutionExploreNode
            while (node!=null) {
                path[node.position] = node.constraint?.direction?.symbol?:'S'
                node = node.previous
            }

            println()
            (0..<city.height).forEach { r->
                (0..<city.width).forEach { c->
                    print ( path[Position(r,c)] ?: '.')
                }
                println()
            }
            println()
        }

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val city = City(lines)
        //city.heuristic.print()
        val solver = Solver(city, 1, 3)
        //solver.print()
        return solver.minimumTotalHeatLoss().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val city = City(lines)
        val solver = Solver(city, 4, 10)
        //solver.print()
        return solver.minimumTotalHeatLoss().toString()
    }

}