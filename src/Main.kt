import org.jfugue.midi.MidiFileManager
import org.jfugue.pattern.Pattern
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val random = Random()
    val scale = Scale(67, ScaleType.Major)
    val structure = generateStructure(8, random)
    val chords = structure.map { generateChords(random, scale, it) }
    val melody = structure.zip(chords).map { (s, c) -> generateMelody(random, scale, s, c) }
    save(scale,
            (0 until structure.size).map { Triple(structure[it], chords[it], melody[it])},
            "musique.mid")
}

fun save(scale: Scale, source: List<Triple<Segment, List<Chord>, List<Int>>>, filename: String) {
    val chordVoices = List(4, { StringBuilder() })
    for (triple in source) {
        val rhythm = triple.first.rhythm
        var i = 0
        for (chord in triple.second) {
            chordVoices[0].append(scale[chord[0] - 7]).append("/").append(rhythm.chordDurations[i]).append(' ')
            chordVoices[1].append(scale[chord[1] - 7]).append("/").append(rhythm.chordDurations[i]).append(' ')
            chordVoices[2].append(scale[chord[2] - 7]).append("/").append(rhythm.chordDurations[i]).append(' ')
            i += 1
        }
        i = 0
        for (note in triple.third) {
            chordVoices[3].append(scale[note]).append("/").append(rhythm.noteDurations[i]).append(' ')
            i += 1
        }
    }
    chordVoices[0].append(scale[0 - 7]).append("/8")
    chordVoices[1].append(scale[2 - 7]).append("/8")
    chordVoices[2].append(scale[4 - 7]).append("/8")
    chordVoices[3].append(scale[7]).append("/8")
    val string = "V0 " + chordVoices[0].toString() +
            " V1 " + chordVoices[1].toString() +
            " V2 " + chordVoices[2].toString() +
            " V3 " + chordVoices[3].toString()

    val pattern = Pattern(string).setVoice(0).setInstrument("Piano").setTempo(1000)
    MidiFileManager.savePatternToMidi(pattern, File(filename))
}