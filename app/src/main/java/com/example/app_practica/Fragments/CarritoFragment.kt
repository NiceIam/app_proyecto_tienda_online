package com.example.app_practica.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practica.Activities.PagoActivity
import com.example.app_practica.Adapter.CarritoAdapter
import com.example.app_practica.Adapter.CarritoViewModel
import com.example.app_practica.Adapter.ProductoAdapter
import com.example.app_practica.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class CarritoFragment : Fragment() {

    private val carritoViewModel: CarritoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_carrito, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCarrito = view.findViewById<RecyclerView>(R.id.rv_carrito)
        val emptyState = view.findViewById<View>(R.id.empty_state)
        val resumenCompra = view.findViewById<CardView>(R.id.card_resumen)
        val btnIrTienda = view.findViewById<Button>(R.id.btn_ir_tienda)
        val btnFinalizarCompra = view.findViewById<Button>(R.id.btn_pagar)

        val tvSubtotal = view.findViewById<TextView>(R.id.tv_subtotal)
        val tvEnvio = view.findViewById<TextView>(R.id.tv_envio)
        val tvTotal = view.findViewById<TextView>(R.id.tv_total)

        val productosEnCarrito = carritoViewModel.obtenerProductos().toMutableList()

        var subtotal = 0.0
        var envio = 0.0
        var total = 0.0

        fun actualizarVista() {
            if (productosEnCarrito.isEmpty()) {
                rvCarrito.visibility = View.GONE
                resumenCompra.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            } else {
                rvCarrito.visibility = View.VISIBLE
                resumenCompra.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
            }
        }

        fun actualizarTotales() {
            subtotal = productosEnCarrito.sumOf { it.precioActual * it.cantidad }
            envio = if (subtotal >= 500.0) 0.0 else 100.0
            total = subtotal + envio

            tvSubtotal.text = "Subtotal: $${"%,.2f".format(subtotal)}"
            tvEnvio.text = if (envio == 0.0) "Envío: Gratis" else "Envío: $${"%,.2f".format(envio)}"
            tvTotal.text = "Total: $${"%,.2f".format(total)}"
        }

        // Configura RecyclerView
        rvCarrito.layoutManager = LinearLayoutManager(requireContext())
        rvCarrito.adapter = CarritoAdapter(
            productosEnCarrito,
            onCambios = {
                actualizarVista()
                actualizarTotales()
            },
            onEliminar = { producto ->
                carritoViewModel.eliminarProducto(producto)
                productosEnCarrito.remove(producto)
                rvCarrito.adapter?.notifyDataSetChanged()
                actualizarVista()
                actualizarTotales()
            }
        )

        actualizarVista()
        actualizarTotales()

        // Botón para volver a la tienda
        btnIrTienda.setOnClickListener {
            val fragment = ProductsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_wrapper, fragment)
                .addToBackStack(null)
                .commit()

            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.selectedItemId = R.id.ic_productos
        }

        // Botón para finalizar la compra
        btnFinalizarCompra.setOnClickListener {
            val intent = Intent(requireContext(), PagoActivity::class.java).apply {
                putExtra("SUBTOTAL", subtotal)
                putExtra("ENVIO", envio)
                putExtra("TOTAL", total)
            }
            startActivity(intent)
        }
    }
}


