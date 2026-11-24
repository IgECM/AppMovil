FROM maven:3.9.11-eclipse-temurin-17 AS compilacion
WORKDIR /codigo
COPY . .
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /app/datos
COPY --from=compilacion /codigo/ms-productos/target/ms-productos-1.0.0.jar /app/ms-productos.jar
COPY --from=compilacion /codigo/ms-movimientos/target/ms-movimientos-1.0.0.jar /app/ms-movimientos.jar
COPY --from=compilacion /codigo/ms-ventas/target/ms-ventas-1.0.0.jar /app/ms-ventas.jar
