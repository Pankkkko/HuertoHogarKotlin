package com.example.cosa.presentation.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cosa.data.database.AppDatabase
import com.example.cosa.data.model.Usuario
import kotlinx.coroutines.launch

private val AccentGreen = Color(0xFF2E8B57)
private val BgMain = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosAdminScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val usuarioDao = remember { db.usuarioDao() }

    val scope = rememberCoroutineScope()
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }

    // estado para diálogo edición
    var mostrarDialogEdicion by remember { mutableStateOf(false) }
    var usuarioAEditar by remember { mutableStateOf<Usuario?>(null) }

    // campos del diálogo
    var editRut by remember { mutableStateOf("") }
    var editUsuario by remember { mutableStateOf("") }
    var editCorreo by remember { mutableStateOf("") }
    var editPass by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        usuarios = usuarioDao.getAll()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(BgMain)
        .padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Usuarios", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(colors = ButtonDefaults.buttonColors(containerColor = AccentGreen), onClick = { navController.navigate("registro") }) {
                Text("Agregar Usuario", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nombre", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text("RUT", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Correo", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text("Acciones", modifier = Modifier.widthIn(min = 120.dp), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(thickness = 1.dp)

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(usuarios) { u ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(u.nombre, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(u.correo, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)

                            // Acciones: ancho mínimo para que los iconos no queden cortados
                            Row(modifier = Modifier.widthIn(min = 120.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    // preparar diálogo edición
                                    usuarioAEditar = u
                                    editUsuario = u.nombre
                                    editCorreo = u.correo
                                    editPass = u.pass
                                    mostrarDialogEdicion = true
                                }, modifier = Modifier.size(48.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Editar",
                                        tint = AccentGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(onClick = {
                                    // eliminar
                                    scope.launch {
                                        usuarioDao.deleteById(u.id)
                                        usuarios = usuarioDao.getAll()
                                    }
                                }, modifier = Modifier.size(48.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(thickness = 1.dp)
                    }
                }
            }
        }
    }

    // Dialogo de edición
    if (mostrarDialogEdicion && usuarioAEditar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogEdicion = false },
            confirmButton = {
                TextButton(onClick = {
                    // guardar cambios
                    val id = usuarioAEditar!!.id
                    scope.launch {
                        usuarioDao.updateById(id, editRut, editUsuario, editCorreo, editPass)
                        usuarios = usuarioDao.getAll()
                        mostrarDialogEdicion = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogEdicion = false }) { Text("Cancelar") }
            },
            title = { Text("Editar usuario") },
            text = {
                Column {
                    OutlinedTextField(value = editRut, onValueChange = { editRut = it }, label = { Text("RUT") })
                    OutlinedTextField(value = editUsuario, onValueChange = { editUsuario = it }, label = { Text("Nombre de usuario") })
                    OutlinedTextField(value = editCorreo, onValueChange = { editCorreo = it }, label = { Text("Correo") })
                    OutlinedTextField(value = editPass, onValueChange = { editPass = it }, label = { Text("Password") })
                }
            }
        )
    }
}