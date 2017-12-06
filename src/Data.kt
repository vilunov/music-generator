import java.lang.Math.floorDiv
import java.lang.Math.floorMod

class Chord(val steps: List<Int>, val scale: Scale) {
    fun midiNotes() : IntArray = steps.map { scale[it] }.toIntArray()
    fun equals(other: Chord) : Boolean = scale == other.scale && steps == other.steps
    operator fun get(idx: Int) : Int {
        val note: Int = floorMod(idx, 3)
        val octave: Int = floorDiv(idx, 3)
        return steps[note] + octave * 7
    }
}

object ScaleType {
    val Major = 0
    val Minor = 5
}

class Scale(val root: Int, val scaleOffset: Int) {
    val offsets = List(7 - scaleOffset, { root + offsets_major[it + scaleOffset] - offsets_major[scaleOffset] }) +
            List(scaleOffset, { root + 12 + offsets_major[it] - offsets_major[scaleOffset] })
    operator fun get(i: Int) : Int = offsets[floorMod(i, 7)] + 12 * floorDiv(i, 7)
    fun equals(other: Scale) : Boolean = root == other.root && scaleOffset == other.scaleOffset

    companion object {
        private val offsets_major = intArrayOf(0, 2, 4, 5, 7, 9, 11)
    }
}

class Rhythm(val chordDurations: List<Int>, val noteDurations: List<Int>) {
    private val collisions: HashMap<Int, Int> = HashMap()

    init {
        assert(noteDurations.isNotEmpty())
        assert(chordDurations.sum() == noteDurations.sum())
        var curNote = 0
        var curChord = 0
        var timeNote = 0
        var timeChord = 0
        while(curChord < chordDurations.size && curNote < noteDurations.size) {
            if(timeNote == timeChord)
                collisions.put(curNote, curChord)
            timeChord += chordDurations[curChord]
            curChord++
            while(timeNote < timeChord && curNote < noteDurations.size) {
                timeNote += noteDurations[curNote]
                curNote++
            }
        }
    }

    fun correspondingChord(idx: Int) : Int? = collisions[idx]
}

class Segment(val rhythm: Rhythm, val startingNote: Int, val endingNote: Int)