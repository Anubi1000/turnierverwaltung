package de.anubi1000.turnierverwaltung.util

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable

fun xwpfDocument(action: XWPFDocument.() -> Unit): XWPFDocument {
    val document = XWPFDocument()
    action(document)
    return document
}

fun XWPFDocument.table(action: XWPFTable.() -> Unit) {
    val table = createTable()
    action(table)
}
