package com.example.verifikasiemail

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Laporan : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var textViewTotalPemasukan: TextView
    private lateinit var buttonTambahPengeluaran: Button
    private lateinit var buttonGeneratePDF: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        database = FirebaseDatabase.getInstance().reference
        textViewTotalPemasukan = findViewById(R.id.textViewTotalPemasukan)
        buttonTambahPengeluaran = findViewById(R.id.buttonTambahPengeluaran)
        buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF)

        loadTotalPemasukan()

        buttonTambahPengeluaran.setOnClickListener {
            val intent = Intent(this, PengeluaranActivity::class.java)
            startActivity(intent)
        }

        buttonGeneratePDF.setOnClickListener {
            generatePDF()
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload the data when returning from PengeluaranActivity
        loadTotalPemasukan()
    }

    private fun loadTotalPemasukan() {
        database.child("payments").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalPemasukan = 0
                for (yearSnapshot in snapshot.children) {
                    for (monthSnapshot in yearSnapshot.children) {
                        val amount = monthSnapshot.getValue(Int::class.java) ?: 0
                        totalPemasukan += amount
                    }
                }
                hitungTotal(totalPemasukan)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun hitungTotal(totalPemasukan: Int) {
        database.child("pengeluaran").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalPengeluaran = 0
                for (expenseSnapshot in snapshot.children) {
                    val nominal = expenseSnapshot.child("nominal").getValue(Int::class.java) ?: 0
                    totalPengeluaran += nominal
                }
                val totalSaldo = totalPemasukan - totalPengeluaran
                textViewTotalPemasukan.text = "Total: $totalSaldo"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun generatePDF() {
        val totalData = TotalData()

        database.child("payments").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (yearSnapshot in snapshot.children) {
                    for (monthSnapshot in yearSnapshot.children) {
                        val amount = monthSnapshot.getValue(Int::class.java) ?: 0
                        val month = monthSnapshot.key ?: ""
                        totalData.totalPemasukan += amount
                        totalData.payments.add(Payment(month, amount))
                    }
                }

                database.child("pengeluaran").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (expenseSnapshot in snapshot.children) {
                            val nominal = expenseSnapshot.child("nominal").getValue(Int::class.java) ?: 0
                            val tanggal = expenseSnapshot.child("tanggal").getValue(String::class.java) ?: ""
                            val keterangan = expenseSnapshot.child("keterangan").getValue(String::class.java) ?: ""
                            totalData.totalPengeluaran += nominal
                            totalData.expenses.add(Expense(tanggal, keterangan, nominal))
                        }
                        createPDFDocument(totalData)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle any errors
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun createPDFDocument(totalData: TotalData) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 12f

        var yPosition = 40

        canvas.drawText("Laporan Keuangan", 30f, yPosition.toFloat(), paint)
        yPosition += 20

        canvas.drawText("Bulan", 30f, yPosition.toFloat(), paint)
        canvas.drawText("Keterangan", 150f, yPosition.toFloat(), paint)
        canvas.drawText("Pemasukan", 300f, yPosition.toFloat(), paint)
        canvas.drawText("Pengeluaran", 450f, yPosition.toFloat(), paint)
        yPosition += 20

        totalData.payments.forEach { payment ->
            canvas.drawText(payment.month, 30f, yPosition.toFloat(), paint)
            canvas.drawText("Pemasukan", 150f, yPosition.toFloat(), paint)
            canvas.drawText(payment.amount.toString(), 300f, yPosition.toFloat(), paint)
            yPosition += 20
        }

        totalData.expenses.forEach { expense ->
            canvas.drawText(expense.date, 30f, yPosition.toFloat(), paint)
            canvas.drawText(expense.description, 150f, yPosition.toFloat(), paint)
            canvas.drawText(expense.amount.toString(), 450f, yPosition.toFloat(), paint)
            yPosition += 20
        }

        val totalSaldo = totalData.totalPemasukan - totalData.totalPengeluaran
        yPosition += 40
        paint.textSize = 14f
        canvas.drawText("Total Pemasukan: ${totalData.totalPemasukan}", 30f, yPosition.toFloat(), paint)
        yPosition += 20
        canvas.drawText("Total Pengeluaran: ${totalData.totalPengeluaran}", 30f, yPosition.toFloat(), paint)
        yPosition += 20
        canvas.drawText("Saldo Akhir: $totalSaldo", 30f, yPosition.toFloat(), paint)

        document.finishPage(page)
        savePDF(document)
    }

    private fun savePDF(document: PdfDocument) {
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val file = File(directoryPath, "LaporanKeuangan.pdf")
        try {
            document.writeTo(FileOutputStream(file))
            document.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    data class TotalData(
        var totalPemasukan: Int = 0,
        var totalPengeluaran: Int = 0,
        val payments: MutableList<Payment> = mutableListOf(),
        val expenses: MutableList<Expense> = mutableListOf()
    )

    data class Payment(val month: String, val amount: Int)
    data class Expense(val date: String, val description: String, val amount: Int)
}
