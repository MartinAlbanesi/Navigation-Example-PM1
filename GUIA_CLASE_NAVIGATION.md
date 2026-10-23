# Guía de clase: Navigation Component con XML

**Proyecto:** `PM1ExampleNavXML` (Kotlin, vistas XML, paquete `com.maralbadev.pm1_example_navxml`)
**Material de apoyo:** `11-Navigation.pptx`

Esta guía se sigue **en orden**. Cada paso indica en qué archivo trabajás, qué representa ese código y qué deberías ver al terminar.

> Ruta base de código Kotlin: `app/src/main/java/com/maralbadev/pm1_example_navxml/`
> Ruta base de recursos: `app/src/main/res/`

---

## Parte 1: conceptos (antes de abrir el código)

| Concepto | Qué representa | Analogía |
|---|---|---|
| **Activity** | Contenedor general de la app (una "ventana"). | El marco del cuadro |
| **Fragment** | Una pantalla o porción de UI dentro de una Activity. | Cada cuadro que cambia dentro del marco |
| **Navigation Graph** | Archivo XML que describe destinos, acciones y argumentos. | El mapa de rutas |
| **NavHost** | Contenedor visual donde se muestra el destino actual. | La pantalla del GPS |
| **NavController** | Objeto que ejecuta los cambios de destino y maneja la pila. | El conductor |
| **Action** | Flecha del grafo: "desde A se puede ir a B". | Una calle del mapa |
| **Argumento** | Dato que viaja de un destino a otro. | El paquete que llevás |
| **Back stack** | Pila de destinos visitados; "Atrás" quita el último. | Pila de platos |

Idea central:

> El **grafo** describe las rutas. El **NavController** las ejecuta. El **NavHost** muestra el destino actual.

Back stack de esta app:

```text
Home -> Detail
Atrás -> Home
Atrás -> sale de la app
```

Intent (se usa en el menú): un `Intent` es una solicitud de acción. Explícito = conocemos la Activity destino. Implícito = describimos la acción (enviar email) y Android busca una app compatible. **Navigation Component para destinos internos; Intent para apps externas u otras Activities.**

---

## Mapa del proyecto

| Archivo | Qué representa | Paso |
|---|---|---|
| `gradle/libs.versions.toml` | Catálogo central de versiones de dependencias | 1 |
| `build.gradle.kts` (raíz) | Declara el plugin de Safe Args (classpath) | 1 |
| `app/build.gradle.kts` | Dependencias de Navigation y activación de Safe Args | 1 |
| `res/values/strings.xml` | Textos de la app (títulos, botón, mensajes) | 2 |
| `res/navigation/nav_graph.xml` | **El grafo**: destinos, action y argumento | 3 |
| `res/layout/fragment_home.xml` | UI de la pantalla Home | 4 |
| `res/layout/fragment_detail.xml` | UI de la pantalla Detail | 4 |
| `res/layout/activity_main.xml` | Toolbar + **NavHost** (`nav_host_fragment`) | 5 |
| `MainActivity.kt` | Conecta Toolbar con el NavController, maneja "Atrás" y el menú | 6 |
| `HomeFragment.kt` | Destino inicial; dispara la navegación y envía el argumento | 7 |
| `DetailFragment.kt` | Destino que recibe el argumento | 8 |
| `res/menu/main.xml` | Ítem de menú "Enviar email" | 9 |
| `AndroidManifest.xml` | Declara `MainActivity` y `<queries>` para el Intent de email | 9 |

---

## Parte 2: construcción paso a paso

### Paso 1. Dependencias y Safe Args

**Archivos:** `gradle/libs.versions.toml`, `build.gradle.kts` (raíz), `app/build.gradle.kts`

Qué representa: las librerías que traen `NavHostFragment`, `NavController` y las utilidades para conectar la Toolbar. Safe Args es un plugin que **genera clases** a partir del grafo.

`gradle/libs.versions.toml`:

```toml
navigation = "2.10.2"
androidx-navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigation" }
androidx-navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigation" }
```

