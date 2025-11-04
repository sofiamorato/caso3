import java.util.ArrayList;

public class BuzonDeCuarentena {

    private final ArrayList<Mensaje> mensajes;

    public BuzonDeCuarentena() {
        this.mensajes = new ArrayList<>();
    }
    // Agrega un mensaje al buzón de cuarentena con un tiempo aleatorio asignado
    public synchronized void enviarMensaje(Mensaje mensaje) {
        int tiempo = (int) (Math.random() * 10);
        mensaje.setTiempoCuarentena(tiempo);
        mensajes.add(mensaje);
        System.out.println("[Cuarentena] Mensaje SPAM recibido: " + mensaje.getId() + ", tiempo asignado: " + tiempo);
    }
    // Revisa y devuelve una copia de los mensajes en cuarentena
    public synchronized ArrayList<Mensaje> revisionMensajes() {
        return new ArrayList<>(mensajes);
    }
    // Elimina un mensaje del buzón de cuarentena
    public synchronized void eliminarMensaje(Mensaje mensaje) {
        mensajes.remove(mensaje);
    }
    // Verifica si el buzón de cuarentena está vacío
    public synchronized boolean estaVacia() {
        return mensajes.isEmpty();
    }

}
