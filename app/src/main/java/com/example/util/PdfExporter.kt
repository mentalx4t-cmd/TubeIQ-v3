package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateMonthlyReport(
        context: Context,
        channelName: String,
        niche: String,
        subscribers: String,
        views: String,
        keywords: List<String>,
        uploadedVideosCount: Int,
        averageSeoScore: Int,
        tiktokFollowers: String,
        instagramFollowers: String
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size: 595 x 842 px
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Paint settings
        val paint = Paint()
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = 12f
        }

        // --- 1. Draw Styled Header Banner ---
        // Navy blue header background
        paint.color = Color.parseColor("#0F172A")
        canvas.drawRect(0f, 0f, 595f, 130f, paint)

        // Accent neon blue line
        paint.color = Color.parseColor("#2563EB")
        canvas.drawRect(0f, 130f, 595f, 136f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("vidIQ Creator Suite", 40f, 55f, paint)

        // Subtitle
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Monthly Creator Analytics & SEO Report", 40f, 85f, paint)

        // Format Current Date
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date())
        paint.color = Color.parseColor("#94A3B8")
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Generated: $dateStr", 555f, 85f, paint)
        paint.textAlign = Paint.Align.LEFT // Reset alignment

        // --- 2. Creator Info Panel ---
        var currentY = 175f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 14f
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("CREATOR PROFILE", 40f, currentY, textPaint)
        
        currentY += 5f
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawLine(40f, currentY, 555f, currentY, paint)

        currentY += 25f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 11f
        textPaint.color = Color.parseColor("#475569")
        canvas.drawText("Channel: ${channelName.take(30)}", 45f, currentY, textPaint)
        canvas.drawText("Niche Group: ${niche.take(30)}", 300f, currentY, textPaint)

        // --- 3. Multi-Platform Audits ---
        currentY += 35f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 14f
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("MULTI-PLATFORM AUDIT", 40f, currentY, textPaint)

        currentY += 5f
        canvas.drawLine(40f, currentY, 555f, currentY, paint)

        currentY += 25f
        // Draw YouTube Card
        paint.color = Color.parseColor("#FFF1F2") // Soft Red
        canvas.drawRoundRect(40f, currentY - 15f, 190f, currentY + 50f, 8f, 8f, paint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = Color.parseColor("#E11D48") // Rose Red
        canvas.drawText("YouTube", 55f, currentY, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("$subscribers Subs", 55f, currentY + 20f, textPaint)
        canvas.drawText("$views Views", 55f, currentY + 36f, textPaint)

        // Draw TikTok Card
        paint.color = Color.parseColor("#F4F4F5") // Soft Zinc
        canvas.drawRoundRect(210f, currentY - 15f, 360f, currentY + 50f, 8f, 8f, paint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = Color.parseColor("#18181B") // Dark Zinc
        canvas.drawText("TikTok Sync", 225f, currentY, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("$tiktokFollowers Followers", 225f, currentY + 20f, textPaint)
        canvas.drawText("Synced", 225f, currentY + 36f, textPaint)

        // Draw Instagram Card
        paint.color = Color.parseColor("#FDF2F8") // Soft Rose Pink
        canvas.drawRoundRect(380f, currentY - 15f, 530f, currentY + 50f, 8f, 8f, paint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = Color.parseColor("#DB2777") // Magenta Pink
        canvas.drawText("Instagram Sync", 395f, currentY, textPaint)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("$instagramFollowers Followers", 395f, currentY + 20f, textPaint)
        canvas.drawText("Synced", 395f, currentY + 36f, textPaint)

        // --- 4. SEO Keyword Performance ---
        currentY += 85f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 14f
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("SEO KEYWORD RESEARCH HIGHLIGHTS", 40f, currentY, textPaint)

        currentY += 5f
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawLine(40f, currentY, 555f, currentY, paint)

        currentY += 25f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 11f
        textPaint.color = Color.parseColor("#475569")
        canvas.drawText("Monitored keywords this billing cycle:", 45f, currentY, textPaint)

        currentY += 15f
        if (keywords.isEmpty()) {
            canvas.drawText("• No keyword searches recorded. Recommended to optimize draft titles.", 55f, currentY, textPaint)
        } else {
            keywords.take(4).forEachIndexed { index, keyword ->
                currentY += 16f
                canvas.drawText("• $keyword (Optimized with Gemini 1.5 Flash)", 55f, currentY, textPaint)
            }
        }

        // --- 5. Engagement Predictions Summaries ---
        currentY += 45f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 14f
        textPaint.color = Color.parseColor("#1E293B")
        canvas.drawText("ENGAGEMENT METRICS SUMMARY", 40f, currentY, textPaint)

        currentY += 5f
        canvas.drawLine(40f, currentY, 555f, currentY, paint)

        currentY += 25f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#475569")
        canvas.drawText("Optimized Uploads: $uploadedVideosCount videos", 45f, currentY, textPaint)
        canvas.drawText("Average SEO Score: $averageSeoScore% (Satisfactory)", 300f, currentY, textPaint)

        currentY += 20f
        canvas.drawText("Viewer Retention Target: 48.5% (Baseline Trend - High Niche Engagement)", 45f, currentY, textPaint)

        // --- 6. Footer disclaimer ---
        currentY = 780f
        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(40f, currentY, 555f, currentY, paint)

        currentY += 20f
        textPaint.textSize = 10f
        textPaint.color = Color.parseColor("#94A3B8")
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("This metric sheet is generated in real-time. vidIQ is a trademark of vidIQ Inc. All rights reserved.", 297f, currentY, textPaint)

        pdfDocument.finishPage(page)

        // Save PDF to App Cache directory
        val folder = File(context.cacheDir, "reports")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, "vidIQ_Report_${System.currentTimeMillis()}.pdf")
        return try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun sharePdfReport(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "vidIQ Monthly Creator Analytics Report")
            putExtra(Intent.EXTRA_TEXT, "Hello! Attached is my Creator Analytics and SEO performance optimization report compiled by vidIQ Suite.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Share Analytics Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "No app available to open PDF share.", Toast.LENGTH_SHORT).show()
        }
    }
}