`build.gradle.kts` (raíz), dentro de `buildscript`:

```kotlin
classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.10.2")
```

`app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs.kotlin")
}

dependencies {
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
}
```

- `navigation-fragment-ktx`: `NavHostFragment`, `findNavController()`, `navArgs()`.
- `navigation-ui-ktx`: `setupActionBarWithNavController`, `AppBarConfiguration`.
- Plugin `safeargs.kotlin`: genera `HomeFragmentDirections` y `DetailFragmentArgs`.

**Checkpoint:** sincronizá Gradle (Sync Now). Debe terminar sin errores. Todavía no hay cambios visibles en la app.

---

### Paso 2. Textos

**Archivo:** `res/values/strings.xml`

Qué representa: todos los textos visibles. Los layouts y el código los referencian con `@string/...` o `R.string...`, nunca texto "pegado".

```xml
<string name="home_title">Home</string>
<string name="open_detail">Ver detalle</string>
<string name="detail_title">Detail</string>
<string name="detail_item_name">Ejemplo de producto</string>
<string name="action_send_email">Enviar email</string>
<string name="no_email_app">No hay una aplicación de email disponible.</string>
```

`detail_item_name` es el valor que Home le va a enviar a Detail (paso 7).

**Checkpoint:** el archivo compila; no hay referencias en rojo más adelante.

---

### Paso 3. El Navigation Graph

**Archivo:** `res/navigation/nav_graph.xml`

Qué representa: el **mapa de la app**. Define:

- **Destinos**: cada `<fragment>` es una pantalla.
- **`startDestination`**: la primera pantalla.
- **`<action>`**: una ruta posible entre destinos.
- **`<argument>`**: el dato que Detail necesita para abrirse.

```xml
<navigation ...
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.maralbadev.pm1_example_navxml.HomeFragment"
        android:label="@string/home_title"
        tools:layout="@layout/fragment_home">
        <action
            android:id="@+id/action_homeFragment_to_detailFragment"
            app:destination="@id/detailFragment" />
    </fragment>

    <fragment
        android:id="@+id/detailFragment"
        android:name="com.maralbadev.pm1_example_navxml.DetailFragment"
        android:label="@string/detail_title"
        tools:layout="@layout/fragment_detail">
        <argument
            android:name="itemTitle"
            app:argType="string" />
    </fragment>
</navigation>
```

Cómo leerlo:

| Elemento | Significa |
|---|---|
| `android:id="@+id/homeFragment"` | Identificador del destino (`R.id.homeFragment`) |
| `android:name="...HomeFragment"` | Clase Kotlin que dibuja ese destino |
| `android:label` | Título que la Toolbar mostrará en ese destino |
| `tools:layout` | Solo para la vista previa en el editor |
| `action_homeFragment_to_detailFragment` | Ruta Home → Detail |
| `itemTitle` + `argType="string"` | Detail exige un `String` llamado `itemTitle` |

**Checkpoint:** en Android Studio, abrí el archivo en la pestaña **Design**: ves dos cajas (Home con ícono de casa = inicio, Detail) unidas por una flecha. Las clases `HomeFragment` y `DetailFragment` todavía pueden aparecer en rojo; se crean en los pasos 7 y 8.

---

### Paso 4. Layouts de los fragments

**Archivos:** `res/layout/fragment_home.xml`, `res/layout/fragment_detail.xml`

Qué representan: la UI de cada pantalla. Solo nos importan los **ids**, porque el código los usa para encontrar las vistas.

`fragment_home.xml` (resumen): un título, una descripción y el botón:

```xml
<Button
    android:id="@+id/button_open_detail"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="24dp"
    android:text="@string/open_detail" />
```

`fragment_detail.xml` (resumen): un `TextView` donde se mostrará el argumento recibido:

```xml
<TextView
    android:id="@+id/text_detail"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textAppearance="?attr/textAppearanceHeadlineSmall"
    android:textStyle="bold" />
```

