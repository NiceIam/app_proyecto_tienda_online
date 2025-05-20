package com.example.app_practica.Activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practica.model.OrdenPago
import com.example.app_practica.R
import com.example.app_practica.databinding.ActivityPagoBinding


class PagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Finalizar Compra"

        // Obtener los extras del intent
        val subtotalExtra = intent.getDoubleExtra("SUBTOTAL", 0.0)
        val envioExtra = intent.getDoubleExtra("ENVIO", 0.0)
        val totalExtra = intent.getDoubleExtra("TOTAL", 0.0)
        val direccionExtra = intent.getStringExtra("DIRECCION") ?: ""
        val ciudadExtra = intent.getStringExtra("CIUDAD") ?: ""
        val telefonoExtra = intent.getStringExtra("TELEFONO") ?: ""

        // Mostrar los datos recibidos
        binding.tvSubtotal.text = "$${"%.2f".format(subtotalExtra)}"
        binding.tvEnvio.text = if (envioExtra == 0.0) "Gratis" else "$${"%.2f".format(envioExtra)}"
        binding.tvTotal.text = "$${"%.2f".format(totalExtra)}"
        binding.etDireccion.setText(direccionExtra)
        binding.etCiudad.setText(ciudadExtra)
        binding.etTelefono.setText(telefonoExtra)
    }

    private fun setupListeners() {
        binding.btnPagar.setOnClickListener {
            if (validateForm()) {
                val orden = createOrder()
                processOrder(orden)
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar datos de envío
        if (binding.etDireccion.text.isNullOrEmpty()) {
            binding.etDireccion.error = "Ingresa tu dirección"
            isValid = false
        }

        if (binding.etCiudad.text.isNullOrEmpty()) {
            binding.etCiudad.error = "Ingresa tu ciudad"
            isValid = false
        }

        if (binding.etTelefono.text.isNullOrEmpty()) {
            binding.etTelefono.error = "Ingresa tu teléfono"
            isValid = false
        }

        // Validar datos de pago
        if (binding.etNumeroTarjeta.text.isNullOrEmpty()) {
            binding.etNumeroTarjeta.error = "Ingresa el número de tarjeta"
            isValid = false
        }

        if (binding.etFechaVencimiento.text.isNullOrEmpty()) {
            binding.etFechaVencimiento.error = "Ingresa la fecha de vencimiento"
            isValid = false
        }

        if (binding.etCvv.text.isNullOrEmpty()) {
            binding.etCvv.error = "Ingresa el CVV"
            isValid = false
        }

        return isValid
    }

    private fun createOrder(): OrdenPago {
        return OrdenPago(
            direccion = binding.etDireccion.text.toString(),
            ciudad = binding.etCiudad.text.toString(),
            telefono = binding.etTelefono.text.toString(),
            numeroTarjeta = binding.etNumeroTarjeta.text.toString(),
            fechaVencimiento = binding.etFechaVencimiento.text.toString(),
            cvv = binding.etCvv.text.toString(),
            total = binding.tvTotal.text.toString().removePrefix("$").toDouble()
        )
    }

    private fun processOrder(orden: OrdenPago) {
        Toast.makeText(this, "Compra confirmada! Se enviará a ${orden.direccion}", Toast.LENGTH_LONG).show()
        saveOrder(orden)
        finish()
    }

    private fun saveOrder(orden: OrdenPago) {
        // Aquí puedes guardar la orden en Room, Firestore, SharedPreferences, etc.
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}

