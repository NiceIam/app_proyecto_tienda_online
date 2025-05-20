package com.example.app_practica.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.app_practica.Activities.LoginActivity
import com.example.app_practica.Activities.LoginActivity.Global
import com.example.app_practica.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.nio.channels.spi.AsynchronousChannelProvider.provider
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        val tvChangePassword = view.findViewById<TextView>(R.id.cambiar_contrasena)

        tvChangePassword.setOnClickListener {
            mostrarDialogoCambiarContrasena()
        }




        // Inicializar Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btn_save = view.findViewById<Button>(R.id.btn_save)
        val btn_logout = view.findViewById<TextView>(R.id.tv_logout)
        val tvEmailUser = view.findViewById<TextView>(R.id.user_email)
        val etFirstName = view.findViewById<TextInputEditText>(R.id.et_first_name)
        val etLastName = view.findViewById<TextInputEditText>(R.id.et_last_name)
        val etPhone = view.findViewById<TextInputEditText>(R.id.et_phone)
        val etAddress = view.findViewById<TextInputEditText>(R.id.et_address)

        // Inicializar GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Obtener email del usuario actual
        val currentUser = auth.currentUser
        val email = currentUser?.email

        // Mostrar email en el TextView si existe
        if (email != null) {
            tvEmailUser.text = email
            // Cargar datos existentes si los hay
            loadUserProfile(email)
        } else {
            tvEmailUser.text = "No hay usuario logueado"
            btn_save.isEnabled = false
        }

        btn_save.setOnClickListener {
            if (email != null) {
                saveUserProfile(
                    email,
                    etFirstName.text.toString(),
                    etLastName.text.toString(),
                    etPhone.text.toString(),
                    etAddress.text.toString()
                )
            }
        }

        btn_logout.setOnClickListener {
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            auth.signOut()
            googleSignInClient.signOut()
            borrar_sesion()
        }

        return view
    }

    private fun saveUserProfile(email: String, firstName: String, lastName: String, phone: String, address: String) {
        Log.d("FirestoreDebug", "Intentando guardar para email: '$email'")
        Log.d("FirestoreDebug", "Datos a guardar: $firstName, $lastName, $phone, $address")

        val userData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "address" to address,
            "lastUpdate" to FieldValue.serverTimestamp()
        )

        firestore.collection("usuarios").document(email)
            .set(userData) // ← eliminado SetOptions.merge() temporalmente
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirestoreDebug", "Documento guardado exitosamente")
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("FirestoreDebug", "Error al guardar", task.exception)
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreDebug", "Fallo al guardar perfil", e)
            }
    }

    private fun loadUserProfile(email: String) {
        Log.d("FirestoreDebug", "Cargando perfil para: '$email'")

        firestore.collection("usuarios").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("FirestoreDebug", "Documento encontrado")
                    view?.findViewById<TextInputEditText>(R.id.et_first_name)?.setText(document.getString("firstName"))
                    view?.findViewById<TextInputEditText>(R.id.et_last_name)?.setText(document.getString("lastName"))
                    view?.findViewById<TextInputEditText>(R.id.et_phone)?.setText(document.getString("phone"))
                    view?.findViewById<TextInputEditText>(R.id.et_address)?.setText(document.getString("address"))
                } else {
                    Log.w("FirestoreDebug", "No se encontró documento para: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreDebug", "Error al cargar perfil", e)
                Toast.makeText(context, "Error al cargar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoCambiarContrasena() {
        val builder = AlertDialog.Builder(requireContext())

        val title = TextView(requireContext()).apply {
            text = "Cambiar contraseña"
            setPadding(32, 32, 32, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
        }

        val input = EditText(requireContext()).apply {
            hint = "Nueva contraseña"
            setHintTextColor(ContextCompat.getColor(context, R.color.hint_color))
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_blue))
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(50, 30, 50, 30)
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(title)
            addView(input)
        }

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevaContrasena = input.text.toString()

            if (nuevaContrasena.length < 6) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            } else {
                val user = auth.currentUser
                user?.updatePassword(nuevaContrasena)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        } else {
                            val error = task.exception?.message ?: "Error desconocido"
                            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        builder.setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.background_light)))
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_primary))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
    }




    private fun borrar_sesion() {
        val borrar_sesion: SharedPreferences.Editor = requireContext()
            .getSharedPreferences(Global.preferencias_compartidas, Context.MODE_PRIVATE).edit()
        borrar_sesion.clear()
        borrar_sesion.apply()
    }
}