| Id | Quién lo usa |
|---|---|
| `button_open_detail` | `HomeFragment` (click para navegar) |
| `text_detail` | `DetailFragment` (muestra `itemTitle`) |

**Checkpoint:** en la vista previa, Home muestra título, descripción y botón "Ver detalle"; Detail muestra un texto fijo centrado (el título aún vacío).

---

### Paso 5. El NavHost en la Activity

**Archivo:** `res/layout/activity_main.xml`

Qué representa: el layout de `MainActivity`. Tiene dos piezas: la **Toolbar** (id `toolbar`) y el **NavHost** (id `nav_host_fragment`), que es el espacio donde se irán intercambiando Home y Detail.

```xml
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="?attr/colorPrimary"
    app:titleTextColor="@android:color/white" />

<fragment
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />
```

- `android:name="androidx.navigation.fragment.NavHostFragment"`: este fragment es el **NavHost**.
- `app:navGraph="@navigation/nav_graph"`: le dice qué grafo usar (el del paso 3).
- `app:defaultNavHost="true"`: intercepta el botón Atrás del sistema para manejar el back stack.

**Checkpoint:** al ejecutar la app ya se ve la Toolbar y la pantalla **Home** (el destino inicial), con el botón "Ver detalle" (que todavía no hace nada).

---

### Paso 6. MainActivity: Toolbar + NavController

**Archivo:** `MainActivity.kt` → `onCreate`, `onSupportNavigateUp`

Qué representa: la Activity que **sostiene** el NavHost y conecta la Toolbar con la navegación.

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment))

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
```

Línea por línea:

| Línea | Qué hace |
|---|---|
| `setSupportActionBar(findViewById(R.id.toolbar))` | Usa la Toolbar del layout como ActionBar |
| `findNavController(R.id.nav_host_fragment)` | Obtiene el **NavController** del NavHost del paso 5 |
| `AppBarConfiguration(setOf(R.id.homeFragment))` | Define los destinos "raíz": en Home **no** se muestra la flecha de volver |
| `setupActionBarWithNavController(...)` | La Toolbar muestra el `label` del destino y la flecha "arriba" cuando corresponde |
| `onSupportNavigateUp()` | Cuando se toca la flecha de la Toolbar, delega en el NavController |

**Checkpoint:** la Toolbar muestra el título **"Home"** (el `label` del grafo) y no tiene flecha de volver.

---

### Paso 7. HomeFragment: navegar y enviar el argumento

**Archivo:** `HomeFragment.kt` → `onCreateView`, `onViewCreated`

Qué representa: la pantalla inicial. Infla su layout y, al tocar el botón, le pide al NavController ir a Detail **enviando un dato**.

```kotlin
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.button_open_detail).setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToDetailFragment(getString(R.string.detail_item_name))
            findNavController().navigate(action)
        }
    }
}
```

| Pieza | Qué representa |
|---|---|
| `onCreateView` | Convierte `fragment_home.xml` en una vista |
| `onViewCreated` | La vista ya existe: acá se buscan vistas y se asignan listeners |
| `HomeFragmentDirections` | Clase **generada por Safe Args** a partir del grafo |
| `actionHomeFragmentToDetailFragment(...)` | Función generada desde `action_homeFragment_to_detailFragment`; recibe el argumento `itemTitle` como parámetro tipado |
| `findNavController().navigate(action)` | El NavController ejecuta la acción y apila Detail |

Detrás de escena Safe Args arma un `Bundle` con la clave `itemTitle`; vos no escribís la clave a mano, y por eso no hay errores de tipeo.

**Checkpoint:** al tocar "Ver detalle" la app cambia de pantalla (aún puede fallar si falta el paso 8; si `DetailFragment` no existe, no compila).

---

### Paso 8. DetailFragment: recibir el argumento

**Archivo:** `DetailFragment.kt`

Qué representa: el destino que recibe y muestra el dato.

```kotlin
class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.text_detail).text = args.itemTitle
    }
}
```

| Pieza | Qué representa |
|---|---|
| `DetailFragmentArgs` | Clase **generada por Safe Args** desde `<argument android:name="itemTitle">` |
| `by navArgs()` | Lee el `Bundle` de argumentos del destino de forma perezosa |
| `args.itemTitle` | El `String` que envió Home ("Ejemplo de producto") |

**Checkpoint:** tocar "Ver detalle" muestra Detail con el texto **"Ejemplo de producto"**, la Toolbar dice **"Detail"** y aparece la flecha de volver. Tocar la flecha o Atrás vuelve a Home.

---

### Paso 9. Menú y Intent implícito

**Archivos:** `res/menu/main.xml`, `MainActivity.kt` (`onCreateOptionsMenu`, `onOptionsItemSelected`, `sendEmail`), `AndroidManifest.xml`

Qué representa: una acción de la Toolbar (⋮) que **no** es navegación interna sino un pedido al sistema: abrir una app de email.

`res/menu/main.xml`:

```xml
<item
    android:id="@+id/action_send_email"
    android:orderInCategory="10"
    android:title="@string/action_send_email"
    app:showAsAction="never" />
