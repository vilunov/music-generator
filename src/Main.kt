import org.jfugue.midi.MidiFileManager
import org.jfugue.pattern.Pattern
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val time = System.currentTimeMillis()
    val random = Random()
    val scale = Scale(Settings.scale_root, Settings.scale_type)
    val structure = generateStructure(Settings.segments, random)
    val chords = structure.map { generateChords(random, scale, it) }
    val melody = structure.zip(chords).map { (s, c) -> generateMelody(random, scale, s, c) }
    save(scale,
            (0 until structure.size).map { Triple(structure[it], chords[it], melody[it]) })
    System.out.format("Taken time: %d ns\n", System.currentTimeMillis() - time)
}

fun save(scale: Scale, source: List<Triple<Segment, List<Chord>, List<Int>>>) {
    val chordVoices = List(4, { StringBuilder() })
    for (triple in source) {
        val rhythm = triple.first.rhythm
        var i = 0
        for (chord in triple.second) {
            chordVoices[0].append(scale[chord[0] - 7]).append("/").append(rhythm.chordDurations[i].toDouble() / 8).append(' ')
            chordVoices[1].append(scale[chord[1] - 7]).append("/").append(rhythm.chordDurations[i].toDouble() / 8).append(' ')
            chordVoices[2].append(scale[chord[2] - 7]).append("/").append(rhythm.chordDurations[i].toDouble() / 8).append(' ')
            i += 1
        }
        i = 0
        for (note in triple.third) {
            chordVoices[3].append(scale[note]).append("/").append(rhythm.noteDurations[i].toDouble() / 8).append(' ')
            i += 1
        }
    }
    chordVoices[0].append(scale[0 - 7]).append("/1")
    chordVoices[1].append(scale[2 - 7]).append("/1")
    chordVoices[2].append(scale[4 - 7]).append("/1")
    chordVoices[3].append(scale[7]).append("/1")
    val string = "V0 " + chordVoices[0].toString() +
            " V1 " + chordVoices[1].toString() +
            " V2 " + chordVoices[2].toString() +
            " V3 " + chordVoices[3].toString()

    val pattern = Pattern(string).setVoice(0).setInstrument("Piano").setTempo(120)
    MidiFileManager.savePatternToMidi(pattern, File(Settings.output_file))
}