package com.example.app_practica.model

data class Producto(
 val id: Int,
 val nombre: String,
 val imagenUrl: String,
 val categoria: String,
 val precioActual: Double,
 val precioAnterior: Double,
 val rating: Float,
 val numResenias: Int,
 val enStock: Boolean,
 val descripcion: String,
 val especificacionesTecnicas: Map<String, String>,
 var cantidad: Int = 1

)