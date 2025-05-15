package com.example.app_practica.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practica.Fragments.Producto
import com.example.app_practica.R

class ProductoAdapter(private val productoList: List<Producto>, private val onClickListener: (Producto) -> Unit) : RecyclerView.Adapter<ProductoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProductoViewHolder(layoutInflater.inflate(R.layout.item_producto, parent, false))
    }

    override fun getItemCount(): Int = productoList.size

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val item = productoList[position]
        holder.render(item, onClickListener)

        holder.btnAddCart.setOnClickListener {

        }

    }

}