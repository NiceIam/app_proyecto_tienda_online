package com.example.app_practica.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.app_practica.Activities.PagoActivity
import com.example.app_practica.R
import com.example.app_practica.databinding.FragmentDetalleProductoBinding

class DetalleProductoFragment : Fragment() {

    private var _binding: FragmentDetalleProductoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre = arguments?.getString("nombre")
        val imagenUrl = arguments?.getString("imagenUrl")
        val precioActual = arguments?.getDouble("precioActual")
        val precioAnterior = arguments?.getDouble("precioAnterior")
        val rating = arguments?.getFloat("rating") ?: 0f
        val enStock = arguments?.getBoolean("enStock") ?: false
        val descripcion = arguments?.getString("descripcion")

        // Para las especificaciones técnicas que es un Map<String, String>
        val especificacionesTecnicas = arguments?.getSerializable("especificacionesTecnicas") as? HashMap<String, String>


        binding.tvNombre.text = nombre
        binding.tvPrecioActual.text = "$$precioActual"
        binding.tvPrecioAnterior.text = "$$precioAnterior"
        binding.ratingBar.rating = rating
        binding.RatingCount.text = "(${rating.toInt()} reseñas)"
        binding.tvDescripcion.text = descripcion
        if (enStock) {
            binding.enStock.text = "En stock"
            binding.enStock.setTextColor(resources.getColor(R.color.secondary_green, null))

            // Habilitar los botones y cambiar el color de fondo a verde
            binding.btnComprarAhora.isEnabled = true
            binding.btnAddCarrito.isEnabled = true

            binding.btnComprarAhora.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.secondary_green)
            )
            binding.btnAddCarrito.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.primary_blue)
            )
        } else {
            binding.enStock.text = "Agotado"
            binding.enStock.setTextColor(resources.getColor(R.color.error_text, null))

            // Deshabilitar los botones y cambiar el color de fondo a gris
            binding.btnComprarAhora.isEnabled = false
            binding.btnAddCarrito.isEnabled = false

            // Cambiar el color a gris cuando estén deshabilitados
            val disabledColor = ContextCompat.getColorStateList(requireContext(), R.color.disabled_button_gray)

            binding.btnComprarAhora.setBackgroundTintList(disabledColor)
            binding.btnAddCarrito.setBackgroundTintList(disabledColor)
        }




        // Mostrar todas las especificaciones técnicas dinámicamente
        val especificacionesLayout = binding.layoutEspecificacionesProducto
        especificacionesLayout.removeAllViews()

        especificacionesTecnicas?.forEach { (clave, valor) ->
            val horizontalLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (4 * resources.displayMetrics.density).toInt()
                }
            }

            val claveTextView = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "$clave:"
                setTextColor(resources.getColor(R.color.text_secondary, null))
                textSize = 14f
            }

            val valorTextView = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = valor
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 14f
            }

            horizontalLayout.addView(claveTextView)
            horizontalLayout.addView(valorTextView)
            especificacionesLayout.addView(horizontalLayout)
        }

        Glide.with(requireContext())
            .load(imagenUrl)
            .into(binding.ivProducto)

        binding.btnComprarAhora.setOnClickListener {
            val intent = Intent(requireContext(), PagoActivity::class.java)

            // Supongamos que quieres pasar el precio actual como subtotal
            val subtotal = precioActual ?: 0.0
            val envio = if (subtotal > 500.0) 0.0 else 200.0 // Por ejemplo, envío gratis si supera cierto valor
            val total = subtotal + envio

            // Puedes enviar otros datos también, como nombre del producto
            intent.putExtra("SUBTOTAL", subtotal)
            intent.putExtra("ENVIO", envio)
            intent.putExtra("TOTAL", total)
            intent.putExtra("DIRECCION", "") // puedes dejar vacío o prellenar si tienes esa info
            intent.putExtra("CIUDAD", "")
            intent.putExtra("TELEFONO", "")

            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}