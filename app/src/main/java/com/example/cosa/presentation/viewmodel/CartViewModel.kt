package com.example.cosa.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.cosa.data.model.CartItem
import com.example.cosa.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {

    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = _items

    private val _count = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _count

    fun addProduct(cartItem: CartItem) {
        val existing = _items.indexOfFirst { it.productoId == cartItem.productoId }
        if (existing >= 0) {
            // Reemplazamos el elemento con una copia modificada para forzar recomposición
            val prev = _items[existing]
            _items[existing] = prev.copy(cantidad = prev.cantidad + cartItem.cantidad)
        } else {
            _items.add(cartItem)
        }
        recomputeCount()
    }

    fun addProductFromProducto(producto: Producto) {
        // adapta según tu modelo Producto
        addProduct(
            CartItem(
                productoId = producto.id,
                nombre = producto.nombre,
                precio = producto.precio,
                cantidad = 1,
                imagenResName = producto.imagen1 // opcional
            )
        )
    }

    fun removeProduct(productoId: String) {
        _items.removeAll { it.productoId == productoId }
        recomputeCount()
    }

    fun increase(productoId: String) {
        val idx = _items.indexOfFirst { it.productoId == productoId }
        if (idx >= 0) {
            val it = _items[idx]
            _items[idx] = it.copy(cantidad = it.cantidad + 1)
            recomputeCount()
        }
    }

    fun decrease(productoId: String) {
        val idx = _items.indexOfFirst { it.productoId == productoId }
        if (idx < 0) return
        val it = _items[idx]
        val newCantidad = it.cantidad - 1
        if (newCantidad <= 0) {
            _items.removeAt(idx)
        } else {
            _items[idx] = it.copy(cantidad = newCantidad)
        }
        recomputeCount()
    }

    fun clearCart() {
        _items.clear()
        recomputeCount()
    }

    fun total(): Double {
        return _items.sumOf { it.precio * it.cantidad }
    }

    private fun recomputeCount() {
        _count.value = _items.sumOf { it.cantidad }
    }
}