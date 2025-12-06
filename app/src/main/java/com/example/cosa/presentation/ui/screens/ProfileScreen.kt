package com.example.cosa.presentation.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cosa.presentation.ui.Components.HuertoNavbar
import com.example.cosa.presentation.ui.Components.ModalComponent
import com.example.cosa.presentation.viewmodel.SessionViewModel
import com.example.cosa.presentation.viewmodel.CartViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current

    var showModal by remember { mutableStateOf(false) }
    var modalMessage by remember { mutableStateOf("") }

    val user by sessionViewModel.currentUser.collectAsState()

    var username by remember(user) { mutableStateOf(user?.nombre ?: "") }
    var password by remember { mutableStateOf("") }

    var profilePic by remember { mutableStateOf<Bitmap?>(null) }

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            profilePic = bitmap
        }
    }

    // Launcher para pedir permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para abrir cámara con permiso
    fun openCameraWithPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(null)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.CAMERA
            ) -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    HuertoNavbar(
        navController = navController,
        sessionViewModel = sessionViewModel,
        cartViewModel = cartViewModel
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Mi Perfil",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                color = androidx.compose.ui.graphics.Color(0xFF2E8B57)
            )

            // FOTO DE PERFIL
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        androidx.compose.ui.graphics.Color(0xFF2E8B57).copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { openCameraWithPermission() },
                contentAlignment = Alignment.Center
            ) {
                if (profilePic != null) {
                    Image(
                        bitmap = profilePic!!.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        "Tocar para tomar foto",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF2E8B57)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = user?.correo ?: "",
                        onValueChange = {},
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Nueva contraseña") },
                        placeholder = { Text("Ingresa una nueva contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            sessionViewModel.updateUser(
                                updatedUsername = username,
                                updatedPassword = if (password.isNotBlank()) password else null
                            ) { success ->
                                modalMessage = if (success)
                                    "Perfil actualizado correctamente"
                                else
                                    "Error al actualizar"

                                showModal = true
                                password = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFF2E8B57)
                        )
                    ) {
                        Text("Guardar cambios", color = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }

            ModalComponent(
                show = showModal,
                title = "Perfil",
                message = modalMessage,
                onClose = { showModal = false }
            )
        }
    }
}
