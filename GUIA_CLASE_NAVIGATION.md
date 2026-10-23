# Guía de clase: navegación Android con XML

**Proyecto base:** C:\Users\lusht\AndroidStudioProjects\PM1ExampleNavXML  
**Material principal:** C:\Users\lusht\Downloads\11-Navigation.pptx

## Orden de la clase

1. Presentar la PPT completa y explicar los conceptos con ejemplos cotidianos.
2. Durante la PPT, no mostrar código. Solo explicar qué problema resuelve cada recurso.
3. Al finalizar la PPT, abrir el proyecto y construir los ejemplos.
4. Cerrar con una actividad práctica y una verificación.

La primera parte responde **qué problema resolvemos**. La segunda muestra **cómo lo implementamos**.

---

# Parte 1: PPT y explicación conceptual

## 1. Múltiples pantallas

Explicar que una aplicación puede separar su funcionalidad en varias pantallas.

Ejemplos para mencionar oralmente:

- Una app de compras muestra el detalle de un producto.
- Una app de correo crea un mensaje nuevo.
- Una app muestra sus configuraciones en otra pantalla.
- Una galería solicita abrir una imagen en otra aplicación.

Pregunta para el curso:

> ¿Conviene poner toda la interfaz y toda la lógica en una sola Activity?

Idea clave: separar pantallas facilita mantener la UI, organizar responsabilidades y controlar la navegación.

## 2. Activities e Intents

Una Activity representa una pantalla o punto de entrada de una aplicación.

Un Intent expresa una solicitud de acción entre componentes.

Explicar la diferencia:

- Intent explícito: conocemos exactamente qué Activity queremos abrir.
- Intent implícito: describimos una acción y Android busca una aplicación compatible.

Ejemplos orales:

- Abrir Settings dentro de nuestra aplicación: Intent explícito.
- Enviar un email: Intent implícito.
- Compartir una imagen: Intent implícito.
- Abrir una pantalla de detalle propia: Intent explícito o navegación interna.

Punto importante: un Intent puede transportar datos, por ejemplo el identificador del producto o de la nota seleccionada.

## 3. App Bar y menús

La App Bar concentra el título, la navegación hacia atrás y acciones importantes.

Los menús permiten ofrecer acciones como:

- Settings
- Search
- Share
- Help

Explicar que el menú se define como recurso XML y que la Activity o el componente de navegación decide qué ocurre cuando el usuario selecciona un ítem.

## 4. Navigation Drawer

El Navigation Drawer es un panel lateral con destinos principales de la aplicación.

Ejemplo oral:

- Home
- Gallery
- Settings
- About

Diferenciar:

- El drawer muestra opciones principales.
- El menú superior muestra acciones contextuales o secundarias.

## 5. Fragments

Un Fragment representa una porción de UI y comportamiento dentro de una Activity.

Ejemplo oral:

- La Activity funciona como el contenedor general.
- HomeFragment muestra la lista.
- DetailFragment muestra el detalle.
- SettingsFragment muestra preferencias.

Ventajas:

- Divide la aplicación en pantallas más pequeñas.
- Permite reutilizar UI.
- Facilita que una Activity administre varios destinos.

Usar la versión AndroidX: androidx.fragment.app.Fragment.

## 6. Navigation Component

Navigation Component reúne las piezas necesarias para describir y ejecutar la navegación.

Sus tres elementos principales son:

### Navigation Graph

Describe:

- destinos disponibles;
- acciones entre destinos;
- destino inicial;
- argumentos;
- opcionalmente, animaciones y deep links.

### NavHost

Es el contenedor visual que muestra el destino actual.

### NavController

Ejecuta los cambios de destino y administra la pila de navegación.

La idea central:

> El grafo describe las rutas. El controller las ejecuta. El host muestra el destino actual.

## 7. Pasar datos entre destinos

Una pantalla suele necesitar información de la pantalla anterior.

Ejemplos:

- Home envía noteId a Detail.
- Una lista envía productId al detalle.
- Una pantalla de cálculo envía dos números al resultado.

Safe Args permite declarar el tipo del dato y generar clases para enviarlo y recibirlo.

Ventaja: reduce errores por nombres incorrectos, tipos incompatibles y conversiones manuales.

## 8. Back stack

Android mantiene una pila de destinos visitados.

Explicar oralmente:

1. El usuario entra a Home.
2. Desde Home abre Detail.
3. Desde Detail vuelve con Atrás.
4. Android retira Detail y muestra Home.

Representación:

~~~text
Home -> Detail -> About
Back  -> Detail
Back  -> Home
~~~

Pregunta para el curso:

