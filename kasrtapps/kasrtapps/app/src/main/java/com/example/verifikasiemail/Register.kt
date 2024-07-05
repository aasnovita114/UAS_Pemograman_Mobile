package com.example.verifikasiemail

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnKembali: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Bind views using synthetic imports
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnKembali = findViewById(R.id.btnKembali)

        auth = FirebaseAuth.getInstance()

        btnKembali.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (isEmpty(email) or isEmpty(password) or isEmpty(confirmPassword)) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password minimal 6 karakter!"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword.error = "Konfirmasi password tidak sama!"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful) {
                            auth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener(this@Register, object : OnCompleteListener<Void> {
                                    override fun onComplete(task: Task<Void>) {
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@Register,
                                                "Pendaftaran berhasil. Cek email untuk verifikasi!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(Intent(this@Register, Login::class.java))
                                        } else {
                                            Toast.makeText(
                                                this@Register,
                                                "Gagal mengirim verifikasi email!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                })
                        } else {
                            Toast.makeText(this@Register, "Pendaftaran gagal!", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    private fun isEmpty(text: String): Boolean {
        return TextUtils.isEmpty(text)
    }
}
