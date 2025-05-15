package com.example.app_practica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practica.Adapter.ProductoAdapter
import com.example.app_practica.Fragments.CarritoFragment
import com.example.app_practica.Fragments.ProductsFragment
import com.example.app_practica.Fragments.ProfileFragment
import com.example.app_practica.databinding.ActivityMainBinding
import com.example.app_practica.db.ProductosProvider

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val productsFragment = ProductsFragment()
        val carritoFragment = CarritoFragment()
        val profileFragment = ProfileFragment()

        makeCurrentFragment(productsFragment)

        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_productos -> makeCurrentFragment(productsFragment)
                R.id.ic_carrito -> makeCurrentFragment(carritoFragment)
                R.id.ic_perfil -> makeCurrentFragment(profileFragment)
            }
            true
        }


    }


    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }


}