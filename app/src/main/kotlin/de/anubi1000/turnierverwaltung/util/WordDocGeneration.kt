package de.anubi1000.turnierverwaltung.util

import de.anubi1000.turnierverwaltung.data.ScoreboardData
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.TableWidthType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFStyle
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType
import java.math.BigInteger
import kotlin.math.roundToInt

private const val TABLE_WIDTH = 15388
private const val SCALING_FACTOR = 7.0f

private const val TABLE_HEADING_STYLE = "tournamentTableHeading"
private const val TABLE_CONTENT_STYLE = "tournamentTableContent"

private const val MAIN_COLOR = "1b5e20"

fun ScoreboardData.toWordDocument(tableIndex: Int): XWPFDocument {
    val table = tables[tableIndex]

    return xwpfDocument {
        setPageSize()

        createStyles()
        addTableHeadingStyle()
        addTableContentStyle()

        addHeadings(
            tournamentName = name,
            table = table,
        )

        table {
            removeRow(0)
            width = TABLE_WIDTH
            widthType = TableWidthType.DXA

            val columnWidths = getColumnWidths(table.columns)
            createHeaderRow(columnWidths, table.columns)
            createContentRows(table, columnWidths)
        }
    }
}

private fun XWPFDocument.addHeadings(
    tournamentName: String,
    table: ScoreboardData.Table,
) {
    createParagraph().apply {
        alignment = ParagraphAlignment.CENTER

        createRun().apply {
            setText(tournamentName)
            fontSize = 26
            color = MAIN_COLOR

            ctr.rPr.apply {
                addNewRFonts().ascii = "Aptos"
                addNewB()
            }

            addBreak()
        }

        createRun().apply {
            setText("VfS Maulbronn - Diefenbach e.V.")
            fontSize = 20
            color = MAIN_COLOR

            ctr.rPr.apply {
                addNewRFonts().ascii = "Aptos"
                addNewB()
            }
        }
    }

    xwpfDocument.createParagraph().apply {
        alignment = ParagraphAlignment.CENTER

        createRun().apply {
            setText(table.name)
            fontSize = 16
            color = MAIN_COLOR

            ctr.rPr.apply {
                addNewRFonts().ascii = "Aptos"
                addNewB()
            }
        }
    }
}

private fun XWPFDocument.setPageSize() {
    val section = document.body.addNewSectPr()

    section.addNewPgSz().apply {
        orient = STPageOrientation.LANDSCAPE
        h = BigInteger.valueOf(595 * 20)
        w = BigInteger.valueOf(842 * 20)
    }

    section.addNewPgMar().apply {
        top = 720
        left = 720
        right = 720
        bottom = 720
    }
}

private fun XWPFDocument.addTableHeadingStyle() {
    val style = CTStyle.Factory.newInstance()
    style.styleId = TABLE_HEADING_STYLE
    style.type = STStyleType.PARAGRAPH

    style.rPr = CTRPr.Factory.newInstance().apply {
        addNewRFonts().ascii = "Aptos"
        addNewSz().setVal(BigInteger.valueOf(12 * 2))
        addNewB()
        addNewColor().setVal("FFFFFF")
    }

    style.pPr = CTPPrGeneral.Factory.newInstance().apply {
        addNewSpacing().apply {
            after = 0
        }
    }

    styles.addStyle(XWPFStyle(style))
}

private fun XWPFDocument.addTableContentStyle() {
    val style = CTStyle.Factory.newInstance()
    style.styleId = TABLE_CONTENT_STYLE
    style.type = STStyleType.PARAGRAPH

    style.rPr = CTRPr.Factory.newInstance().apply {
        addNewRFonts().ascii = "Aptos"
        addNewSz().setVal(BigInteger.valueOf(12 * 2))
    }

    style.pPr = CTPPrGeneral.Factory.newInstance().apply {
        addNewSpacing().apply {
            after = 0
        }
    }

    styles.addStyle(XWPFStyle(style))
}

private fun getColumnWidths(columns: List<ScoreboardData.Table.Column>): List<Int> {
    var totalWeight = 0f
    var totalFixedWidth = 0
    columns.forEach { column ->
        when (column.width) {
            is ScoreboardData.Table.Column.Width.Variable -> {
                totalWeight += column.width.weight
            }

            is ScoreboardData.Table.Column.Width.Fixed -> {
                totalFixedWidth += column.width.width
            }
        }
    }

    val remainingWidth = TABLE_WIDTH - (totalFixedWidth * SCALING_FACTOR)

    return columns.map { column ->
        when (val width = column.width) {
            is ScoreboardData.Table.Column.Width.Fixed -> (width.width * SCALING_FACTOR).roundToInt()
            is ScoreboardData.Table.Column.Width.Variable -> ((width.weight / totalWeight) * remainingWidth).roundToInt()
        }
    }
}

private fun XWPFTable.createHeaderRow(columnWidths: List<Int>, columns: List<ScoreboardData.Table.Column>) {
    val headerRow = createRow()
    columns.forEachIndexed { index, column ->
        val cell = headerRow.createCell().apply {
            setWidth(columnWidths[index].toString())
            verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
            color = MAIN_COLOR

            setCellMargins(
                80,
                60,
                60,
                60,
            )

            val borders = ctTc.tcPr.addNewTcBorders()

            borders.addNewBottom().`val` = STBorder.NIL
            borders.addNewTop().`val` = STBorder.SINGLE

            borders.addNewLeft().`val` = if (index == 0) STBorder.SINGLE else STBorder.NIL
            borders.addNewRight().`val` = if (index == columnWidths.size - 1) STBorder.SINGLE else STBorder.NIL
        }

        val paragraph = cell.paragraphs[0].apply {
            alignment = column.alignment.toParagraphAlignment()
            style = TABLE_HEADING_STYLE
        }

        paragraph.createRun().apply {
            setText(column.name)
        }
    }
}

private fun XWPFTable.createContentRows(
    table: ScoreboardData.Table,
    columnWidths: List<Int>,
) {
    table.rows.forEachIndexed { rowIndex, row ->
        val wordRow = createRow()
        row.values.forEachIndexed { index, value ->
            val column = table.columns[index]
            val cell = wordRow.getCell(index).apply {
                setWidth(columnWidths[index].toString())
                verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
                if (rowIndex % 2 == 1) color = "b9dabb"

                setCellMargins(
                    60,
                    60,
                    40,
                    60,
                )

                val borders = ctTc.tcPr.addNewTcBorders()

                borders.addNewTop().`val` = STBorder.NIL

                borders.addNewBottom().`val` = if (rowIndex == table.rows.size - 1) STBorder.SINGLE else STBorder.NIL
                borders.addNewLeft().`val` = if (index == 0) STBorder.SINGLE else STBorder.NIL
                borders.addNewRight().`val` = if (index == columnWidths.size - 1) STBorder.SINGLE else STBorder.NIL
            }

            val paragraph = cell.paragraphs[0].apply {
                alignment = column.alignment.toParagraphAlignment()
                style = TABLE_CONTENT_STYLE
            }

            paragraph.createRun().apply {
                setText(value)
            }
        }
    }
}
