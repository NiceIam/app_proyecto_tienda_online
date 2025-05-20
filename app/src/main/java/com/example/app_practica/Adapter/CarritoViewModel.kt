package com.example.app_practica.Adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app_practica.model.Producto

class CarritoViewModel : ViewModel() {
    private val productosEnCarrito = mutableListOf<Producto>()

    fun obtenerProductos(): List<Producto> = productosEnCarrito

    fun agregarProducto(producto: Producto) {
        val existente = productosEnCarrito.find { it.id == producto.id }
        if (existente != null) {
            existente.cantidad += 1
        } else {
            productosEnCarrito.add(producto.copy(cantidad = 1))
        }
    }

    fun eliminarProducto(producto: Producto) {
        productosEnCarrito.removeIf { it.id == producto.id }
    }

    fun incrementarCantidad(producto: Producto) {
        val item = productosEnCarrito.find { it.id == producto.id }
        item?.let { it.cantidad += 1 }
    }

    fun decrementarCantidad(producto: Producto) {
        val item = productosEnCarrito.find { it.id == producto.id }
        item?.let {
            if (it.cantidad > 1) {
                it.cantidad -= 1
            } else {
                eliminarProducto(it)
            }
        }
    }
}
