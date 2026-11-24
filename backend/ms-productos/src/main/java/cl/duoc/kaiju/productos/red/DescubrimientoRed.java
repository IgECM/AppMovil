package cl.duoc.kaiju.productos.red;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class DescubrimientoRed {
    private DatagramSocket socket;

    @PostConstruct
    public void iniciar() {
        try {
            socket = new DatagramSocket(8989);
            Thread hilo = new Thread(this::responder, "kaiju-descubrimiento");
            hilo.setDaemon(true);
            hilo.start();
        } catch (IOException excepcion) {
            System.err.println("Descubrimiento UDP no disponible: " + excepcion.getMessage());
        }
    }

    private void responder() {
        byte[] datos = new byte[128];
        while (socket != null && !socket.isClosed()) {
            try {
                DatagramPacket solicitud = new DatagramPacket(datos, datos.length);
                socket.receive(solicitud);
                String mensaje = new String(
                    solicitud.getData(), solicitud.getOffset(), solicitud.getLength(), StandardCharsets.UTF_8
                ).trim();
                if (!mensaje.equals("KAIJU_BUSCAR_SERVIDOR")) {
                    continue;
                }
                byte[] respuesta = "KAIJU_SERVIDOR".getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(
                    respuesta, respuesta.length, solicitud.getAddress(), solicitud.getPort()
                ));
            } catch (IOException excepcion) {
                if (socket == null || socket.isClosed()) {
                    break;
                }
            }
        }
    }

    @PreDestroy
    public void detener() {
        if (socket != null) {
            socket.close();
        }
    }
}
