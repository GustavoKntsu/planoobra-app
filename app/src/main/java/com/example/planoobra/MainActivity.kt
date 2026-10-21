package com.example.planoobra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.planoobra.navigation.AppNav
import com.example.planoobra.ui.theme.PlanoObraTheme // Import do tema novo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanoObraTheme { // Chamando o tema novo
                AppNav()
            }
        }
    }
}