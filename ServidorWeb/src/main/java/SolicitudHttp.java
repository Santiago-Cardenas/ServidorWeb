package main.java;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public final class SolicitudHttp implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public SolicitudHttp(Socket socket) throws Exception {
        this.socket = socket;
    }

    public void run() {
        try {
            proceseSolicitud();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void proceseSolicitud() throws Exception {
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String linea = in.readLine();
        System.out.println(linea);

        StringTokenizer partesLinea = new StringTokenizer(linea);

        String metodo = partesLinea.nextToken();
        String nombreArchivo = partesLinea.nextToken();
        nombreArchivo = "." + nombreArchivo;

        while ((linea = in.readLine()) != null && !linea.isEmpty()) {
            System.out.println(linea);
        }

        String lineaDeEstado = null;
        String lineaHeaderResponse = null;

        String recurso = nombreArchivo.substring(2);
        InputStream inputStream = SolicitudHttp.class.getResourceAsStream("/" + recurso);

        System.out.println("Resource URL: " + SolicitudHttp.class.getResource("/" + recurso));
        System.out.println("Resource URL: " + SolicitudHttp.class.getResource("/" + recurso).getPath());

        if (inputStream != null) {
            lineaDeEstado = "HTTP/1.0 200 OK" + CRLF;
            lineaHeaderResponse = "Content-type: " + contentType(nombreArchivo) + CRLF;
            enviarString(lineaDeEstado, out);
            enviarString(lineaHeaderResponse, out);
            enviarString(CRLF, out);
            enviarBytes(inputStream, out);
            inputStream.close();
        }
        else {
            lineaDeEstado = "HTTP/1.0 404 Not Found" + CRLF;
            lineaHeaderResponse = "Content-type: text/html" + CRLF;
            enviarString(lineaDeEstado, out);
            enviarString(lineaHeaderResponse, out);
            enviarString(CRLF, out);
            InputStream errorStream = ClassLoader.getSystemResourceAsStream("404.html");
            if (errorStream != null) {
                enviarBytes(errorStream, out);
                errorStream.close();
            } else {
                enviarString("<html><body><h1>Error 404: File Not Found</h1></body></html>", out);
            }
        }

        out.flush();
        out.close();
        in.close();
        socket.close();
    }

    private static void enviarString(String line, OutputStream os) throws Exception {
        os.write(line.getBytes(StandardCharsets.UTF_8));
    }

    private static void enviarBytes(InputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
    
    private static String contentType(String nombreArchivo) {
        if (nombreArchivo.endsWith(".html")) {
            return "text/html";
        }
        if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (nombreArchivo.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
    
}

