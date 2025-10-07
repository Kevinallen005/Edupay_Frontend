package com.example.feepayment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.FileProvider
import com.example.feepayment.responses.ReceiptResponse
import com.example.feepayment.retrofit.retrofit
import com.example.feepayment.utils.MenuHandler
import com.itextpdf.kernel.pdf.*
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ReceiptActivity : AppCompatActivity() {

    private var receipt: ReceiptResponse? = null
    private var generatedPdfFile: File? = null

    // SAF launcher for saving PDF
    private val saveFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri: Uri? = result.data!!.data
            uri?.let {
                try {
                    contentResolver.openOutputStream(it)?.use { output ->
                        generatedPdfFile?.inputStream()?.copyTo(output)
                    }
                    Toast.makeText(this, "PDF saved successfully!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_receipt)

        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        MenuHandler.setupMenu(this, menuIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val studentId = intent.getIntExtra("studentid", -1)
        val feeName = intent.getStringExtra("feename") ?: ""

        if (studentId != -1 && feeName.isNotEmpty()) {
            fetchReceipt(studentId, feeName)
        } else {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show()
            finish()
        }

        val printBtn = findViewById<Button>(R.id.printbtn)
        printBtn.setOnClickListener {
            if (receipt != null) {
                generatePdf(receipt!!)
            } else {
                Toast.makeText(this, "Receipt not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchReceipt(studentId: Int, feeName: String) {
        retrofit.instance.getReceiptDetails(studentId, feeName)
            .enqueue(object : Callback<ReceiptResponse> {
                override fun onResponse(call: Call<ReceiptResponse>, response: Response<ReceiptResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        receipt = response.body()
                        val r = receipt!!
                        findViewById<TextView>(R.id.name).text = r.name
                        findViewById<TextView>(R.id.class_).text = r.class_
                        findViewById<TextView>(R.id.sect).text = r.sec
                        findViewById<TextView>(R.id.amountHis).text = "₹${r.feeamt}"
                        findViewById<TextView>(R.id.feename).text = r.feename
                        findViewById<TextView>(R.id.paydate).text = r.paydate
                        findViewById<TextView>(R.id.duedate).text = r.duedate
                        findViewById<TextView>(R.id.referenceID).text = r.referenceid
                        findViewById<TextView>(R.id.scholarship).text = r.ScholarshipAmount
                    } else {
                        Toast.makeText(this@ReceiptActivity, "No receipt found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ReceiptResponse>, t: Throwable) {
                    Toast.makeText(this@ReceiptActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /** Download letterhead from server */
    private fun downloadLetterhead(onComplete: (File?) -> Unit) {
        Thread {
            try {
                val url = URL(retrofit.BASE_URL + "uploads/letterhead.pdf")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val input: InputStream = connection.inputStream
                val file = File(cacheDir, "letterhead.pdf")
                val output = FileOutputStream(file)

                input.copyTo(output)
                input.close()
                output.close()
                connection.disconnect()

                runOnUiThread { onComplete(file) }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { onComplete(null) }
            }
        }.start()
    }

    /** Generate aligned PDF into cache, then preview & let user save */
    private fun generatePdf(receipt: ReceiptResponse) {
        downloadLetterhead { letterheadFile ->
            if (letterheadFile == null) {
                Toast.makeText(this, "Failed to download letterhead", Toast.LENGTH_SHORT).show()
                return@downloadLetterhead
            }

            try {
                val tempFile = File(cacheDir, "receipt_preview.pdf")
                val reader = PdfReader(letterheadFile.absolutePath)
                val writer = PdfWriter(FileOutputStream(tempFile))
                val pdfDoc = PdfDocument(reader, writer)
                val doc = Document(pdfDoc)

                // Add aligned table
                val table = Table(floatArrayOf(150f, 300f))
                table.setMarginTop(200f)

                table.addCell("Name"); table.addCell(receipt.name)
                table.addCell("Class"); table.addCell(receipt.class_)
                table.addCell("Section"); table.addCell(receipt.sec)
                table.addCell("Fee Type"); table.addCell(receipt.feename)
                table.addCell("Amount"); table.addCell("₹${receipt.feeamt}")
                table.addCell("Pay Date"); table.addCell(receipt.paydate)
                table.addCell("Due Date"); table.addCell(receipt.duedate)
                table.addCell("Scholarship"); table.addCell(receipt.ScholarshipAmount)
                table.addCell("Reference ID"); table.addCell(receipt.referenceid)

                doc.add(table)
                doc.close()
                pdfDoc.close()

                generatedPdfFile = tempFile

                // Show preview
                previewPdf(tempFile)

                // Ask user where to save
                askUserToSave()

            } catch (e: Exception) {
                Log.e("PDFError", "Error generating PDF", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun previewPdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(Intent.createChooser(intent, "Preview PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun askUserToSave() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "Receipt_${System.currentTimeMillis()}.pdf")
        }
        saveFileLauncher.launch(intent)
    }
}
