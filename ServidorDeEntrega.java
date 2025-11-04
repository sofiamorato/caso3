public class ServidorDeEntrega extends Thread {
    private final int idServidor;
    private final BuzonDeEntrega buzon;
    private boolean iniciado = false;
    private boolean activo = true;
    
    public ServidorDeEntrega(int idServidor, BuzonDeEntrega buzon) {
        this.idServidor = idServidor;
        this.buzon = buzon;
    }
    // Método run para procesar mensajes del buzón de entrega
    @Override
    public void run() {
        System.out.println("[Servidor " + idServidor + "] Esperando mensaje de INICIO...");
        // Bucle principal del servidor de entrega
        while (activo) {
            Mensaje mensaje = buzon.extraerMensaje();
            // Procesa el mensaje según su tipo
            // FIN debe terminar el servidor aunque aún no haya recibido INICIO
            if (mensaje.getTipo().equals("FIN")) {
                System.out.println("[Servidor " + idServidor + "] FIN recibido. Terminando ejecución.");
                activo = false;
                continue;
            }

            if (!iniciado) {
                if (mensaje.getTipo().equals("INICIO")) {
                    iniciado = true;
                    System.out.println("[Servidor " + idServidor + "] INICIO recibido. Comienza el procesamiento.");
                } else if (mensaje.getTipo().equals("NORMAL")) {
                    // Aún no iniciado: ignorar NORMAL, pero el mensaje ya fue extraído del buzón compartido.
                    System.out.println("[Servidor " + idServidor + "] Ignora mensaje NORMAL hasta recibir INICIO.");
                }
            } else {
                if (mensaje.getTipo().equals("NORMAL")) {
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
        System.out.println("[Servidor " + idServidor + "] Finalizado.");
    }
}