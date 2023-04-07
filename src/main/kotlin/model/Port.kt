package model

data class Port(
    val name: String,
    val group: String,
    val direction: PortDirection,
    val type: PortType
)

enum class PortDirection {
    INPUT, OUTPUT;
}

enum class PortType {
    AUDIO, MIDI, CLIENT
}