package com.example.cosa.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cosa.data.model.Producto
import com.example.cosa.data.ServiceLocator
import kotlinx.coroutines.launch

private val AccentGreen = Color(0xFF2E8B57)
private val BgMain = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosAdminScreen(navController: NavHostController) {
    val repo = remember { ServiceLocator.createProductoRepository() }
    val scope = rememberCoroutineScope()

    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var mostrandoFormulario by remember { mutableStateOf(false) }
    var editarProducto by remember { mutableStateOf<Producto?>(null) }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagen1 by remember { mutableStateOf("") }
    var imagen2 by remember { mutableStateOf("") }
    var imagen3 by remember { mutableStateOf("") }
    var imagen4 by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productos = repo.obtenerProductos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain)
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Productos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                onClick = {
                    mostrandoFormulario = !mostrandoFormulario

                    if (!mostrandoFormulario) {
                        // limpiar
                        editarProducto = null
                        nombre = ""; descripcion = ""; precio = ""; stock = ""
                        categoria = ""; imagen1 = ""; imagen2 = ""; imagen3 = ""; imagen4 = ""
                    }
                }
            ) {
                Text(
                    if (mostrandoFormulario) "Cerrar formulario" else "Agregar Producto",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Formulario colapsable
        AnimatedVisibility(visible = mostrandoFormulario) {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = if (editarProducto == null) "Agregar Nuevo Producto" else "Editar Producto",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = imagen1, onValueChange = { imagen1 = it }, label = { Text("Imagen 1") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = imagen2, onValueChange = { imagen2 = it }, label = { Text("Imagen 2") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = imagen3, onValueChange = { imagen3 = it }, label = { Text("Imagen 3") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = imagen4, onValueChange = { imagen4 = it }, label = { Text("Imagen 4") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Button(
                            onClick = {
                                // Validar
                                val precioNum = precio.toDoubleOrNull()
                                val stockNum = stock.toIntOrNull()

                                if (nombre.isBlank() || descripcion.isBlank() || precioNum == null || stockNum == null) {
                                    return@Button
                                }

                                val nuevoProducto = Producto(
                                    id = editarProducto?.id ?: "", // se ignora en POST si está vacío
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precioNum,
                                    stock = stockNum,
                                    categoria = categoria,
                                    imagen1 = imagen1,
                                    imagen2 = imagen2,
                                    imagen3 = imagen3,
                                    imagen4 = imagen4
                                )

                                scope.launch {
                                    if (editarProducto == null) {
                                        // CREAR
                                        repo.agregarProducto(nuevoProducto)
                                    } else {
                                        // ACTUALIZAR
                                        repo.actualizarProducto(nuevoProducto)
                                    }

                                    productos = repo.obtenerProductos()

                                    // Limpiar
                                    mostrandoFormulario = false
                                    editarProducto = null
                                    nombre = ""; descripcion = ""; precio = ""; stock = ""
                                    categoria = ""; imagen1 = ""; imagen2 = ""; imagen3 = ""; imagen4 = ""
                                }
                            }
                        ) {
                            Text("Guardar")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(onClick = {
                            mostrandoFormulario = false
                            editarProducto = null
                        }) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabla
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Nombre", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text("Precio", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Stock", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Acciones", modifier = Modifier.widthIn(min = 96.dp), fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(thickness = 1.dp)

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(productos) { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(p.nombre, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(p.precioFormateado, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(p.stock.toString(), modifier = Modifier.weight(1f))

                            Row(
                                modifier = Modifier.widthIn(min = 96.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                IconButton(
                                    onClick = {
                                        editarProducto = p
                                        mostrandoFormulario = true

                                        nombre = p.nombre
                                        descripcion = p.descripcion
                                        precio = p.precio.toString()
                                        stock = p.stock.toString()
                                        categoria = p.categoria
                                        imagen1 = p.imagen1
                                        imagen2 = p.imagen2
                                        imagen3 = p.imagen3
                                        imagen4 = p.imagen4
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AccentGreen)
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            repo.eliminarProducto(p.id)
                                            productos = repo.obtenerProductos()
                                        }
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                }
                            }
                        }

                        HorizontalDivider(thickness = 1.dp)
                    }
                }
            }
        }
    }
}
