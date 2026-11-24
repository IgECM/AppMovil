package cl.duoc.proyectosemestralapp.data.remoto

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object DescubrimientoServidor {
    private data class RedLocal(val direccion: Inet4Address, val difusion: InetAddress?)
    private data class Candidato(val ip: String, val origen: Inet4Address?)

    @Volatile
    private var servidorEncontrado: String? = null

    @Synchronized
    fun obtenerDireccion(): String {
        servidorEncontrado?.let { return it }
        val encontrado = buscarUdp() ?: buscarPorRedLocal()
        if (encontrado == null) {
            throw IOException("No se encontró el servidor Kaiju en la red local")
        }
        servidorEncontrado = encontrado
        return encontrado
    }

    @Synchronized
    fun olvidarDireccion(direccion: String) {
        if (servidorEncontrado == direccion) {
            servidorEncontrado = null
        }
    }

    private fun obtenerRedes(): List<RedLocal> {
        val redes = mutableListOf<RedLocal>()
        val interfaces = NetworkInterface.getNetworkInterfaces() ?: return redes
        while (interfaces.hasMoreElements()) {
            val interfaz = interfaces.nextElement()
            if (!interfaz.isUp || interfaz.isLoopback) continue
            val nombre = interfaz.name.lowercase()
            if (nombre.startsWith("rmnet") || nombre.startsWith("ccmni") ||
                nombre.startsWith("pdp") || nombre.startsWith("tun") ||
                nombre.startsWith("v4-")) continue
            for (direccion in interfaz.interfaceAddresses) {
                val ipv4 = direccion.address as? Inet4Address ?: continue
                if (!ipv4.isSiteLocalAddress) continue
                redes.add(RedLocal(ipv4, direccion.broadcast))
            }
        }
        return redes
    }

    private fun buscarUdp(): String? {
        for (red in obtenerRedes()) {
            try {
                DatagramSocket(null).use { socket ->
                    socket.reuseAddress = true
                    socket.bind(InetSocketAddress(red.direccion, 0))
                    socket.broadcast = true
                    socket.soTimeout = 800
                    val solicitud = "KAIJU_BUSCAR_SERVIDOR".toByteArray(StandardCharsets.UTF_8)
                    val destinos = listOfNotNull(red.difusion, InetAddress.getByName("255.255.255.255"))
                    var enviado = false
                    for (destino in destinos.distinct()) {
                        try {
                            socket.send(DatagramPacket(solicitud, solicitud.size, destino, 8989))
                            enviado = true
                        } catch (_: IOException) {
                        }
                    }
                    if (!enviado) return@use
                    val respuesta = ByteArray(64)
                    val paquete = DatagramPacket(respuesta, respuesta.size)
                    try {
                        socket.receive(paquete)
                        val mensaje = String(
                            paquete.data, paquete.offset, paquete.length, StandardCharsets.UTF_8
                        ).trim()
                        if (mensaje == "KAIJU_SERVIDOR") {
                            return paquete.address.hostAddress
                        }
                    } catch (_: SocketTimeoutException) {
                    }
                }
            } catch (_: IOException) {
            }
        }
        return null
    }

    private fun buscarPorRedLocal(): String? {
        val candidatos = mutableListOf<Candidato>()
        candidatos.add(Candidato("10.0.2.2", null))
        for (red in obtenerRedes()) {
            val partes = red.direccion.hostAddress.split(".")
            if (partes.size != 4) continue
            val prefijo = partes.take(3).joinToString(".")
            for (numero in 2..254) {
                val ip = "$prefijo.$numero"
                candidatos.add(Candidato(ip, red.direccion))
            }
            candidatos.add(Candidato("$prefijo.1", red.direccion))
        }
        if (candidatos.size == 1) return null
        val ejecutor = Executors.newFixedThreadPool(24)
        val resultados = ExecutorCompletionService<String?>(ejecutor)
        try {
            for (candidato in candidatos) {
                resultados.submit(java.util.concurrent.Callable {
                    if (esServidorKaiju(candidato)) candidato.ip else null
                })
            }
            val final = System.nanoTime() + TimeUnit.SECONDS.toNanos(8)
            repeat(candidatos.size) {
                val restante = final - System.nanoTime()
                if (restante <= 0) return null
                val resultado = resultados.poll(restante, TimeUnit.NANOSECONDS) ?: return null
                val ip = resultado.get()
                if (ip != null) return ip
            }
        } catch (_: Exception) {
        } finally {
            ejecutor.shutdownNow()
        }
        return null
    }

    private fun esServidorKaiju(candidato: Candidato): Boolean {
        try {
            Socket().use { socket ->
                if (candidato.origen != null) {
                    socket.bind(InetSocketAddress(candidato.origen, 0))
                }
                socket.connect(InetSocketAddress(candidato.ip, 8081), 450)
                socket.soTimeout = 650
                val solicitud = "GET /api/identidad HTTP/1.1\r\n" +
                    "Host: ${candidato.ip}:8081\r\nConnection: close\r\n\r\n"
                socket.getOutputStream().write(solicitud.toByteArray(StandardCharsets.UTF_8))
                val datos = ByteArray(2048)
                val respuesta = StringBuilder()
                repeat(5) {
                    val leidos = socket.getInputStream().read(datos)
                    if (leidos <= 0) return@use
                    respuesta.append(String(datos, 0, leidos, StandardCharsets.UTF_8))
                    if (respuesta.contains("KAIJU_SERVIDOR") && respuesta.contains("200")) {
                        return true
                    }
                }
            }
        } catch (_: IOException) {
        }
        return false
    }
}
