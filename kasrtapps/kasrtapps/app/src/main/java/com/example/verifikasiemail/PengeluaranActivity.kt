package com.example.verifikasiemail

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PengeluaranActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextTanggal: EditText
    private lateinit var editTextNominal: EditText
    private lateinit var editTextKeterangan: EditText
    private lateinit var buttonSimpanPengeluaran: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengeluaran)

        database = FirebaseDatabase.getInstance().reference
        editTextTanggal = findViewById(R.id.editTextTanggal)
        editTextNominal = findViewById(R.id.editTextNominal)
        editTextKeterangan = findViewById(R.id.editTextKeterangan)
        buttonSimpanPengeluaran = findViewById(R.id.buttonSimpanPengeluaran)

        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(calendar)
        }

        editTextTanggal.setOnClickListener {
            DatePickerDialog(
                this@PengeluaranActivity,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        buttonSimpanPengeluaran.setOnClickListener {
            val tanggal = editTextTanggal.text.toString()
            val nominal = editTextNominal.text.toString().toInt()
            val keterangan = editTextKeterangan.text.toString()
            simpanPengeluaran(tanggal, nominal, keterangan)
        }
    }

    private fun updateDateInView(calendar: Calendar) {
        val myFormat = "dd-MM-yyyy" // Format yang diinginkan
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextTanggal.setText(sdf.format(calendar.time))
    }

    private fun simpanPengeluaran(tanggal: String, nominal: Int, keterangan: String) {
        val pengeluaranId = database.child("pengeluaran").push().key
        val pengeluaran = Pengeluaran(tanggal, nominal, keterangan)
        if (pengeluaranId != null) {
            database.child("pengeluaran").child(pengeluaranId).setValue(pengeluaran)
                .addOnCompleteListener {
                    // Kembali ke activity Laporan setelah menyimpan dan memperbarui data
                    finish()
                }
        }
    }

    data class Pengeluaran(val tanggal: String, val nominal: Int, val keterangan: String)
}
