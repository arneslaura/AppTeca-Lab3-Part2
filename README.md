#  Lab 3C AppTeca - Jetpack Compose & MVVM

Aplicación Android nativa desarrollada para la gestión de un catálogo de aplicaciones. Este proyecto demuestra la implementación de una arquitectura moderna y reactiva, utilizando las herramientas recomendadas por Google para el desarrollo UI declarativo (Single-Activity Architecture).

## Tecnologías y Herramientas
* **UI Declarativa:** Jetpack Compose
* **Arquitectura:** MVVM (Model-View-ViewModel)
* **Gestión de Estado:** Kotlin Coroutines (`StateFlow`), `collectAsStateWithLifecycle`
* **Navegación:** Navigation Compose (`NavHost`, `NavController`)
* **Supervivencia:** `SavedStateHandle`

## Características Principales

* **Interfaz 100% Declarativa:** Reemplazo completo del sistema tradicional de vistas (XML) y adaptadores (`RecyclerView`) por funciones `@Composable`, logrando una UI que reacciona automáticamente a los cambios de estado.
* **Navegación Integrada:** Transiciones fluidas entre el listado principal y el detalle de cada aplicación utilizando `Navigation Compose`, con manejo automático del historial de navegación (Back Stack).
* **Supervivencia de Estado Avanzada:** Sincronización del estado temporal de la pantalla (filtros y términos de búsqueda) mediante `SavedStateHandle`. Esto garantiza que la aplicación resista la muerte del proceso operativo (Process Death) sin generar interfaces inconsistentes al ser restaurada.
* **Animaciones Reactivas:** Uso de modificadores como `animateItem()` y claves de identidad (`key`) en listas perezosas (`LazyColumn`) para lograr reordenamientos y filtrados con transiciones visuales suaves.

## Instalación

El archivo instalable `.apk` se puede descargar directamente desde la sección de **Releases** en este repositorio para instalarse y probarse en cualquier dispositivo Android:

* **AppTeca v3.0 (Compose + MVVM):** Versión final con interfaz 100% declarativa, navegación integrada y manejo de estado avanzado.