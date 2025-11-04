import java.util.ArrayList;

public class ManejadorDeCuarentena extends Thread {

    private final BuzonDeCuarentena cuarentena;
    private final BuzonDeEntrega entrega;
    private boolean activo = true;

    public ManejadorDeCuarentena(BuzonDeCuarentena cuarentena, BuzonDeEntrega entrega) {
        this.cuarentena = cuarentena;
        this.entrega = entrega;
    }
    // Método run para manejar los mensajes en cuarentena
    @Override
    public void run() {
        while (activo) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            ArrayList<Mensaje> mensajesRevisados = cuarentena.revisionMensajes();
            // Recorre los mensajes en cuarentena
            for (int i = 0; i < mensajesRevisados.size(); i++) {
                Mensaje mensaje = mensajesRevisados.get(i);

                if (mensaje.getTiempoCuarentena() > 0) {
                    int tiempo = mensaje.getTiempoCuarentena();
                    // Decremento en milisegundos, con tick de 1000ms
                    int nuevoTiempo = tiempo - 1000;
                    if (nuevoTiempo < 0) nuevoTiempo = 0;
                    mensaje.setTiempoCuarentena(nuevoTiempo);
                    System.out.println("[Cuarentena] Mensaje " + mensaje.getId() + " en cuarentena, tiempo restante: "
                            + mensaje.getTiempoCuarentena() + " ms");
                }
                // Si el tiempo de cuarentena llegó a 0, se elimina el mensaje
                else if (mensaje.getTiempoCuarentena() <= 0) {
                    cuarentena.eliminarMensaje(mensaje);
                    // Procesa el mensaje según su tipo
                    if (mensaje.getTipo().equals("FIN")) {
                        System.out.println("[Manejador] Mensaje FIN recibido. Terminando ejecución.");
                        activo = false;
                    } else {
                        int random = 1 + (int) (Math.random() * 21);
                        if (random % 7 != 0) {
                            entrega.enviarMensaje(mensaje);
                            System.out.println(
                                    "[Manejador] Mensaje " + mensaje.getId() + " reenviado al Buzón de Entrega.");
                        } else {
                            System.out.println("[Manejador] Mensaje " + mensaje.getId() + " descartado por malicioso.");

                        }
                    }
                }
            }
        }
    }
}