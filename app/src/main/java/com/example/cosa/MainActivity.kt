package com.example.cosa

import SessionViewModelFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.cosa.data.ServiceLocator
import com.example.cosa.data.database.AppDatabase
import com.example.cosa.data.repository.UsuarioRepository
import com.example.cosa.data.repository.UsuarioRemoteRepository
import com.example.cosa.data.remote.RetrofitClient
import com.example.cosa.data.remote.api.UsuarioApi
import com.example.cosa.presentation.ui.screens.*
import com.example.cosa.presentation.viewmodel.*
import com.example.cosa.ui.theme.CosaTheme

class MainActivity : ComponentActivity() {

    private lateinit var sessionViewModel: SessionViewModel
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = ServiceLocator.createUsuarioRepository(this)

        // 🔹 Inicializamos SessionViewModel con factory
        val factory = SessionViewModelFactory(repo)
        sessionViewModel = ViewModelProvider(this, factory)[SessionViewModel::class.java]

        // 🔥 Carga la sesión guardada apenas arranca la app
        sessionViewModel.loadSession(this)

        setContent {
            CosaTheme {
                AppNavigation(
                    sessionViewModel = sessionViewModel,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavigation(sessionViewModel: SessionViewModel, cartViewModel: CartViewModel) { // 👈 se recibe acá
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // 🏠 Home
        composable("home") {
            HomeScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // 🛒 Productos
        composable("productos") {
            // Crear repo hacia tu backend local
            val repo = remember {
                ServiceLocator.createProductoRepository("http://10.0.2.2:8080")
            }

            // Crear factory
            val factory = remember { ProductoViewModelFactory(repo) }

            // Crear ViewModel con repo real
            val productoViewModel: ProductoViewModel = viewModel(factory = factory)

            ProductosScreen(
                viewModel = productoViewModel,
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // 🧺 Detalle de producto
        composable("producto/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            if (id != null) {
                ProductDetailScreen(navController = navController, productoId = id, sessionViewModel = sessionViewModel, cartViewModel = cartViewModel)
            }
        }

        // 📰 Blogs
        composable("blogs") {
            BlogsScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // 👥 Nosotros
        composable("nosotros") {
            NosotrosScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // ☎️ Contacto
        composable("contacto") {
            ContactoScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // 🧾 Registro
        composable("registro") {
            val context = LocalContext.current

            val db = remember {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            val usuarioDao = remember { db.usuarioDao() }
            // Crear el remote repo y pasarlo al repo local
            val usuarioApi: UsuarioApi = RetrofitClient.crearServicio(UsuarioApi::class.java)
            val remoteRepo = remember { UsuarioRemoteRepository(usuarioApi) }
            val repo = remember { UsuarioRepository(usuarioDao, remoteRepo) }
            val factory = remember { UsuarioViewModelFactory(repo) }
            val usuarioViewModel: UsuarioViewModel = viewModel(factory = factory)

            RegisterScreen(
                navController = navController,
                usuarioViewModel = usuarioViewModel,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // 🔐 Login
        composable("login") {
            LoginScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }
        composable("carrito"){
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                sessionViewModel = sessionViewModel
            )
        }
        composable("perfil"){
            ProfileScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cartViewModel = cartViewModel
            )
        }

        // ADMIN ROUTES (sin guardias por dominio)
        composable("admin") {
            AdminMainScreen(navController = navController, sessionViewModel = sessionViewModel)
        }

        composable("admin/usuarios") {
            UsuariosAdminScreen(navController = navController)
        }

        composable("admin/productos") {
            ProductosAdminScreen(navController = navController)
        }

        composable("admin/ventas") {
            VentasAdminScreen(navController = navController)
        }
    }
}