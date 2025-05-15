package com.example.app_practica.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practica.R

class HomeActivity : AppCompatActivity() {

    private lateinit var btn_comenzar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btn_comenzar = findViewById(R.id.btn_comenzar)
        btn_comenzar.setOnClickListener{
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}