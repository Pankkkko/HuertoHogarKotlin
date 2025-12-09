package com.example.cosa.presentation.ui.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosa.data.model.CartItem
import kotlin.math.round

@Composable
fun ReceiptDialog(
    show: Boolean,
    items: List<CartItem>,
    taxRate: Double = 0.0,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (!show) return

    val subtotal = items.sumOf { it.precio * it.cantidad }
    val tax = (subtotal * taxRate * 100).let { round(it) } / 100.0
    val total = (subtotal + tax).let { round(it * 100) } / 100.0

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Boleta de compra", fontSize = 20.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.nombre, fontSize = 16.sp)
                                Text("Cantidad: ${item.cantidad}", fontSize = 12.sp)
                            }
                            Text("$${"%.2f".format(item.precio * item.cantidad)}", fontSize = 14.sp)
                        }
                        Divider()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Subtotal:")
                    Text("$${"%.2f".format(subtotal)}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Impuestos:")
                    Text("$${"%.2f".format(tax)}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", fontSize = 16.sp)
                    Text("$${"%.2f".format(total)}", fontSize = 16.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}

