package com.example.appteca3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppTecaViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var query = ""
    private var soloFavoritas = false
    private var todas: List<App> = emptyList()
    private var suscripcionCatalogo: ListenerRegistration? = null

    private val _listaVisible = MutableStateFlow<List<App>>(emptyList())
    val listaVisible: StateFlow<List<App>> = _listaVisible

    private val _modoSoloFavoritas = MutableStateFlow(false)
    val modoSoloFavoritas: StateFlow<Boolean> = _modoSoloFavoritas

    private val _appSeleccionada = MutableStateFlow<App?>(null)
    val appSeleccionada: StateFlow<App?> = _appSeleccionada

    private val _usuario = MutableStateFlow(auth.currentUser)
    val usuario: StateFlow<FirebaseUser?> = _usuario

    private val _errorAuth = MutableStateFlow<String?>(null)
    val errorAuth: StateFlow<String?> = _errorAuth

    init {
        Log.d("VIDA", "ViewModel creado (${hashCode()})")
        auth.addAuthStateListener {
            _usuario.value = it.currentUser
            if (it.currentUser != null) {
                sembrarSiHaceFalta()
                escucharCatalogo()
            } else {
                suscripcionCatalogo?.remove()
                todas = emptyList()
                aplicarFiltros()
            }
        }
    }

    fun registrar(email: String, pass: String) {
        viewModelScope.launch {
            try {
                _errorAuth.value = null
                auth.createUserWithEmailAndPassword(email, pass).await()
            } catch (e: Exception) {
                _errorAuth.value = e.localizedMessage
            }
        }
    }

    fun entrar(email: String, pass: String) {
        viewModelScope.launch {
            try {
                _errorAuth.value = null
                auth.signInWithEmailAndPassword(email, pass).await()
            } catch (e: Exception) {
                _errorAuth.value = e.localizedMessage
            }
        }
    }

    fun salir() = auth.signOut()

    private fun coleccionApps() = db.collection("usuarios")
        .document(auth.currentUser!!.uid)
        .collection("apps")

    private fun sembrarSiHaceFalta() {
        viewModelScope.launch {
            try {
                val existentes = coleccionApps().get().await()
                if (existentes.isEmpty) {
                    Catalogo.apps.forEach { app ->
                        coleccionApps().document(app.id.toString()).set(app).await()
                    }
                    Log.d("NUBE", "Catálogo sembrado: ${Catalogo.apps.size} apps")
                }
            } catch (e: Exception) {
                Log.e("NUBE", "Error sembrando", e)
            }
        }
    }

    private fun escucharCatalogo() {
        suscripcionCatalogo?.remove()
        suscripcionCatalogo = coleccionApps().addSnapshotListener { snap, error ->
            if (error != null) {
                Log.e("NUBE", "Error escuchando", error)
                return@addSnapshotListener
            }
            todas = snap?.toObjects(App::class.java) ?: emptyList()
            Log.d("NUBE", "Snapshot: ${todas.size} apps")
            aplicarFiltros()
        }
    }

    fun buscar(texto: String) {
        query = texto.trim()
        aplicarFiltros()
    }

    fun alternarModo() {
        soloFavoritas = !soloFavoritas
        aplicarFiltros()
    }

    fun alternarFavorita(app: App) {
        coleccionApps().document(app.id.toString())
            .update("esFavorita", !app.esFavorita)
    }

    fun seleccionar(app: App) { _appSeleccionada.value = app }

    fun volverALista() { _appSeleccionada.value = null }

    private fun aplicarFiltros() {
        var lista: List<App> = todas

        if (query.isNotEmpty()) lista = lista.filter {
            it.nombre.contains(query, true) || it.categoria.contains(query, true)
        }
        if (soloFavoritas) lista = lista.filter { it.esFavorita }

        _listaVisible.value = lista
        _modoSoloFavoritas.value = soloFavoritas

        _appSeleccionada.value = _appSeleccionada.value?.let { sel ->
            todas.find { it.id == sel.id }
        }
    }

    override fun onCleared() {
        suscripcionCatalogo?.remove()
        Log.d("VIDA", "ViewModel onCleared")
    }
}
