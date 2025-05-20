package com.example.app_practica.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app_practica.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var btnBogota: MaterialButton
    private lateinit var btnMedellin: MaterialButton
    private lateinit var btnCartagena: MaterialButton

    // Coordenadas de las ciudades
    private val bogota = LatLng(4.7110, -74.0721)
    private val medellin = LatLng(6.2442, -75.5812)
    private val cartagena = LatLng(10.3910, -75.4794)
    private val defaultZoom = 12f

    // Coordenadas de sedes simuladas cercanas
    private val sedesBogota = listOf(
        LatLng(4.715, -74.070),
        LatLng(4.710, -74.068),
        LatLng(4.712, -74.075)
    )

    private val sedesMedellin = listOf(
        LatLng(6.246, -75.578),
        LatLng(6.240, -75.584),
        LatLng(6.242, -75.579)
    )

    private val sedesCartagena = listOf(
        LatLng(10.392, -75.478),
        LatLng(10.389, -75.476),
        LatLng(10.393, -75.482)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)

        btnBogota = view.findViewById(R.id.btnBogota)
        btnMedellin = view.findViewById(R.id.btnMedellin)
        btnCartagena = view.findViewById(R.id.btnCartagena)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setupButtons()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        with(googleMap) {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            moveCameraToCity(bogota)
        }
    }

    private fun setupButtons() {
        btnBogota.setOnClickListener {
            moveCameraToCity(bogota)
            highlightSelectedButton(btnBogota)
        }

        btnMedellin.setOnClickListener {
            moveCameraToCity(medellin)
            highlightSelectedButton(btnMedellin)
        }

        btnCartagena.setOnClickListener {
            moveCameraToCity(cartagena)
            highlightSelectedButton(btnCartagena)
        }
    }

    private fun moveCameraToCity(position: LatLng) {
        if (::googleMap.isInitialized) {
            googleMap.clear()

            // Marcador central
            googleMap.addMarker(MarkerOptions().position(position).title("Ubicación central"))

            // Agregar marcadores de sedes según la ciudad
            val sedes = when (position) {
                bogota -> sedesBogota
                medellin -> sedesMedellin
                cartagena -> sedesCartagena
                else -> emptyList()
            }

            sedes.forEachIndexed { index, sede ->
                googleMap.addMarker(
                    MarkerOptions()
                        .position(sede)
                        .title("Sede ${index + 1}")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoom))
        }
    }

    private fun highlightSelectedButton(selectedButton: MaterialButton) {
        listOf(btnBogota, btnMedellin, btnCartagena).forEach { button ->
            button.apply {
                setBackgroundColor(resources.getColor(android.R.color.transparent, null))
                setTextColor(resources.getColor(R.color.primary_blue, null))
                strokeWidth = 1
            }
        }

        selectedButton.apply {
            setBackgroundColor(resources.getColor(R.color.primary_blue, null))
            setTextColor(resources.getColor(R.color.white, null))
            strokeWidth = 0
        }
    }
}
