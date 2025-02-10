package main.java;

import java.net.*;

public final class ServidorWeb {
    public static void main(String argv[]) throws Exception {
        int puerto = 8080;

        try(ServerSocket servidor = new ServerSocket(puerto)){ 
            System.out.println("Servidor escuchando en el puerto " + puerto);
            while (true) {
                Socket socket = servidor.accept();
                SolicitudHttp solicitud = new SolicitudHttp(socket);
                Thread hilo = new Thread(solicitud);
                hilo.start();
            }
        }
    }
}
