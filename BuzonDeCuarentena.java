import java.util.ArrayList;

public class BuzonDeCuarentena {

    private final ArrayList<Mensaje> mensajes;

    public BuzonDeCuarentena() {
        this.mensajes = new ArrayList<>();
    }

    public synchronized void enviarMensaje(Mensaje mensaje) {
        int tiempo = (int) (10000 + Math.random() * 10000);
        mensaje.setTiempoCuarentena(tiempo);
        mensajes.add(mensaje);
        System.out.println("[Cuarentena] Mensaje SPAM recibido: " + mensaje.getId() + ", tiempo asignado: " + tiempo);
    }

    public synchronized ArrayList<Mensaje> revisionMensajes() {
        return new ArrayList<>(mensajes);
    }

    public synchronized void eliminarMensaje(Mensaje mensaje) {
        mensajes.remove(mensaje);
    }

    public synchronized boolean estaVacia() {
        return mensajes.isEmpty();
    }

}
