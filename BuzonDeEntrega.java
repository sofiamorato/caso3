public class BuzonDeEntrega {
    private final Mensaje[] mensajes;
    private final int capacidad;
    private int extraerIndex = 0;
    private int envioIndex = 0;
    private int contador = 0;

    private final int servidoresTotales;
    private boolean finInsertado = false;

    public BuzonDeEntrega(int capacidad, int servidoresTotales) {
        this.capacidad = capacidad;
        this.mensajes = new Mensaje[capacidad];
        this.servidoresTotales = servidoresTotales;
    }

    public synchronized void enviarMensaje(Mensaje mensaje) {
        while (contador == capacidad) {
            try {
                System.out.println("[Buzon] Lleno. Servidor de Entrega con mensaje " + mensaje.getId() + " está esperando. Mensajes actuales: " + contador + "/" + capacidad);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if(envioIndex == capacidad) {
            envioIndex = 0;
        }

        mensajes[envioIndex] = mensaje;
        envioIndex++;
        contador++;
        System.out.println("[Buzon] El mensaje: " + mensaje.getId() + " ha sido enviado al Buzon de Entrega.");
        notifyAll();
    }


    public synchronized Mensaje extraerMensaje() {
        while (contador == 0) {
            System.out.println("[Buzon] Vacío. Servidor de Entrega espera mensaje...");
            Thread.yield();
        }

        Mensaje mensaje = mensajes[extraerIndex];
        extraerIndex++;
        if (extraerIndex == capacidad) {
            extraerIndex = 0;
        }
        contador--;
        System.out.println("[Buzon] El mensaje " + mensaje.getId() + " ha sido extraído por el Servidor de Entrega.");
        notifyAll();

        if (mensaje.getTipo().equals("FIN") && !finInsertado) {
            finInsertado = true;
            for (int i = 1; i < servidoresTotales; i++) {
                Mensaje email = Mensaje.fin(-1);
                enviarMensaje(email);
            }
        }

        return mensaje;
    }

    public synchronized boolean estaVacio() {
        return contador == 0;
    }
}
