// File: ProductoCard.kt
package com.example.cosa.presentation.ui.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cosa.data.model.Producto
import coil.compose.AsyncImage
import android.util.Log

@Composable
fun ProductoCard(producto: Producto, navController: NavController) {

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ------------------------------------------------------------------
            // 🔥 FIX DE URL PARA IMAGENES DESDE EL BACKEND 🔥
            // Esto convierte:
            // "/manzana.jpg" -> http://10.0.2.2:8080/uploads/manzana.jpg
            // "/uploads/manzana.jpg" -> http://10.0.2.2:8080/uploads/manzana.jpg
            // "manzana.jpg" -> http://10.0.2.2:8080/uploads/manzana.jpg
            // Ya no usa drawables locales
            // ------------------------------------------------------------------

            val imagen = producto.imagen1 ?: ""

            val finalUrl = when {
                imagen.startsWith("http") -> imagen
                imagen.startsWith("/uploads") -> "http://10.0.2.2:8080$imagen"
                imagen.startsWith("/") -> "http://10.0.2.2:8080/uploads$imagen"
                else -> "http://10.0.2.2:8080/uploads/$imagen"
            }

            Log.d("IMG_URL", "Cargando imagen desde: $finalUrl")

            // ------------------------------------------------------------------
            // 🔥 Cargar imagen remota con Coil
            // ------------------------------------------------------------------
            AsyncImage(
                model = finalUrl,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(
                producto.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Descripción corta
            Text(
                producto.descripcion,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de compra
            Button(
                onClick = {
                    navController.navigate("producto/${producto.id}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E8B57)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Comprar: ${producto.precioFormateado}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
