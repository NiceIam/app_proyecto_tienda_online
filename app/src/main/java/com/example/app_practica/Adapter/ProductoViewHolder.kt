package com.example.app_practica.Adapter

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_practica.model.Producto
import com.example.app_practica.R
import com.example.app_practica.databinding.ItemProductoBinding

class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemProductoBinding.bind(view)


    // IMPLEMENTACION DEL BINDING
//    val producto = view.findViewById<TextView>(R.id.tvNombre)
//    val imagen = view.findViewById<ImageView>(R.id.ivProducto)
//    val precio = view.findViewById<TextView>(R.id.tvPrecioActual)
//    val precioAnterior = view.findViewById<TextView>(R.id.tvPrecioAnterior)
//    val rating = view.findViewById<TextView>(R.id.tvRatingCount)
//    val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

    val btnAddCart = view.findViewById<TextView>(R.id.btnAddCart)


    fun render(productoModel: Producto, onClickListener: (Producto) -> Unit) {
        binding.tvNombre.text = productoModel.nombre
        Glide.with(binding.ivProducto.context).load(productoModel.imagenUrl).into(binding.ivProducto)

        binding.tvPrecioActual.text = "$${productoModel.precioActual}"
        binding.tvPrecioAnterior.text = "$${productoModel.precioAnterior}"

        binding.tvRatingCount.text = "(${productoModel.rating})"
        binding.ratingBar.rating = productoModel.rating

        binding.tvDescripcion.text = productoModel.descripcion


        // Mostrar especificación 1 (clave y valor)
        val specs = productoModel.especificacionesTecnicas.toList()
        if (specs.isNotEmpty()) {
            binding.valorEspecificacion1.text = "${specs[0].first}:"
            binding.valorEspecificacion2.text = specs[0].second
        } else {
            binding.valorEspecificacion1.visibility = View.GONE
            binding.valorEspecificacion2.visibility = View.GONE
        }

        // Aquí podrías hacer similar para la segunda especificación si tienes más TextViews para eso

        // Mostrar stock (ejemplo con TextView que muestra si hay stock)
        binding.enStock.text = if (productoModel.enStock) "En stock" else "Agotado"

        binding.itemProducto.setOnClickListener { onClickListener(productoModel) }

        itemView.setOnClickListener {
            onClickListener(productoModel)
        }

    }

}