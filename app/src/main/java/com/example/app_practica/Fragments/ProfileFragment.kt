package com.example.app_practica.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.nio.channels.spi.AsynchronousChannelProvider.provider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    // Variables para manejo de imágenes
    private lateinit var profileImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private var currentPhotoPath: String? = null
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inicializar Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        // Inicializar vistas
        profileImageView = view.findViewById(R.id.profile_image)
        val tvChangePassword = view.findViewById<TextView>(R.id.cambiar_contrasena)
        val btn_save = view.findViewById<Button>(R.id.btn_save)
        val btn_logout = view.findViewById<TextView>(R.id.tv_logout)
        val tvEmailUser = view.findViewById<TextView>(R.id.user_email)
        val etFirstName = view.findViewById<TextInputEditText>(R.id.et_first_name)
        val etLastName = view.findViewById<TextInputEditText>(R.id.et_last_name)
        val etPhone = view.findViewById<TextInputEditText>(R.id.et_phone)
        val etAddress = view.findViewById<TextInputEditText>(R.id.et_address)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Cargar datos del usuario
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email != null) {
            tvEmailUser.text = email
            loadUserProfile(email)
            loadProfileImage(currentUser)
        } else {
            tvEmailUser.text = "No hay usuario logueado"
            btn_save.isEnabled = false
        }

        // Configurar listeners
        profileImageView.setOnClickListener {
            showImagePickerOptions()
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
            signOut()
        }

        tvChangePassword.setOnClickListener {
            mostrarDialogoCambiarContrasena()
        }

        return view
    }

    private fun showImagePickerOptions() {
        val options = arrayOf<CharSequence>("Tomar foto", "Elegir de galería", "Cancelar")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cambiar foto de perfil")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Tomar foto" -> checkCameraPermission()
                options[item] == "Elegir de galería" -> openImageChooser()
                options[item] == "Cancelar" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Verifica que hay una app de cámara disponible
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            // Crea el archivo donde irá la foto
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(context, "Error al crear el archivo", Toast.LENGTH_SHORT).show()
                null
            }

            // Continúa solo si el archivo fue creado exitosamente
            photoFile?.also { file ->
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } ?: run {
            Toast.makeText(context, "No se encontró aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Crea un nombre único para la imagen
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Guarda la ruta del archivo para usarla después
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        imageUri = uri
                        Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(profileImageView)
                        uploadImageToFirebase(uri)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    currentPhotoPath?.let { path ->
                        val file = File(path)
                        imageUri = Uri.fromFile(file)
                        Glide.with(this)
                            .load(file)
                            .circleCrop()
                            .into(profileImageView)
                        uploadImageToFirebase(Uri.fromFile(file))

                        // Opcional: agregar a la galería
                        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        mediaScanIntent.data = Uri.fromFile(file)
                        requireActivity().sendBroadcast(mediaScanIntent)
                    }
                }
            }
        }
    }


    private val PERMISSION_REQUEST_CAMERA = 100

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si los permisos no están concedidos, solicitarlos
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CAMERA
            )
        } else {
            // Si los permisos ya están concedidos, lanzar la cámara
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun uploadImageToFirebase(imageUri: Uri) {
        val currentUser = auth.currentUser
        val email = currentUser?.email ?: return

        val storageRef = storage.reference
        val profileImagesRef = storageRef.child("profile_images/${email}.jpg")

        val uploadTask = profileImagesRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            profileImagesRef.downloadUrl.addOnSuccessListener { uri ->
                updateProfileImageUrl(email, uri.toString())

                currentUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()
                )
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfileImageUrl(email: String, imageUrl: String) {
        firestore.collection("usuarios").document(email)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileImage(currentUser: FirebaseUser?) {
        currentUser?.let { user ->
            user.email?.let { email ->
                firestore.collection("usuarios").document(email)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val customImageUrl = document.getString("profileImageUrl")
                            if (!customImageUrl.isNullOrEmpty()) {
                                Glide.with(this)
                                    .load(customImageUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_admin_avatar)
                                    .into(profileImageView)
                                return@addOnSuccessListener
                            }
                        }
                        user.photoUrl?.let { uri ->
                            Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .placeholder(R.drawable.ic_admin_avatar)
                                .into(profileImageView)
                        }
                    }
            }
        }
    }

    private fun signOut() {
        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
        auth.signOut()
        googleSignInClient.signOut()
        borrar_sesion()
    }

    private fun saveUserProfile(email: String, firstName: String, lastName: String, phone: String, address: String) {
        val userData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "address" to address,
            "lastUpdate" to FieldValue.serverTimestamp()
        )

        firestore.collection("usuarios").document(email)
            .set(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun loadUserProfile(email: String) {
        firestore.collection("usuarios").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    view?.findViewById<TextInputEditText>(R.id.et_first_name)?.setText(document.getString("firstName"))
                    view?.findViewById<TextInputEditText>(R.id.et_last_name)?.setText(document.getString("lastName"))
                    view?.findViewById<TextInputEditText>(R.id.et_phone)?.setText(document.getString("phone"))
                    view?.findViewById<TextInputEditText>(R.id.et_address)?.setText(document.getString("address"))
                }
            }
            .addOnFailureListener { e ->
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
                            Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
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
        val borrarSesion: SharedPreferences.Editor = requireContext()
            .getSharedPreferences(Global.preferencias_compartidas, Context.MODE_PRIVATE).edit()
        borrarSesion.clear()
        borrarSesion.apply()
    }
}