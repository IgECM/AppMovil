# Kaiju — Microservicios Spring Boot

El backend está dentro de la carpeta `backend/` del mismo proyecto que la aplicación Android. Se desarrolló en Java 17 con Spring Boot, Maven y bases de datos H2. No es necesario instalar Docker para ejecutarlo.

## Estructura

```text
backend/
├── ms-productos/       Productos y stock (puerto 8081)
├── ms-movimientos/     Entradas y salidas (puerto 8082)
├── ms-ventas/          Ventas (puerto 8083)
├── pom.xml             Configuración Maven
└── README.md
```

El microservicio de productos permite crear, consultar, modificar y eliminar productos. El de movimientos guarda el historial de entradas y salidas y actualiza el stock mediante el servicio de productos. El de ventas registra ventas y utiliza los otros servicios para descontar existencias y registrar el movimiento.

## Cómo ejecutarlos

Puedes abrir la carpeta `backend/` en IntelliJ IDEA y ejecutar las clases principales de los tres proyectos. También puedes compilar desde PowerShell, ubicado dentro de `backend/`:

```powershell
mvn clean package
```

Después, abre tres consolas distintas en la carpeta `backend/` y ejecuta un comando en cada una:

```powershell
java -jar ms-productos/target/ms-productos-1.0.0.jar
```

```powershell
java -jar ms-movimientos/target/ms-movimientos-1.0.0.jar
```

```powershell
java -jar ms-ventas/target/ms-ventas-1.0.0.jar
```

Primero inicia productos y después los otros dos servicios. Mantén abiertas las tres consolas mientras utilices Kaiju.

Para comprobar que productos está funcionando, puedes abrir `http://localhost:8081/api/productos` desde el navegador del computador.

## Conexión con Android

El teléfono necesita poder comunicarse con el PC en los puertos 8081, 8082 y 8083. La dirección IP del computador se configura en `app/src/main/java/cl/duoc/proyectosemestralapp/data/remoto/ConfiguracionServidor.kt`.

Los datos se almacenan en H2. Los archivos de base de datos generados al ejecutar los servicios no deben subirse a GitHub.

Este proyecto se utiliza como parte de una evaluación académica y no está preparado para exponer sus servicios directamente a Internet.

## Detección automática de la red

El servidor de productos responde a la búsqueda automática de Android por UDP (puerto 8989) y al endpoint `GET /api/identidad` en 8081. No necesita configurar una IP fija: los tres servicios escuchan en todas las interfaces. El PC y el teléfono deben estar conectados a la misma red local, por ejemplo mediante el hotspot del Samsung. El firewall de Windows debe permitir los puertos 8081, 8082 y 8083; UDP 8989 acelera el descubrimiento, pero no es indispensable cuando funciona la búsqueda HTTP.
