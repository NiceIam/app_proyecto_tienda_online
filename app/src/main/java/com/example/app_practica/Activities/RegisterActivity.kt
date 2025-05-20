package com.example.app_practica.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practica.Activities.LoginActivity.Global
import com.example.app_practica.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register) // Usa el mismo layout si quieres

        val correo = findViewById<EditText>(R.id.et_email)
        val pass = findViewById<EditText>(R.id.et_password)
        val btn_register = findViewById<Button>(R.id.btn_register)
        val tv_login = findViewById<TextView>(R.id.tv_login)

        btn_register.setOnClickListener {
            val emailText = correo.text.toString()
            val passText = pass.text.toString()

            if (passText.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    creaCuentaFirebase(emailText, passText)
                } else {
                    Toast.makeText(this, "Correo incorrecto", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        tv_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun creaCuentaFirebase(correo: String, pass: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("correo", task.result?.user?.email)
                    intent.putExtra("pass", pass)
                    startActivity(intent)

                    fun guardar_sesion(correo:String){
                        val guardar_sesion = getSharedPreferences(Global.preferencias_compartidas, Context.MODE_PRIVATE).edit()
                        guardar_sesion.putString("Correo", correo)
                        guardar_sesion.apply()
                    }


                    Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Contraseña corta o correo ya registrado", Toast.LENGTH_SHORT).show()
                }
            }
    }
}