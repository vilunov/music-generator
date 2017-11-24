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

enum class ScaleType {
    Major, Dorian, Minor;

    fun offsets() = when (this) {
        Major -> majorOffsets
        Dorian -> dorianOffsets
        Minor -> minorOffsets
    }

    operator fun get(i: Int) : Int {
        val note: Int = floorMod(i, 7)
        val octave: Int = floorDiv(i, 7)
        return offsets()[note] + octave * 12
    }

    companion object {
        private val majorOffsets = intArrayOf(0, 2, 4, 5, 7, 9, 11).toList()
        private val dorianOffsets = intArrayOf(0, 2, 3, 5, 7, 9, 10).toList()
        private val minorOffsets = intArrayOf(0, 2, 3, 5, 7, 8, 10).toList()
    }
}

class Scale(val root: Int, val type: ScaleType) {
    fun midiNotes() : IntArray = type.offsets().map { it + root }.toIntArray()
    operator fun get(i: Int) : Int = type[i] + root
    fun equals(other: Scale) : Boolean = root == other.root && type == other.type
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