> ¿Por qué Atrás vuelve a la pantalla anterior en lugar de cerrar directamente la aplicación?

---

# Parte 2: implementación al finalizar la PPT

## 9. Preparar el proyecto

Verificar:

- MainActivity extiende AppCompatActivity.
- La UI inicial está en res/layout/activity_main.xml.
- La app ejecuta correctamente.
- El paquete es com.maralbadev.pm1_example_navxml.

Agregar:

- fragment-ktx
- navigation-fragment-ktx
- navigation-ui-ktx
- navigation-safe-args-gradle-plugin

Centralizar las versiones en gradle/libs.versions.toml y sincronizar Gradle.

## 10. Crear la estructura

~~~text
MainActivity
└── NavHostFragment
    ├── HomeFragment
    └── DetailFragment
~~~

## 11. Agregar el NavHost

En activity_main.xml:

~~~xml
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/main_nav_graph" />
~~~

defaultNavHost permite que el NavHost gestione el botón Atrás. navGraph vincula el contenedor con el archivo de navegación.

## 12. Crear el Navigation Graph

En res/navigation/nav_graph.xml:

~~~xml
<navigation
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.maralbadev.pm1_example_navxml.HomeFragment">

        <action
            android:id="@+id/action_homeFragment_to_detailFragment"
            app:destination="@id/detailFragment" />
    </fragment>

    <fragment
        android:id="@+id/detailFragment"
        android:name="com.maralbadev.pm1_example_navxml.DetailFragment" />
</navigation>
~~~

Verificar:

- HomeFragment es el destino inicial.
- DetailFragment está declarado.
- La action conecta ambos destinos.

## 13. Implementar la navegación

Desde HomeFragment:

~~~kotlin
findNavController().navigate(
    R.id.action_homeFragment_to_detailFragment
)
~~~

Para volver:

~~~kotlin
findNavController().popBackStack()
~~~

## 14. Pasar datos entre destinos

La implementación actual usa Safe Args para enviar un argumento tipado entre destinos:

~~~kotlin
val action = HomeFragmentDirections
    .actionHomeFragmentToDetailFragment(getString(R.string.detail_item_name))
findNavController().navigate(action)
~~~

En DetailFragment:

~~~kotlin
private val args: DetailFragmentArgs by navArgs()
val itemTitle = args.itemTitle
~~~

Safe Args genera las clases HomeFragmentDirections y DetailFragmentArgs a partir del argumento definido en nav_graph.xml. Así se evitan nombres escritos manualmente y se conserva el tipo String durante el envío y la recepción.

## 15. Implementar un Intent

Intent explícito:

~~~kotlin
val intent = Intent(this, SettingsActivity::class.java)
startActivity(intent)
~~~

Intent implícito:

~~~kotlin
val intent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:docente@example.com")
}

if (intent.resolveActivity(packageManager) != null) {
    startActivity(intent)
}
~~~

Usar Navigation Component para destinos internos. Usar Intent para otra Activity, el sistema o una aplicación externa.

## 16. Menú simple

El ejemplo base usa un único ítem de menú: Enviar email.

1. Crear el recurso en res/menu/main.xml.
2. Inflarlo en MainActivity.
3. Ejecutar un Intent implícito al seleccionarlo.

El Navigation Drawer, SettingsFragment y AboutFragment quedan como ampliaciones para una segunda clase. No forman parte del flujo mínimo.

# Actividad práctica

Completar una app de notas con:

- HomeFragment: muestra una nota y un botón.
- DetailFragment: recibe itemTitle mediante Safe Args.
- Una acción de Navigation Graph conecta Home con Detail.
- El menú permite enviar un email con un Intent implícito.

## Checklist final

- [ ] MainActivity extiende AppCompatActivity.
- [ ] Existe un NavHostFragment.
- [ ] HomeFragment es el destino inicial.
- [ ] El Navigation Graph declara Home, Detail y su action.
- [ ] Home navega a Detail.
- [ ] Detail recupera el argumento recibido.
- [ ] Atrás respeta el back stack.
- [ ] El menú ejecuta el Intent de email.
- [ ] El Intent verifica resolveActivity.
- [ ] La aplicación compila y se ejecuta en el emulador.

## Preguntas de cierre

1. ¿Qué diferencia hay entre Activity y Fragment?
2. ¿Qué describe el Navigation Graph?
3. ¿Qué hace el NavController?
4. ¿Qué muestra el NavHost?
5. ¿Qué problema resuelve Safe Args?
6. ¿Cuándo usamos Navigation Component y cuándo un Intent?
7. ¿Qué representa el back stack?










