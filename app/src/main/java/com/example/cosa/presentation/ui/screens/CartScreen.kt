package com.example.cosa.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cosa.presentation.ui.Components.CartItemRow
import com.example.cosa.presentation.ui.Components.HuertoNavbar
import com.example.cosa.presentation.ui.Components.ModalComponent
import com.example.cosa.presentation.ui.Components.ReceiptDialog
import com.example.cosa.presentation.viewmodel.CartViewModel
import com.example.cosa.presentation.viewmodel.SessionViewModel

@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(),
    sessionViewModel: SessionViewModel
) {
    var showReceipt by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }

    HuertoNavbar(
        navController = navController,
        sessionViewModel = sessionViewModel,
        cartViewModel = cartViewModel
    ) { innerPadding ->
        val items = cartViewModel.items
        // derivedStateOf observará lecturas internas de estado (mutableStateListOf) en ViewModel
        val total by remember { derivedStateOf { cartViewModel.total() } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Mi carrito de compras",
                fontSize = 22.sp,
                color = androidx.compose.ui.graphics.Color(0xFF2E8B57),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 🛒 Lista de productos
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    CartItemRow(item = item, cartViewModel = cartViewModel)
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📝 Resumen siempre abajo
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Total: $${"%.2f".format(total)}", fontSize = 18.sp)

                    var codigo by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código de descuento") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { showReceipt = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF2E8B57))
                    ) {
                        Text("Comprar", color = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }
        }

        // Boleta de compra (detalle) y confirmación
        ReceiptDialog(
            show = showReceipt,
            items = items,
            onConfirm = {
                // vaciar carrito y mostrar confirmación
                cartViewModel.clearCart()
                showReceipt = false
                showConfirmation = true
            },
            onCancel = {
                showReceipt = false
            }
        )

        // Modal de confirmación simple
        ModalComponent(
            show = showConfirmation,
            title = "¡Compra realizada!",
            message = "Gracias por tu compra.",
            onClose = { showConfirmation = false }
        )
    }
}
