package com.example.cosa.presentation.ui.screens

import android.content.res.Resources
import androidx.compose.foundation.Image
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
    val resources: Resources = context.resources

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
                        item {
                            // breadcrumb
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

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // IMAGEN PRINCIPAL + THUMBS
                                Column(modifier = Modifier.weight(1f)) {
                                    val mainResId = resources.getIdentifier(
                                        imagenPrincipal,
                                        "drawable",
                                        context.packageName
                                    )
                                    val mainPainter = if (mainResId != 0)
                                        painterResource(id = mainResId)
                                    else
                                        painterResource(id = R.drawable.iconmain)

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        shape = RoundedCornerShape(8.dp),
                                    ) {
                                        Image(
                                            painter = mainPainter,
                                            contentDescription = producto.nombre,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val thumbs = listOf(
                                            producto.imagen1,
                                            producto.imagen2,
                                            producto.imagen3,
                                            producto.imagen4
                                        )
                                        thumbs.forEach { imgName ->
                                            val resId = resources.getIdentifier(
                                                imgName,
                                                "drawable",
                                                context.packageName
                                            )
                                            val painter = if (resId != 0)
                                                painterResource(id = resId)
                                            else
                                                painterResource(id = R.drawable.iconmain)

                                            Image(
                                                painter = painter,
                                                contentDescription = imgName,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable { imagenPrincipal = imgName },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }

                                // DETALLE Y COMPRA
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

                                    // ✅ Botón Añadir al carrito
                                    Button(
                                        onClick = {
                                            val cantidad = cantidadText.toIntOrNull() ?: 0
                                            if (cantidad < 1) {
                                                dialogTitle = "Error"
                                                dialogMessage = "Por favor ingresa una cantidad válida (mínimo 1)."
                                                showDialog = true
                                                return@Button
                                            }

                                            // 💚 Agregar al carrito
                                            val item = CartItem(
                                                productoId = producto.id,
                                                nombre = producto.nombre,
                                                precio = producto.precio,
                                                cantidad = cantidad,
                                                imagenResName = producto.imagen1
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

    // AlertDialog de confirmación
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
