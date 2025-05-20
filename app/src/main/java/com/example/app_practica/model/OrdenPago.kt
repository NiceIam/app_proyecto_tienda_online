package com.example.app_practica.model

data class OrdenPago(
    // Datos de envío
    val direccion: String,
    val ciudad: String,
    val telefono: String,

    // Datos de pago
    val numeroTarjeta: String,
    val fechaVencimiento: String,
    val cvv: String,

    // Información de la compra
    val total: Double,
    val fecha: Long = System.currentTimeMillis() // Fecha actual por defecto
) {

    fun ultimosDigitos(): String = numeroTarjeta.takeLast(4)
}