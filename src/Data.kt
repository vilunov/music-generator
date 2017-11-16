import java.lang.Math.floorDiv
import java.lang.Math.floorMod

enum class ChordType {
    MajorTriad, MinorTriad;

    fun offsets() : List<Int> {
        return when(this) {
            MajorTriad -> majorOffsets
            MinorTriad -> minorOffsets
        }
    }

    operator fun get(i: Int) : Int {
        val note: Int = floorMod(i, 3)
        val octave: Int = floorDiv(i, 3)
        return offsets()[note] + octave * 12
    }

    companion object {
        private val majorOffsets = intArrayOf(0, 4, 7).toList()
        private val minorOffsets = intArrayOf(0, 3, 7).toList()
    }
}

class Chord(val root: Int, val type: ChordType) {
    fun midiNotes() : IntArray {
        return type.offsets().map { i -> i + root }.toIntArray()
    }

    fun equals(other: Chord) : Boolean {
        return root == other.root && type == other.type
    }

    operator fun get(idx: Int) : Int {
        return type[idx] + root
    }
}

enum class ScaleType {
    Major, Minor;

    fun offsets() : List<Int> {
        return when (this) {
            Major -> majorOffsets
            Minor -> minorOffsets
        }
    }

    operator fun get(i: Int) : Int {
        val note: Int = floorMod(i, 7)
        val octave: Int = floorDiv(i, 7)
        return offsets()[note] + octave * 12
    }

    companion object {
        private val majorOffsets = intArrayOf(0, 2, 4, 5, 7, 9, 11).toList()
        private val minorOffsets = intArrayOf(0, 2, 3, 5, 7, 8, 10).toList()
    }
}

class Scale(val root: Int, val type: ScaleType) {
    fun midiNotes() : IntArray {
        return type.offsets().map { i -> i + root }.toIntArray()
    }

    operator fun get(i: Int) : Int {
        return type[i] + root
    }

    fun equals(other: Scale) : Boolean {
        return root == other.root && type == other.type
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

    fun correspondingChord(idx: Int) : Int? {
        return collisions[idx]
    }
}