package com.example.app_practica.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practica.R
import com.example.app_practica.Adapter.ProductoAdapter
import com.example.app_practica.databinding.FragmentProductsBinding
import com.example.app_practica.db.ProductosProvider


class ProductsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí inicializamos el RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerProductos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter =
            ProductoAdapter(ProductosProvider.productoList) {producto ->
                onItemSelected(
                    producto
                )
            }
    }

    private fun onItemSelected(producto: Producto) {
        val detalleFragment = DetalleProductoFragment()
        val bundle = Bundle().apply {
            putString("nombre", producto.nombre)
            putString("imagenUrl", producto.imagenUrl)
            putDouble("precioActual", producto.precioActual)
            putDouble("precioAnterior", producto.precioAnterior)
            putFloat("rating", producto.rating)
            putBoolean("enStock", producto.enStock)
            putString("descripcion", producto.descripcion)
            putSerializable("especificacionesTecnicas", HashMap(producto.especificacionesTecnicas))
        }
        detalleFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wrapper, detalleFragment)
            .addToBackStack(null)
            .commit()
    }

}
