import java.lang.Math.*
import java.util.Random

class ParticleChords private constructor(val length: Int) {
    val chords: DoubleArray = DoubleArray(length, {_ -> 0.0})
    val velocity: DoubleArray = DoubleArray(length, {_ -> 0.0})
    var fitness: Double = 0.0
        private set
    var personalBest: IntArray = IntArray(length, {_ -> 0})
        private set

    init { recalculateFitness() }

    constructor(r: Random, rhythm: Rhythm) : this(rhythm.chordDurations.size) {
        for(i in 0 until length) {
            chords[i] = r.nextDouble() * 3 - 0.5
            velocity[i] = (r.nextDouble() - 0.5) / 2
            personalBest[i] = round(chords[i]).toInt()
        }
        recalculateFitness()
    }

    private constructor(chords: DoubleArray, velocity: DoubleArray) : this(chords.size) {
        assert(velocity.size == chords.size)
        recalculateFitness()
        personalBest = chords.map(::round).map(Long::toInt).toIntArray()
    }

    fun move() {
        val prev = fitness
        for(i in 0 until length) chords[i] += velocity[i]
        recalculateFitness()
        if(fitness < prev) personalBest = chords.map(::round).map(Long::toInt).toIntArray()
    }

    fun toSequence() : List<Chord> {
        val list = personalBest.clone().toMutableList()
        for (i in 1 until list.size) {
            list[i] += list[i - 1]
        }
        return list.map { d ->
            when(floorMod(d, 3)) {
                0 -> 0
                1 -> 3
                else -> 4
            } + 7 * floorDiv(d, 3)
        }.map { root -> Chord(root, ChordType.MajorTriad) }.toList()
    }

    private fun recalculateFitness() {
        val chords = toSequence()
        var total = 0
        run {
            var cur = chords[0]
            var len = 0
            for (i in 1 until length) {
                if (chords[i] == cur) {
                    len++
                } else {
                    total += len * len
                    len = 0
                    cur = chords[i]
                }
            }
            total += len * len
        }

        run {
            val counts = HashMap<Chord, Int>()
            chords.forEach({i ->
                counts.put(i, counts[i]?.plus(1) ?: 0)
            })

            for(i in counts) {
                total += i.value * i.value
            }
        }

        run {
            var cur = chords[0].root
            for (i in 1 until length) {
                total += abs(abs(chords[i].root - cur) - 1)
            }
        }

        fitness = total.toDouble()
    }

    fun clone() : ParticleChords {
        return ParticleChords(chords.clone(), velocity.clone())
    }
}

class ParticleNotes private constructor(val rhythm: Rhythm, val chords: List<Chord>) {
    val length = rhythm.noteDurations.size
    val notes: DoubleArray = DoubleArray(length, {_ -> 0.0})
    val velocity: DoubleArray = DoubleArray(length, {_ -> 0.0})
    var fitness: Double = 0.0
        private set
    var personalBest: IntArray = kotlin.IntArray(length, {_ -> 0})
        private set

    init { recalculateFitness() }

    constructor(r: Random, rhythm: Rhythm, chords: List<Chord>) : this(rhythm, chords) {
        for(i in 0 until length) {
            val chord = rhythm.correspondingChord(i)
            if(chord == null)
                notes[i] = r.nextDouble() * 7 - 0.5
            else
                notes[i] = r.nextDouble() * 3 - 0.5
            velocity[i] = (r.nextDouble() - 0.5) / 4.0
            personalBest[i] = round(notes[i]).toInt()
        }
        recalculateFitness()
    }

    private constructor(rhythm: Rhythm, chords: List<Chord>, notes: DoubleArray, velocity: DoubleArray) : this(rhythm, chords) {
        assert(velocity.size == chords.size)
        recalculateFitness()
        personalBest = notes.map(::round).map(Long::toInt).toIntArray()
    }

    fun move() {
        val prev = fitness
        for(i in 0 until length) notes[i] += velocity[i]
        recalculateFitness()
        if(fitness < prev) personalBest = notes.map(::round).map(Long::toInt).toIntArray()
    }

    /**
     * Returns a sequence of steps
     */
    fun toSequence(scale: Scale) : List<Int> {
        return personalBest.mapIndexed { index, d ->
            val chord = rhythm.correspondingChord(index)
            if(chord != null) {
                scale.root + chords[chord][floorMod(d, 3)] + 12 * (floorDiv(d, 3) + 1)
            } else scale[floorMod(d, 7)]
        }
    }

    private fun chord(idx: Int) : Chord? {
        val chord = rhythm.correspondingChord(idx)
        return if(chord == null) null else chords[chord]
    }

    private fun recalculateFitness() {

    }

    fun clone() : ParticleNotes {
        return ParticleNotes(rhythm, chords, notes.clone(), velocity.clone())
    }
}
