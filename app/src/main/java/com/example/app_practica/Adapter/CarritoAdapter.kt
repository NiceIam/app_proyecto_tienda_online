package com.example.app_practica.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_practica.model.Producto
import com.example.app_practica.R

class CarritoAdapter(
    private val productos: MutableList<Producto>,
    private val onCambios: () -> Unit, // para notificar cambios al fragment
    private val onEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.tv_nombre)
        val precio = view.findViewById<TextView>(R.id.tv_precio)
        val cantidad = view.findViewById<TextView>(R.id.tv_cantidad)
        val btnMenos = view.findViewById<ImageButton>(R.id.btn_decrementar)
        val btnMas = view.findViewById<ImageButton>(R.id.btn_incrementar)
        val btnEliminar = view.findViewById<ImageButton>(R.id.btn_eliminar)
        val imgProducto = view.findViewById<ImageView>(R.id.img_producto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito_producto, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = "$${producto.precioActual}"
        holder.cantidad.text = producto.cantidad.toString()
        Glide.with(holder.itemView.context).load(producto.imagenUrl).into(holder.imgProducto)

        holder.btnMas.setOnClickListener {
            producto.cantidad += 1
            notifyItemChanged(position)
            onCambios()
        }

        holder.btnMenos.setOnClickListener {
            if (producto.cantidad > 1) {
                producto.cantidad -= 1
                notifyItemChanged(position)
            } else {
                productos.removeAt(position)
                notifyItemRemoved(position)
                onEliminar(producto)
            }
            onCambios()
        }

        holder.btnEliminar.setOnClickListener {
            productos.removeAt(position)
            notifyItemRemoved(position)
            onEliminar(producto)
            onCambios()
        }
    }

    override fun getItemCount(): Int = productos.size
}

