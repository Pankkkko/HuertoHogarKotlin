// File: ProductosScreen.kt (reemplaza el existente)
package com.example.cosa.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cosa.data.Enum.CategoriaENUM
import com.example.cosa.presentation.ui.Components.FooterSection
import com.example.cosa.presentation.ui.Components.HuertoNavbar
import com.example.cosa.presentation.ui.Components.ProductoCard
import com.example.cosa.presentation.viewmodel.CartViewModel
import com.example.cosa.presentation.viewmodel.ProductoViewModel
import com.example.cosa.presentation.viewmodel.SessionViewModel

@Composable
fun ProductosScreen(viewModel: ProductoViewModel, navController: NavController, sessionViewModel: SessionViewModel, cartViewModel: CartViewModel) {
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = true)

    var categoriaSeleccionada by remember { mutableStateOf("Todos") }



    HuertoNavbar(navController = navController, sessionViewModel = sessionViewModel, cartViewModel = cartViewModel) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding) // importantísimo: respetar el padding del scaffold
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Botones de filtro
            CategoryButtons(
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaChange = { nueva -> categoriaSeleccionada = nueva }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2E8B57))
                    }
                }

                productos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠️ No hay productos disponibles",
                            color = Color(0xFF2E8B57),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(productos) { producto ->
                            ProductoCard(producto = producto, navController = navController)
                        }

                        item { FooterSection() }
                    }


                }
            }
        }
    }
}

@Composable
fun CategoryButtons(
    categoriaSeleccionada: String,
    onCategoriaChange: (String) -> Unit
) {
    val categorias = listOf("Todos", "Frutas", "Verduras", "Orgánicos", "Lácteos")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        categorias.forEach { categoria ->
            val isSelected = categoria == categoriaSeleccionada
            Button(
                onClick = { onCategoriaChange(categoria) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF2E8B57) else Color.White,
                    contentColor = if (isSelected) Color.White else Color(0xFF2E8B57)
                ),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(40.dp)
            ) {
                Text(text = categoria, fontSize = 14.sp)
            }
        }
    }
}
