public class Fin {
    private int finRecibidos = 0;
    private final int totalClientes;
    private boolean finEntregado = false;

    private final BuzonDeEntrada buzon;
    private final BuzonDeCuarentena cuarentena;
    private final BuzonDeEntrega entrega;

    public Fin(int totalClientes,
                        BuzonDeEntrada buzon,
                        BuzonDeCuarentena cuarentena,
                        BuzonDeEntrega entrega) {
        this.totalClientes = totalClientes;
        this.buzon = buzon;
        this.cuarentena = cuarentena;
        this.entrega = entrega;
    }

    public synchronized void registrarFinCliente(Mensaje mensaje) {
        finRecibidos++;
        entrega.enviarMensaje(mensaje);
        System.out.println("[EstadoGlobal] FIN recibido: " + mensaje.getId() +
                " (" + finRecibidos + "/" + totalClientes + ")");

        if (finRecibidos == totalClientes && buzon.estaVacio() && cuarentena.estaVacia() && !finEntregado) {
            entrega.enviarMensaje(Mensaje.fin(-1));
            cuarentena.enviarMensaje(Mensaje.fin(-1));
            finEntregado = true;
            System.out.println("[EstadoGlobal] >>> FIN GLOBAL enviado a Entrega y Cuarentena <<<");
            notifyAll();
        }
    }

    public synchronized boolean debeFinalizarFiltro() {
        return finEntregado && buzon.estaVacio();
    }

}
