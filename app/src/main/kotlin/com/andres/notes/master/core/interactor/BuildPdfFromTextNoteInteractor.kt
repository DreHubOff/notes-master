package com.andres.notes.master.core.interactor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.print.PrintAttributes
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.data.FileManagerRepository
import com.andres.notes.master.data.TextNotesRepository
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import java.io.File
import javax.inject.Inject

class BuildPdfFromTextNoteInteractor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val textNotesRepository: TextNotesRepository,
    private val fileManagerRepository: FileManagerRepository,
) {

    suspend operator fun invoke(noteId: Long): File? {
        val textNote = textNotesRepository.getNoteById(noteId) ?: return null
        val pdfFile = fileManagerRepository.createSharableFile(fileName = buildPdfName(textNote))
        val document = SimplyPdf
            .with(context, pdfFile)
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A4)
            .margin(Margin.default)
            .firstPageBackgroundColor(Color.WHITE)
            .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
            .build()

        val titleStyle = TextProperties().apply {
            textSize = 16
            textColor = "#000000"
            typeface = Typeface.DEFAULT_BOLD
        }
        document.text.write(
            text = textNote.title,
            properties = titleStyle,
        )

        val contentStyle = TextProperties().apply {
            textSize = 12
            textColor = "#000000"
            typeface = Typeface.DEFAULT
        }
        document.text.write(
            text = textNote.content,
            properties = contentStyle,
        )

        document.finish()
        return pdfFile
    }

    private fun buildPdfName(textNote: TextNote): String {
        val formatter = DateTimeComponents.Format {
            year()
            char('-')
            monthNumber(Padding.ZERO)
            char('-')
            day(Padding.ZERO)
            char('_')
            hour(Padding.ZERO)
            char('-')
            minute(Padding.ZERO)
            char('-')
            second(Padding.ZERO)
        }
        return "Note_${textNote.creationDate.format(formatter)}.pdf"
    }
}