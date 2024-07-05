package com.example.verifikasiemail

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DateFormatSymbols

class Pemasukan : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextAmount: EditText
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var buttonPay: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pemasukan)

        database = FirebaseDatabase.getInstance().reference
        editTextAmount = findViewById(R.id.editTextAmount)
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        buttonPay = findViewById(R.id.buttonPay)
        progressBar = findViewById(R.id.progressBar)

        // Set default value for editTextAmount
        editTextAmount.setText("50000")

        val months = DateFormatSymbols().months.filter { it.isNotEmpty() }
        val monthAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            months
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        val yearList = (2010..2040).map { it.toString() }
        val yearAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            yearList
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = parent?.getItemAtPosition(position).toString()
                loadPaidMonths(selectedYear)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle when nothing is selected
            }
        }

        buttonPay.setOnClickListener {
            val amount = editTextAmount.text.toString().toIntOrNull() ?: 0
            val month = spinnerMonth.selectedItem.toString()
            val year = spinnerYear.selectedItem.toString()
            if (amount > 0) {
                pay(month, year, amount)
            } else {
                Toast.makeText(this, "Masukkan jumlah pembayaran yang valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPaidMonths(selectedYear: String) {
        val paidMonths = mutableListOf<String>()

        val paymentsRef = database.child("payments").child(selectedYear)
        paymentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (monthSnapshot in snapshot.children) {
                    paidMonths.add(monthSnapshot.key ?: "")
                }
                // Filter out paid months from the spinner
                val months = DateFormatSymbols().months.filter { it.isNotEmpty() && !paidMonths.contains(it) }
                val monthAdapter = ArrayAdapter(
                    this@Pemasukan,
                    android.R.layout.simple_spinner_item,
                    months
                )
                monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMonth.adapter = monthAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Pemasukan, "Failed to load paid months: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun pay(month: String, year: String, amount: Int) {
        progressBar.visibility = ProgressBar.VISIBLE // Show ProgressBar

        val paymentRef = database.child("payments").child("$year").child(month)
        paymentRef.setValue(amount)
            .addOnSuccessListener {
                Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show()
                editTextAmount.setText("50000") // Reset editTextAmount after successful payment
                progressBar.visibility = ProgressBar.GONE // Hide ProgressBar after successful payment
            }
            .addOnFailureListener {
                Toast.makeText(this, "Pembayaran gagal", Toast.LENGTH_SHORT).show()
                progressBar.visibility = ProgressBar.GONE // Hide ProgressBar if payment fails
            }
    }
}
