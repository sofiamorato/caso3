public class Fin {
    private int finRecibidos = 0;
    private final int totalClientes;
    private boolean finEntregado = false;

    public Fin(int totalClientes) {
        this.totalClientes = totalClientes;
    }

    public synchronized void registrarFinCliente(Mensaje mensaje) {
        finRecibidos++;
        System.out.println("[Fin] FIN recibido: " + mensaje.getId() +
                " (" + finRecibidos + "/" + totalClientes + ")");
    }

    public synchronized boolean todosFinalizados() {
        return finRecibidos >= totalClientes;
    }

    public synchronized boolean finYaEnviado() {
        return finEntregado;
    }

    public synchronized boolean marcarFinGlobalEnviadoSiNoLoEsta() {
        if (!finEntregado) {
            finEntregado = true;
            return true;
        }
        return false;
    }
}
