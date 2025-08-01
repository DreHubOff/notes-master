package com.andres.notes.master.core.interactor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.print.PrintAttributes
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.ChecklistItem
import com.andres.notes.master.data.ChecklistRepository
import com.andres.notes.master.data.FileManagerRepository
import com.andres.notes.master.di.qualifier.BulletPointSymbol
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

class BuildPdfFromChecklistInteractor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:BulletPointSymbol private val bulletPointSymbol: String,
    private val checklistRepository: ChecklistRepository,
    private val fileManagerRepository: FileManagerRepository,
) {

    suspend operator fun invoke(checklistId: Long): File? {
        val checklist = checklistRepository.getChecklistById(checklistId) ?: return null
        val pdfFile = fileManagerRepository.createSharableFile(buildPdfName(checklist))

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
            text = checklist.title,
            properties = titleStyle,
        )

        val uncheckedItemStyle = TextProperties().apply {
            this.bulletSymbol = this@BuildPdfFromChecklistInteractor.bulletPointSymbol
            textSize = 12
            textColor = "#000000"
            typeface = Typeface.DEFAULT
        }
        val checkedItems = mutableListOf<ChecklistItem>()
        checklist
            .items
            .sortedBy { it.listPosition }
            .forEach { item ->
                if (item.isChecked) {
                    checkedItems += item
                    return@forEach
                }
                document.text.write(text = item.title, properties = uncheckedItemStyle)
            }

        val checkedItemStyle = TextProperties().apply {
            this.bulletSymbol = this@BuildPdfFromChecklistInteractor.bulletPointSymbol
            strikethrough = true
            textSize = 12
            textColor = "#000000"
            typeface = Typeface.DEFAULT
        }
        checkedItems.forEach { item ->
            document.text.write(text = item.title, properties = checkedItemStyle)
        }

        document.finish()
        return pdfFile
    }

    private fun buildPdfName(checklist: Checklist): String {
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
        return "Checklist_${checklist.creationDate.format(formatter)}.pdf"
    }
}