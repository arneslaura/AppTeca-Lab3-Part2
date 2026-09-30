package com.example.appteca3

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppTecaViewModel(private val state: SavedStateHandle) : ViewModel() {

    val textoBusqueda: StateFlow<String> = state.getStateFlow("query", "")
    val modoSoloFavoritas: StateFlow<Boolean> = state.getStateFlow("soloFavoritas", false)

    private val _listaVisible = MutableStateFlow<List<App>>(emptyList())
    val listaVisible: StateFlow<List<App>> = _listaVisible

    private val _appSeleccionada = MutableStateFlow<App?>(null)
    val appSeleccionada: StateFlow<App?> = _appSeleccionada

    fun seleccionar(app: App) { _appSeleccionada.value = app }
    fun volverALista() { _appSeleccionada.value = null }

    init {
        Log.d("VIDA", "ViewModel creado (${hashCode()})")
        aplicarFiltros()
    }

    fun buscar(texto: String) {
        state["query"] = texto
        aplicarFiltros()
    }

    fun alternarModo() {
        val actual = state.get<Boolean>("soloFavoritas") ?: false
        state["soloFavoritas"] = !actual
        aplicarFiltros()
    }

    fun alternarFavorita(app: App) {
        val nuevas = Catalogo.apps.map {
            if (it.id == app.id) it.copy(esFavorita = !it.esFavorita) else it
        }
        Catalogo.apps.clear()
        Catalogo.apps.addAll(nuevas)
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        var lista: List<App> = Catalogo.apps.toList()

        val queryActual = state.get<String>("query") ?: ""
        val modoFavActual = state.get<Boolean>("soloFavoritas") ?: false

        if (queryActual.isNotEmpty()) {
            lista = lista.filter {
                it.nombre.contains(queryActual, true) || it.categoria.contains(queryActual, true)
            }
        }
        if (modoFavActual) {
            lista = lista.filter { it.esFavorita }
        }

        _listaVisible.value = lista

        _appSeleccionada.value = _appSeleccionada.value?.let { sel ->
            Catalogo.apps.find { it.id == sel.id }
        }
    }

    override fun onCleared() {
        Log.d("VIDA", "ViewModel onCleared")
    }
}