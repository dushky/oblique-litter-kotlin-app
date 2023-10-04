package com.example.obliquelitterkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.obliquelitterkotlin.databinding.ActivityMainBinding
import com.example.obliquelitterkotlin.ui.theme.ObliqueLitterKotlinTheme

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding // Declare a binding variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Initialize the binding

        setContentView(binding.root)

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Ahoj, $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ObliqueLitterKotlinTheme {
        Greeting("Android")
    }
}