# Kaiju — Gestión de inventario

Aplicación académica Android creada con Kotlin y Jetpack Compose para gestionar productos, stock, movimientos y ventas de Kaiju. El inventario principal se encuentra en los microservicios Spring Boot.

## Integrantes

- Ignacio Cárdenas
- Vicente Araya
- Tomás Gaete

## Funcionalidades

- Crear, consultar, editar y eliminar productos del inventario del servidor.
- Alertas visuales cuando el stock llega al mínimo.
- Registrar entradas y salidas con historial y stock anterior y posterior.
- Registrar ventas y descontar existencias.
- Consultar el catálogo externo de DummyJSON con Retrofit, sin agregar automáticamente sus productos al inventario.
- Compartir un resumen de los productos del servidor mediante las aplicaciones disponibles en Android.
- Vibración al guardar correctamente un producto en el servidor.
- Room y sus pruebas quedan en el código como respaldo de la evaluación anterior, sin acceso desde la interfaz principal.

## Tecnologías

- Android Studio, Kotlin y Jetpack Compose.
- Retrofit para conectarse con los microservicios y con DummyJSON.
- Spring Boot, JDK 17, Maven y H2 para los 3 microservicios.
- Room como respaldo de la funcionalidad local anterior.
- JUnit, MockK, Robolectric y JaCoCo para las pruebas.

## Estructura del proyecto

```text
KaijuProyecto/
├── app/                    Aplicación Android
├── backend/                El backend (IntelliJ)
│   ├── ms-productos/       Productos y stock
│   ├── ms-movimientos/     Entradas y salidas
│   └── ms-ventas/          Ventas
├── gradle/                 Archivos de Gradle
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew.bat
├── .gitignore
└── README.md
```

## Microservicios

Los tres microservicios se encuentran dentro de `backend/` y fueron desarrollados con Spring Boot en IntelliJ IDEA.

El servicio de **productos** utiliza el puerto **8081** y permite administrar los productos y su stock. El servicio de **movimientos** funciona en el puerto **8082** y registra las entradas y salidas. El servicio de **ventas** utiliza el puerto **8083**, registra las ventas y descuenta las existencias correspondientes.

Cada servicio utiliza H2 para guardar sus datos. Los tres deben estar funcionando para utilizar todas las opciones del inventario. Se usa IntelliJ para correr.

## Cómo ejecutar el proyecto

1. Abrir `backend/` en IntelliJ IDEA, compilar e iniciar los tres microservicios Spring Boot en el computador que funcionará como servidor.
2. Conectar el computador y el teléfono a la misma red local, o a una red donde ambos puedan comunicarse, y permitir las conexiones a los puertos 8081, 8082 y 8083.
3. Revisar `app/src/main/java/cl/duoc/proyectosemestralapp/data/remoto/ConfiguracionServidor.kt` y cambiar `direccionComputador` por la dirección IP local del computador si es necesario.
4. Abrir la carpeta principal `KaijuProyecto` en Android Studio, seleccionar **Gradle JDK 17** y sincronizar el proyecto.
5. Ejecutar la aplicación en el teléfono.
6. Al abrir Kaiju, se muestra directamente el inventario conectado a Spring Boot.

El inventario del servidor necesita que los microservicios estén ejecutándose y que el teléfono pueda conectarse al computador. El catálogo externo de DummyJSON solo necesita conexión a Internet.

## Pruebas unitarias

Desde la carpeta principal del proyecto, ejecutar en PowerShell:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:informeCoberturaLogica
```

Durante las pruebas, se ejecutaron **66 pruebas unitarias, con 0 fallos**. El informe de JaCoCo registró **91 % de cobertura de instrucciones** y **90 % de cobertura de ramas** para los paquetes `dominio`, `data.repositorio` y `ui.viewmodel`.

El informe se genera en `app/build/reports/cobertura-logica/index.html`.

##  APK firmado
https://drive.google.com/file/d/1Yd_8WN2Ul9aeZlFcRhu1ytsb3H2fZ3vU/view?usp=sharing

## Links

- Drive: https://drive.google.com/file/d/1nXCghdSdqdtD4OL6iNDZYR8vNCjTlVx1/view?usp=sharing
- GitHub: https://github.com/IgECM/AppMovil
- Trello: https://trello.com/invite/b/6ad0e58deaf6414da2d7214f/ATTIa7cf53fa540badbf7d1b127e2277f860B3F26DB3/trabajo-desarollo-movil
