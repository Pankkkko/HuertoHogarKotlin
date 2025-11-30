package com.example.cosa.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.cosa.R
import com.example.cosa.presentation.viewmodel.SessionViewModel
import com.example.cosa.data.repository.ProductoRepository
import com.example.cosa.data.database.AppDatabase
import com.example.cosa.data.repository.VentaRepository
import com.example.cosa.data.ServiceLocator

@Composable
fun AdminMainScreen(navController: NavHostController, sessionViewModel: SessionViewModel) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidthDp = config.screenWidthDp

    val productoRepo = remember { ServiceLocator.createProductoRepository() }
    val ventaRepo = remember { VentaRepository() }
    val db = remember { AppDatabase.getDatabase(context) }
    val usuarioDao = remember { db.usuarioDao() }

    var productosCount by remember { mutableStateOf(0) }
    var usuariosCount by remember { mutableStateOf(0) }
    var ventasCount by remember { mutableStateOf(0) }

    // colores coherentes con admin.html
    val accentGreen = Color(0xFF2E8B57)
    val accentBrown = Color(0xFF8B4513)
    val bg = Color(0xFFF7F7F7)
    val accentRed = Color(0xFFC62828)

    val email by sessionViewModel.userEmail.collectAsState()

    LaunchedEffect(Unit) {
        // cargar datos
        productosCount = productoRepo.obtenerProductos().size
        usuariosCount = usuarioDao.getAll().size
        ventasCount = ventaRepo.obtenerVentas().size
    }

    val showEmail = screenWidthDp > 480
    val showFullBackText = screenWidthDp > 360
    val isCompact = screenWidthDp < 600
    val isVeryNarrow = screenWidthDp < 360

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(8.dp)
    ) {
        // Encabezado responsive: en pantallas muy estrechas apilar elementos
        if (isVeryNarrow) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(accentGreen)
                .padding(12.dp)) {
                Text(
                    text = "Panel de Control - Admin",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    softWrap = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { navController.navigate("home") },
                        colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Volver", color = Color.White)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentGreen)
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // permitir que el título pueda ocupar 2 líneas si hace falta
                Text(
                    text = "Panel de Control - Admin",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                if (showEmail && !email.isNullOrEmpty()) {
                    Text(
                        text = email ?: "",
                        color = Color.White,
                        modifier = Modifier.padding(end = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = { navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(if (showFullBackText) "Volver a la Tienda" else "Volver", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dashboard: ocupar todo el espacio disponible
        if (isCompact) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCardColumnItem("Productos", productosCount.toString(), painterResource(id = R.drawable.icon2), accentBrown, accentGreen, isCompact = true, modifier = Modifier.weight(1f))
                DashboardCardColumnItem("Usuarios", usuariosCount.toString(), null, accentBrown, accentGreen, Icons.Filled.Person, isCompact = true, modifier = Modifier.weight(1f))
                DashboardCardColumnItem("Ventas", ventasCount.toString(), null, accentBrown, accentGreen, Icons.Filled.ShoppingCart, isCompact = true, modifier = Modifier.weight(1f))
            }
        } else {
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCardColumnItem("Productos", productosCount.toString(), painterResource(id = R.drawable.icon2), accentBrown, accentGreen, modifier = Modifier.weight(1f), isCompact = false)
                DashboardCardColumnItem("Usuarios", usuariosCount.toString(), null, accentBrown, accentGreen, Icons.Filled.Person, modifier = Modifier.weight(1f), isCompact = false)
                DashboardCardColumnItem("Ventas", ventasCount.toString(), null, accentBrown, accentGreen, Icons.Filled.ShoppingCart, modifier = Modifier.weight(1f), isCompact = false)
            }
        }

        // Área de acciones (fuera del dashboard para que no se comprima)
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            AdminActionButton(text = "Productos", onClick = { navController.navigate("admin/productos") }, bgColor = accentGreen)
            AdminActionButton(text = "Usuarios", onClick = { navController.navigate("admin/usuarios") }, bgColor = accentGreen)
            AdminActionButton(text = "Ventas", onClick = { navController.navigate("admin/ventas") }, bgColor = accentGreen)
        }
    }
}

@Composable
private fun DashboardCardColumnItem(
    title: String,
    value: String,
    iconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    iconTint: Color,
    accentColor: Color,
    vectorIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val minH = if (isCompact) 140.dp else 180.dp
    Card(modifier = modifier.fillMaxSize().heightIn(min = minH), elevation = CardDefaults.cardElevation(6.dp)) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (iconPainter != null) {
                Image(painter = iconPainter, contentDescription = title, modifier = Modifier.size(if (isCompact) 48.dp else 64.dp))
            } else if (vectorIcon != null) {
                Icon(vectorIcon, contentDescription = title, tint = iconTint, modifier = Modifier.size(if (isCompact) 48.dp else 64.dp))
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                // permitir wrapping en varias líneas para evitar recortes
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    softWrap = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(value, fontSize = 26.sp, color = accentColor)
            }
        }
    }
}

@Composable
private fun AdminActionButton(text: String, onClick: () -> Unit, bgColor: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 18.dp)
    ) {
        Text(text = text, color = Color.White, textAlign = TextAlign.Center, softWrap = true)
    }
}