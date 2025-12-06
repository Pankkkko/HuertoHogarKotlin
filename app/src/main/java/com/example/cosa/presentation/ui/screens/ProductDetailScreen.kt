package com.example.cosa.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cosa.R
import com.example.cosa.data.model.Producto
import com.example.cosa.presentation.ui.Components.HuertoNavbar
import com.example.cosa.presentation.viewmodel.CartViewModel
import com.example.cosa.data.model.CartItem
import com.example.cosa.presentation.viewmodel.ProductoViewModel
import com.example.cosa.presentation.viewmodel.SessionViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productoId: String,
    viewModel: ProductoViewModel = viewModel(),
    sessionViewModel: SessionViewModel,
    cartViewModel: CartViewModel
) {
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = true)
    val context = LocalContext.current

    val producto: Producto? = productos.find { it.id == productoId }

    var imagenPrincipal by remember { mutableStateOf(producto?.imagen1 ?: "") }
    var cantidadText by remember { mutableStateOf("1") }

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    fun formatPrecio(precio: Double): String {
        val nf = NumberFormat.getNumberInstance(Locale("es", "ES"))
        val whole = nf.format(precio.toLong())
        return "$$whole"
    }

    // ===============================
    //   FIX DE URL → NO DUPLICA MÁS
    // ===============================
    fun imagenUrl(nombre: String?): String {
        if (nombre.isNullOrBlank()) return ""

        // Si ya es URL completa, no la tocamos
        if (nombre.startsWith("http")) {
            Log.d("IMG_URL", "URL final (ya venía completa) = $nombre")
            return nombre
        }

        val final = "http://10.0.2.2:8080/uploads/$nombre"
        Log.d("IMG_URL", "URL final = $final")
        return final
    }

    HuertoNavbar(
        navController = navController,
        sessionViewModel = sessionViewModel,
        cartViewModel = cartViewModel
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
        ) {

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E8B57))
                    }
                }

                producto == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Producto no encontrado",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E8B57)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Volver al inicio o a la lista de productos.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { navController.navigate("home") }) {
                            Text("Ir al inicio")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // BREADCRUMB
                        item {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Inicio",
                                    color = Color(0xFF2E8B57),
                                    modifier = Modifier.clickable { navController.navigate("home") }
                                )
                                Text("  >  ")
                                Text(
                                    text = producto.categoria.replace('_', ' '),
                                    color = Color(0xFF2E8B57),
                                    modifier = Modifier.clickable {
                                        navController.navigate("productos")
                                    }
                                )
                                Text("  >  ")
                                Text(text = producto.nombre)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // CONTENIDO
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                // =============================
                                //     IMAGEN PRINCIPAL
                                // =============================
                                Column(modifier = Modifier.weight(1f)) {

                                    AsyncImage(
                                        model = imagenUrl(imagenPrincipal),
                                        contentDescription = producto.nombre,
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(id = R.drawable.iconmain),
                                        error = painterResource(id = R.drawable.iconmain),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        onError = { state ->
                                            Log.e("IMG_ERROR", "Error cargando imagen: ${state.result.throwable?.message}")
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // THUMBNAILS
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            producto.imagen1,
                                            producto.imagen2,
                                            producto.imagen3,
                                            producto.imagen4
                                        ).forEach { imgName ->

                                            AsyncImage(
                                                model = imagenUrl(imgName),
                                                contentDescription = imgName,
                                                contentScale = ContentScale.Crop,
                                                placeholder = painterResource(id = R.drawable.iconmain),
                                                error = painterResource(id = R.drawable.iconmain),
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable { imagenPrincipal = imgName ?: "" },
                                                onError = { state ->
                                                    Log.e("IMG_ERROR", "Error cargando thumbnail: ${state.result.throwable?.message}")
                                                }
                                            )
                                        }
                                    }
                                }

                                // =============================
                                //   DATOS Y BOTÓN CARRITO
                                // =============================
                                Column(modifier = Modifier.weight(1f)) {

                                    Text(producto.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Precio: ${formatPrecio(producto.precio)}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2E8B57)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(producto.descripcion)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text("Cantidad:", fontWeight = FontWeight.Medium)

                                    OutlinedTextField(
                                        value = cantidadText,
                                        onValueChange = { new ->
                                            if (new.all { it.isDigit() } || new.isEmpty())
                                                cantidadText = new
                                        },
                                        singleLine = true,
                                        modifier = Modifier.width(120.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            val cantidad = cantidadText.toIntOrNull() ?: 0
                                            if (cantidad < 1) {
                                                dialogTitle = "Error"
                                                dialogMessage = "Por favor ingresa una cantidad válida."
                                                showDialog = true
                                                return@Button
                                            }

                                            val item = CartItem(
                                                productoId = producto.id,
                                                nombre = producto.nombre,
                                                precio = producto.precio,
                                                cantidad = cantidad,
                                                imagenResName = imagenUrl(producto.imagen1)
                                            )

                                            cartViewModel.addProduct(item)

                                            dialogTitle = "Producto agregado!"
                                            dialogMessage =
                                                "${producto.nombre} x$cantidad agregado al carrito."
                                            showDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E8B57)
                                        ),
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Añadir al carrito", color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
