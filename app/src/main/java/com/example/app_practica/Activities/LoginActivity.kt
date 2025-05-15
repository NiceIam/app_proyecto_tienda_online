package com.example.app_practica.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practica.MainActivity
import com.example.app_practica.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var btnInicioSesion: Button
    private lateinit var btnGoogleSignIn: MaterialButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id)) // Agrega esto desde tu google-services.json
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Botón normal
        btnInicioSesion = findViewById(R.id.btn_iniciarsesion)
        btnInicioSesion.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Botón Google
        btnGoogleSignIn = findViewById(R.id.btn_google_signin)
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
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
                val account = task.getResult(ApiException::class.java)
                // Autenticación exitosa
                handleGoogleSignInResult(account)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Error en inicio de sesión: ${e.statusCode}")
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        Toast.makeText(this, "Bienvenido: ${account?.displayName}", Toast.LENGTH_SHORT).show()

        // Redirigir al MainActivity después del login exitoso
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya está autenticado
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}