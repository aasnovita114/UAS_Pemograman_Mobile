package com.example.verifikasiemail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var buttonBayarIuran: Button
    private lateinit var buttonLaporan: Button
    private lateinit var buttonFormwarga : Button
    private lateinit var buttonDatawarga : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonBayarIuran = findViewById(R.id.buttonBayarIuran)
        buttonLaporan  = findViewById(R.id.btnLaporan)
        buttonFormwarga = findViewById(R.id.btnFormwarga)
        buttonDatawarga =  findViewById(R.id.btnDatawarga)

        buttonBayarIuran.setOnClickListener {
            val intent = Intent(this, Pemasukan::class.java)
            startActivity(intent)
        }



        buttonLaporan.setOnClickListener {
            val intent = Intent(this, Laporan::class.java)
            startActivity(intent)
        }

        buttonFormwarga.setOnClickListener{
            val intent = Intent(this, FormWarga::class.java)
            startActivity(intent)
        }

        buttonDatawarga.setOnClickListener{
            val intent = Intent(this, Datawarga::class.java)
            startActivity(intent)
        }


    }
}