```

`MainActivity.kt`:

```kotlin
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.action_send_email -> {
            sendEmail()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
```

`sendEmail()` (resumen de la lógica real):

```kotlin
val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:$recipient")
    putExtra(Intent.EXTRA_SUBJECT, subject)
    putExtra(Intent.EXTRA_TEXT, body)
}

if (emailIntent.resolveActivity(packageManager) != null) {
    startActivity(emailIntent)
    return
}
// si no: intenta Intent.ACTION_SEND con chooser, y si tampoco hay app:
Toast.makeText(this, R.string.no_email_app, Toast.LENGTH_LONG).show()
```

| Pieza | Qué representa |
|---|---|
| `menuInflater.inflate(R.menu.main, menu)` | Dibuja el menú XML en la Toolbar |
| `onOptionsItemSelected` | Decide qué hacer según el ítem tocado |
| `ACTION_SENDTO` + `mailto:` | Intent **implícito**: "quiero escribir un email" |
| `resolveActivity(packageManager)` | Verifica que exista una app que pueda atenderlo |
| `Toast` | Mensaje de respaldo si no hay app de email |
| `<queries>` en el Manifest | Desde Android 11 la app debe declarar que consulta apps `mailto`; sin esto `resolveActivity` devuelve `null` |

`AndroidManifest.xml`:

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.SENDTO" />
        <data android:scheme="mailto" />
    </intent>
</queries>
```

**Checkpoint:** en la Toolbar aparece el menú ⋮ con "Enviar email". En un emulador sin app de email se ve el Toast "No hay una aplicación de email disponible."; en un dispositivo con Gmail se abre el borrador.

---

## Parte 3: SharedPreferences

> **Estado en este proyecto:** el código actual **no usa SharedPreferences** (no hay ninguna referencia en `app/src`). Esta sección es una **ampliación propuesta**: el ejemplo de abajo **no está implementado** en el repositorio.

### Qué es

`SharedPreferences` guarda **pares clave-valor pequeños** (String, Int, Boolean, Float, Long) en un archivo XML privado de la app. Los datos **sobreviven** a cerrar la app o reiniciar el teléfono.

### Cuándo usarlo

- Configuraciones simples (tema, idioma, "no mostrar de nuevo").
- Recordar el último ítem visto o el nombre del usuario.
- **No** para listas grandes ni datos relacionales (para eso: Room/SQLite).
- **No** para contraseñas ni datos sensibles.

### Diferencia con un argumento de Navigation

| | Argumento (Bundle / Safe Args) | SharedPreferences |
|---|---|---|
| Vive | Mientras el destino existe | Permanente en disco |
| Sirve para | Pasar un dato de A a B | Recordar algo entre ejecuciones |

### API básica

```kotlin
// 1. Abrir (o crear) el archivo de preferencias
val prefs = requireContext().getSharedPreferences("mis_prefs", Context.MODE_PRIVATE)

// 2. Escribir
prefs.edit().putString("last_item", "Ejemplo de producto").apply()

// 3. Leer (con valor por defecto si la clave no existe)
val last = prefs.getString("last_item", null)
```

