package com.example.appteca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appteca3.ui.theme.AppTeca3Theme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTeca3Theme {
                PantallaAppTeca()
            }
        }
    }
}

@Composable
fun FilaApp(
    app: App,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(app.nombre, style = MaterialTheme.typography.titleMedium)
            Text(app.categoria, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = if (app.esFavorita) "★" else "☆",
            fontSize = 24.sp,
            modifier = Modifier
                .clickable { onFavoritoClick() }
                .padding(8.dp)
        )
    }
}

@Composable
fun ListaApps(
    apps: List<App>,
    onAppClick: (App) -> Unit,
    onFavoritoClick: (App) -> Unit
) {
    LazyColumn {
        items(apps, key = { it.id }) { app ->
            FilaApp(
                app = app,
                modifier = Modifier.animateItem(),
                onClick = { onAppClick(app) },
                onFavoritoClick = { onFavoritoClick(app) }
            )
        }
    }
}

@Composable
fun PantallaAppTeca(vm: AppTecaViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {

        // RUTA 1: LA PANTALLA PRINCIPAL
        composable("lista") {
            val lista by vm.listaVisible.collectAsStateWithLifecycle()
            val modoFav by vm.modoSoloFavoritas.collectAsStateWithLifecycle()
            val textoBusqueda by vm.textoBusqueda.collectAsStateWithLifecycle()

            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { nuevo ->
                        vm.buscar(nuevo)
                    },
                    label = { Text("Buscar por nombre o categoría...") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
                Button(
                    onClick = { vm.alternarModo() },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(if (modoFav) "★ Solo favoritas" else "☆ Todas")
                }

                ListaApps(
                    apps = lista,
                    onAppClick = { app ->
                        vm.seleccionar(app)
                        navController.navigate("detalle")
                    },
                    onFavoritoClick = { app -> vm.alternarFavorita(app) }
                )
            }
        }

        // RUTA 2: LA PANTALLA DE DETALLE
        composable("detalle") {
            val seleccionada by vm.appSeleccionada.collectAsStateWithLifecycle()

            if (seleccionada != null) {
                DetalleApp(
                    app = seleccionada!!,
                    onFavoritoClick = { vm.alternarFavorita(seleccionada!!) },
                    onVolver = {
                        vm.volverALista()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun DetalleApp (app: App, onFavoritoClick: () -> Unit, onVolver: () -> Unit) {
    BackHandler { onVolver() }
    Column (modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp)) {
        Text (app.nombre, style = MaterialTheme.typography.headlineLarge)
        Text(app.categoria, style = MaterialTheme.typography.bodyMedium)
        Spacer (Modifier.height(16.dp))
        Text (app.descripcion, style = MaterialTheme.typography.bodyLarge)
        Spacer (Modifier.height(24.dp))
        Button (onClick = onFavoritoClick) {
            Text (if (app.esFavorita) "★ Quitar de favoritas" else "☆ Marcar favorita")
        }
    }
}
