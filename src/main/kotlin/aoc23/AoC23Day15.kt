package aoc23

import Problem

fun hash(s:String) = s.fold(0) {acc,ch -> ((acc+ch.code)*17)%256}

class AoC23Day15: Problem(15, 2023, "Lens Library") {

    sealed class Step(val label:String) {
        companion object {
            fun from(s:String):Step {
                val (label, focal) = s.split("[-=]".toRegex())
                return if (s[label.length]=='-') {
                    RemoveStep(label)
                } else {
                    AddStep(label, focal.toInt())
                }
            }
        }
    }
    class RemoveStep(label:String):Step(label) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return if (other is RemoveStep) {
                label==other.label
            } else false
        }
    }

    class AddStep(label:String, val focalLength:Int):Step(label) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return if (other is AddStep) {
                label==other.label && focalLength==other.focalLength
            } else false
        }
    }

    data class Lens(val label:String, val focalLength:Int)

    class Box(val number:Int) {
        private val lenses:MutableList<Lens> = mutableListOf()

        fun apply(step:Step) {
            val idx = lenses.indexOfFirst { l -> l.label==step.label}
            when(step) {
                is RemoveStep -> {
                    if (idx>=0) lenses.removeAt(idx)
                }
                is AddStep -> {
                    val newLens = Lens(step.label, step.focalLength)
                    if (idx>=0) {
                        lenses[idx] = newLens
                    } else {
                        lenses.add(newLens)
                    }
                }
            }
        }

        fun focusingPower():Int = lenses.mapIndexed { idx, lens ->  number*(idx+1)*lens.focalLength}.sum()

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return lines[0].split(',').sumOf { hash(it) }.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val boxes:Array<Box> = Array(256) {idx -> Box(idx+1)}
        val steps = lines[0].split(',').map { Step.from(it) }
        steps.forEach { step ->
            boxes[hash(step.label)].apply(step)
        }
        return boxes.sumOf { it.focusingPower() }.toString()
    }

}