package de.anubi1000.turnierverwaltung.server.messages

data class UpdateRowMessage(
    val tableId: String,
    val rowId: String,
    val values: List<String>,
    val sortValues: List<Int>,
) : Message {
    override val type: Message.Type = Message.Type.UPDATE_ROW
}
