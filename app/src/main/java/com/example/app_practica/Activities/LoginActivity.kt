package com.example.app_practica.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.example.app_practica.MainActivity
import com.example.app_practica.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    object Global {
        var preferencias_compartidas = "sharedpreserences"
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        verficar_sesion_abierta()

        val correo = findViewById<EditText>(R.id.text_input_email)
        val pass = findViewById<EditText>(R.id.text_input_password)
        val btn_login = findViewById<Button>(R.id.btn_iniciarsesion)
        val txt_register = findViewById<TextView>(R.id.txt_register)
        val btn_google = findViewById<Button>(R.id.btn_google_signin)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de tener esto en strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btn_login.setOnClickListener {
            if (pass.text.toString().isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(correo.text.toString()).matches()) {
                    login_firebase(correo.text.toString(), pass.text.toString())
                } else {
                    Toast.makeText(applicationContext, "Correo incorrecto", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        txt_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_google.setOnClickListener {
            signInWithGoogle()
        }

        val txt_olvidaste = findViewById<TextView>(R.id.olvidaste_tu_contrasena)


        txt_olvidaste.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            val title = TextView(this).apply {
                text = "Recuperar contraseña"
                setPadding(32, 32, 32, 0)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                setTypeface(null, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            }

            val input = EditText(this).apply {
                hint = "Correo electrónico"
                setHintTextColor(ContextCompat.getColor(context, R.color.hint_color))
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_blue))
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                setPadding(50, 30, 50, 30)
            }

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 16, 32, 16)
                addView(title)
                addView(input)
            }

            builder.setView(layout)

            builder.setPositiveButton("Enviar") { _, _ ->
                val correo = input.text.toString().trim()
                if (correo.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancelar", null)

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.background_light)))
            dialog.show()

            // Colorear los botones
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.button_primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }


    }

    private fun login_firebase(correo: String, pass: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    guardar_sesion(task.result?.user?.email.toString())
                } else {
                    Toast.makeText(applicationContext, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun verficar_sesion_abierta() {
        val sesion_abierta: SharedPreferences = getSharedPreferences(Global.preferencias_compartidas, Context.MODE_PRIVATE)
        val correo = sesion_abierta.getString("Correo", null)
        if (correo != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
            finish()
        }
    }

    private fun guardar_sesion(correo: String) {
        val guardar_sesion: SharedPreferences.Editor = getSharedPreferences(Global.preferencias_compartidas, Context.MODE_PRIVATE).edit()
        guardar_sesion.putString("Correo", correo)
        guardar_sesion.apply()
        guardar_sesion.commit()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error en el inicio con Google: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    guardar_sesion(user?.email ?: "")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Fallo autenticación con Google: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}