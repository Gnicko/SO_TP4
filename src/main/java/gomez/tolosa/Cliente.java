package gomez.tolosa;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente implements Runnable {
    private static final int PUERTO = 9999;
    private static final String HOST = "localhost";

    private Ubicacion ubicacion;

    private int id;
    private Socket socket;
    public Cliente(int id, Ubicacion ubicacion){
        this.id=id;
        this.ubicacion=ubicacion;
    }
    public static void main(String[] args) {
        for(int i=0;i<6;i++){
            Ubicacion u=Ubicacion.values()[i%2];//alternar ubicacion de inicio
            new Thread(new Cliente(i,u)).start();
        }
    }
    @Override
    public void run() {
        try {

            while (true) {
                socket = new Socket(HOST, PUERTO);
                System.out.println("El barco " + id + " espera para pasar desde el " + ubicacion);
                enviarMensaje(
                        (ubicacion == Ubicacion.ESTE) ?
                                Acciones.SOLICITAR_DESDE_ESTE.toString() :
                                Acciones.SOLICITAR_DESDE_OESTE.toString());
                String mensajeRecivido;

                do {// un bucle por si el servidor responde con un mensaje distinto de "AVANZAR"
                    //para este ejemplo nunca se va a dar el caso de que envie un mensaje distinto de "AVANZAR", solo lo deje como validacion
                    mensajeRecivido = leerMensaje();

                } while (!mensajeRecivido.equals(Acciones.AVANZAR.toString()));
                System.out.println("El barco " + id + " cruza la primera esclusa desde el " + ubicacion);
                Thread.sleep(2000);
                enviarMensaje(Acciones.SOLICITAR_SEGUNTA_ESCLUSA.toString());

                do {
                    mensajeRecivido = leerMensaje();
                } while (!mensajeRecivido.equals(Acciones.AVANZAR.toString()));
                System.out.println("El barco " + id + " cruza la segunda esclusa desde el " + ubicacion);
                Thread.sleep(2000);

                enviarMensaje(Acciones.SALIR.toString());
                socket.close();
                ubicacion = Ubicacion.cambiarUbicacion(ubicacion);//cambia de sentido y intenta cruzar desde el otro extremo

            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private String leerMensaje() {
        try {
            DataInputStream mensajeEntrada = new DataInputStream(socket.getInputStream());
            return mensajeEntrada.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enviarMensaje(String mensaje) {
        try {
            DataOutputStream mensajeSalida = new DataOutputStream(socket.getOutputStream());
            mensajeSalida.writeUTF(mensaje);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