| Elemento | Significado |
|---|---|
| `getSharedPreferences("mis_prefs", ...)` | Nombre del archivo de preferencias |
| `Context.MODE_PRIVATE` | Solo esta app puede leerlo (el modo que se usa siempre) |
| `edit()` | Abre un editor para modificar |
| `putString(clave, valor)` | Guarda un String (también `putInt`, `putBoolean`...) |
| `getString(clave, default)` | Lee; devuelve `default` si no existe |
| `apply()` | Guarda **en segundo plano** (asíncrono). **Recomendado** |
| `commit()` | Guarda **de forma síncrona** y devuelve `true/false`. Bloquea el hilo; usalo solo si necesitás saber el resultado |

### Ejemplo propuesto para esta app: "último ítem visto"

Idea: al abrir Detail se guarda el nombre del ítem; al volver (o reabrir la app) Home muestra "Último visto: ...".

**(a) `res/values/strings.xml`: agregar**

```xml
<string name="last_seen_format">Último visto: %1$s</string>
<string name="last_seen_none">Todavía no viste ningún ítem.</string>
```

**(b) `res/layout/fragment_home.xml`: agregar un `TextView` dentro del `LinearLayout`, después del botón**

```xml
<TextView
    android:id="@+id/text_last_seen"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="24dp" />
```

**(c) `DetailFragment.kt` → dentro de `onViewCreated`: guardar**

```kotlin
// agregar import android.content.Context
requireContext()
    .getSharedPreferences("mis_prefs", Context.MODE_PRIVATE)
    .edit()
    .putString("last_item", args.itemTitle)
    .apply()
```

**(d) `HomeFragment.kt` → dentro de `onViewCreated`: leer y mostrar**

```kotlin
// agregar import android.content.Context y android.widget.TextView
val prefs = requireContext().getSharedPreferences("mis_prefs", Context.MODE_PRIVATE)
val last = prefs.getString("last_item", null)
view.findViewById<TextView>(R.id.text_last_seen).text =
    if (last != null) getString(R.string.last_seen_format, last)
    else getString(R.string.last_seen_none)
```

**Checkpoint (cuando se implemente):**
1. Primera vez: Home muestra "Todavía no viste ningún ítem."
2. Tocá "Ver detalle", volvé con Atrás: Home muestra "Último visto: Ejemplo de producto".
3. Cerrá la app por completo y volvé a abrirla: el texto **sigue ahí**. Esa es la diferencia con un argumento.

Nota: `onViewCreated` de Home se vuelve a ejecutar al volver desde Detail (la vista se recrea), por eso el texto se actualiza.

---

## Errores comunes

| Síntoma | Causa probable |
|---|---|
| `HomeFragmentDirections` / `DetailFragmentArgs` en rojo | No se aplicó el plugin Safe Args (paso 1), falta Sync, o falta el `<argument>`/`<action>` en el grafo |
| La app muestra pantalla vacía o crashea al iniciar | `app:navGraph` apunta a un grafo que no existe o `startDestination` no está declarado |
| `findNavController` falla: "does not have a NavController set" | Id incorrecto: debe ser `R.id.nav_host_fragment` (paso 5) |
| No aparece la flecha de volver en Detail | Falta `setupActionBarWithNavController` o `onSupportNavigateUp` en `MainActivity` |
| Aparece flecha de volver en Home | Home no está en `AppBarConfiguration(setOf(R.id.homeFragment))` |
| Crash al navegar: "argument itemTitle is required" | Se navegó sin enviar el argumento obligatorio |
| `resolveActivity` siempre `null` | Falta el bloque `<queries>` en el Manifest |
| `NullPointerException` en `findViewById` | Id equivocado, o se busca antes de `onViewCreated` |
| SharedPreferences no persiste | Se olvidó `apply()`/`commit()` tras `putString` |
