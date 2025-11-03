public class ServidorDeEntrega extends Thread {
    private final int idServidor;
    private final BuzonDeEntrega buzon;
    private boolean iniciado = false;
    private boolean activo = true;

    public ServidorDeEntrega(int idServidor, BuzonDeEntrega buzon) {
        this.idServidor = idServidor;
        this.buzon = buzon;
    }

    @Override
    public void run() {
        System.out.println("[Servidor " + idServidor + "] Esperando mensaje de INICIO...");

        while (activo) {
            Mensaje mensaje = buzon.extraerMensaje();

            if (!iniciado) {
                if (mensaje.getTipo().equals("INICIO")) {
                    iniciado = true;
                    System.out.println("[Servidor " + idServidor + "] INICIO recibido. Comienza el procesamiento.");
                }
            } else {
                if (mensaje.getTipo().equals("FIN")) {
                    System.out.println("[Servidor " + idServidor + "] FIN recibido. Terminando ejecución.");
                    activo = false;
                } else {
                    if (mensaje.getTipo().equals("NORMAL") && !iniciado) {
                        System.out.println("[Servidor " + idServidor + "] Procesando mensaje: " + mensaje);
                        try {
                            int tiempo = (int) (500 + Math.random() * 1000);
                            Thread.sleep(tiempo);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            activo = false;
                        }
                    }
                }
            }
        }

        System.out.println("[Servidor " + idServidor + "] Finalizado.");
    }
}
