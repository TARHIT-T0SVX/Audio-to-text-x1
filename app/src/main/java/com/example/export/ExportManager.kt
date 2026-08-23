package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.TranscriptEntity
import java.io.File
import java.io.FileOutputStream

object ExportManager {

    enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
        MARKDOWN("md", "text/markdown", "Markdown (.md)"),
        PLAIN_TEXT("txt", "text/plain", "Plain Text (.txt)"),
        PDF("pdf", "application/pdf", "PDF Document (.pdf)")
    }

    fun exportTranscript(context: Context, transcript: TranscriptEntity, format: ExportFormat): File? {
        val safeTitle = transcript.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "${safeTitle}_${System.currentTimeMillis()}.${format.extension}")

        return try {
            when (format) {
                ExportFormat.MARKDOWN -> {
                    val mdContent = buildString {
                        append("# ${transcript.title}\n\n")
                        append("**Date:** ${transcript.dateFormatted}  \n")
                        append("**Duration:** ${transcript.durationText}  \n")
                        append("**Language:** ${transcript.language}  \n")
                        append("**Word Count:** ${transcript.wordCount} words | ${transcript.characterCount} characters\n\n")
                        append("---\n\n")
                        append(transcript.content)
                    }
                    file.writeText(mdContent, Charsets.UTF_8)
                }
                ExportFormat.PLAIN_TEXT -> {
                    val txtContent = buildString {
                        append("${transcript.title}\n")
                        append("Date: ${transcript.dateFormatted}\n")
                        append("Duration: ${transcript.durationText}\n")
                        append("Language: ${transcript.language}\n")
                        append("Word Count: ${transcript.wordCount} words\n")
                        append("----------------------------------------\n\n")
                        append(transcript.content)
                    }
                    file.writeText(txtContent, Charsets.UTF_8)
                }
                ExportFormat.PDF -> {
                    generatePdf(file, transcript)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generatePdf(file: File, transcript: TranscriptEntity) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.BLACK
            textSize = 13f
            isAntiAlias = true
        }

        var y = 60f
        canvas.drawText(transcript.title, 40f, y, titlePaint)
        y += 25f

        canvas.drawText("Date: ${transcript.dateFormatted}  |  Duration: ${transcript.durationText}  |  Language: ${transcript.language}", 40f, y, metaPaint)
        y += 20f
        canvas.drawText("${transcript.wordCount} words | ${transcript.characterCount} characters", 40f, y, metaPaint)
        y += 25f

        // Draw horizontal line
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 25f

        // Draw body wrapped lines
        val lines = wrapText(transcript.content, bodyPaint, 515f)
        for (line in lines) {
            if (y > 780f) { // simple single-page bound or break
                break
            }
            canvas.drawText(line, 40f, y, bodyPaint)
            y += 18f
        }

        pdfDocument.finishPage(page)
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val result = mutableListOf<String>()
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
            if (paragraph.isBlank()) {
                result.add("")
                continue
            }
            val words = paragraph.split(" ")
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) result.add(currentLine)
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) {
                result.add(currentLine)
            }
        }
        return result
    }

    fun shareFile(context: Context, file: File, format: ExportFormat) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Transcript"))
    }
}
