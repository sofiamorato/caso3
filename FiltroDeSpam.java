public class FiltroDeSpam extends Thread {
    private final BuzonDeEntrada buzon;
    private final BuzonDeCuarentena cuarentena;
    private final BuzonDeEntrega entrega;
    private final Fin fin;
    private boolean activo = true;

    public FiltroDeSpam(BuzonDeEntrada buzon, BuzonDeCuarentena cuarentena, BuzonDeEntrega entrega, Fin fin) {
        this.buzon = buzon;
        this.cuarentena = cuarentena;
        this.entrega = entrega;
        this.fin = fin;
    }

    @Override
    public void run() {
        while (activo) {
            Mensaje mensaje = buzon.extraerMensaje();

            if (mensaje.getTipo().equals("INICIO")) {
            entrega.enviarMensaje(mensaje);
            System.out.println("[Filtro] Mensaje de control (" + mensaje.getTipo() + "): " + mensaje.getId() + " → enviado al Servidor de Entrega");
        }
            else if (mensaje.getTipo().equals("NORMAL")) {
                if (mensaje.esSpam()) {
                    cuarentena.enviarMensaje(mensaje);
                    System.out.println("[Filtro] SPAM detectado: " + mensaje.getId() + " → enviado al Manejador de Cuarentena");
                } else {
                    entrega.enviarMensaje(mensaje);
                    System.out.println("[Filtro] Mensaje limpio: " + mensaje.getId() + " → enviado al Servidor de Entrega");
                }
            } else if (mensaje.getTipo().equals("FIN")) {
                fin.registrarFinCliente(mensaje);
                if (fin.debeFinalizarFiltro()) {
                    System.out.println("[Filtro] Condiciones de cierre cumplidas → Finalizando hilo");
                    activo = false;
                }
            }
        }
    }
}