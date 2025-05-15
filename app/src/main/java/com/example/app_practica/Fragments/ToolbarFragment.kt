package com.example.app_practica.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.app_practica.R  // Importa el R de tu paquete

class ToolbarFragment : Fragment() {
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Cambia a tu propio R.layout
        val view = inflater.inflate(R.layout.fragment_toolbar, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        return view
    }

    fun setTitle(title: String?) {
        toolbar?.title = title
    }
}