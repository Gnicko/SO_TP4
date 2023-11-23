package gomez.tolosa;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Servidor {
    private final int PUERTO = 9999;
    private Queue<Socket> barcosEste, barcosOeste;

    public static void main(String[] args) {
      Servidor s=new Servidor();
      s.iniciar();
    }

    public Servidor() {
        barcosEste = new LinkedList<>();
        barcosOeste = new LinkedList<>();
        System.out.println("Iniciando servidor...");
        iniciar();
    }

    private void iniciar() {

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            while (true) {
                Socket socket = serverSocket.accept();
                String mensaje = leerMensaje(socket);
                System.out.println("Llega un barco para  "+mensaje);
                if (mensaje.equals(Acciones.SOLICITAR_DESDE_ESTE.toString())) {
                    barcosEste.offer(socket);
                    avanzarBarcos(barcosEste);
                } else {
                    barcosOeste.offer(socket);
                    avanzarBarcos(barcosOeste);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void avanzarBarcos(Queue<Socket> colaBarcos) {
        if (colaBarcos.size() >= 2) {
            Socket sBarco1 = colaBarcos.poll();
            Socket sBarco2 = colaBarcos.poll();
            System.out.println("El Servidor permite que 2 barcos crucen la primera esclusa");

            enviarMensaje(sBarco1, Acciones.AVANZAR.toString());
            enviarMensaje(sBarco2, Acciones.AVANZAR.toString());

            String mensajeBarco1;
            String mensajeBarco2;
            do {

                /*utilizo un do while en vez de un if porque la idea es que llegue el mensaje que solicita la siguente esclusa,
                 no puede llegar (y para este caso nunca va a pasar)
                 y no puedo continuar la ejecucion si tengo 2 barcos entre las 2 esclusas, la idea es que si no envian la confirmacion correspondiente
                 volvera a esperar.
               */
                mensajeBarco1 = leerMensaje(sBarco1);
                mensajeBarco2 = leerMensaje(sBarco2);

            } while (!mensajeBarco1.equals(mensajeBarco2) ||
                    !mensajeBarco1.equals(Acciones.SOLICITAR_SEGUNTA_ESCLUSA.toString()));
            System.out.println("El Servidor permite que 2 barcos crucen la segunda esclusa");
            enviarMensaje(sBarco1, Acciones.AVANZAR.toString());
            enviarMensaje(sBarco2, Acciones.AVANZAR.toString());

            do {
                mensajeBarco1 = leerMensaje(sBarco1);
                mensajeBarco2 = leerMensaje(sBarco2);
            } while (!mensajeBarco1.equals(mensajeBarco2) || !mensajeBarco1.equals(Acciones.SALIR.toString()));
            System.out.println("Ya pasaron 2 barcos, continuar...");
            try {
                sBarco1.close();
                sBarco2.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    private String leerMensaje(Socket socket) {
        try {
            DataInputStream mensajeEntrada = new DataInputStream(socket.getInputStream());
            return mensajeEntrada.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enviarMensaje(Socket s, String mensaje) {
        try {
            DataOutputStream mensajeSalida = new DataOutputStream(s.getOutputStream());
            mensajeSalida.writeUTF(mensaje);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
