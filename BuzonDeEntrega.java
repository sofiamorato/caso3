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
    // Método para enviar un mensaje al buzón de entrega
    public void enviarMensaje(Mensaje mensaje) {
        boolean enviado = false;
        while (!enviado) {
            synchronized (this) {
                if (contador < capacidad) {

                    if (envioIndex == capacidad) {
                        envioIndex = 0;
                    }

                    mensajes[envioIndex] = mensaje;
                    envioIndex++;
                    contador++;
                    System.out.println(
                            "[Buzon Entrega] El mensaje: " + mensaje.getId() + " ha sido enviado al Buzon de Entrega.");
                    notifyAll();
                    enviado = true;

                } else {
                    System.out.println("[Buzon Entrega] Lleno. Mensaje " + mensaje.getId() +
                            " espera. Ocupado: " + contador + "/" + capacidad);
                }
            }
            // Espera semiactiva si no se pudo enviar
            if (!enviado) {
                try {
                    Thread.sleep(500); // espera semiactiva
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    // Método para extraer un mensaje del buzón de entrega
    public Mensaje extraerMensaje() {
        Mensaje mensaje = null;
        boolean extraido = false;
        int duplicadosPendientes = 0; // duplicaciones de FIN a realizar fuera del bloqueo

        while (!extraido) {
            synchronized (this) {
                if (contador > 0) {
                    mensaje = mensajes[extraerIndex];
                    extraerIndex++;
                    if (extraerIndex == capacidad) {
                        extraerIndex = 0;
                    }
                    contador--;
                    System.out.println(
                            "[Buzon Entrega] El mensaje " + mensaje.getId()
                                    + " ha sido extraído por el Servidor de Entrega.");
                    notifyAll();
                    // Marcar duplicaciones pendientes de FIN global sin enviar dentro del bloqueo
                    if (mensaje.getTipo().equals("FIN") && mensaje.getId() == -1 && !finInsertado) {
                        finInsertado = true;
                        duplicadosPendientes = Math.max(servidoresTotales - 1, 0);
                    }

                    extraido = true;
                } else {
                    System.out.println("[Buzon Entrega] Vacío. Servidor de Entrega espera mensaje...");
                }
            }
            // Espera semiactiva si no se pudo extraer
            if (!extraido) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Realizar duplicaciones fuera de la sección crítica para evitar bloqueos reentrantes
        if (duplicadosPendientes > 0) {
            for (int i = 0; i < duplicadosPendientes; i++) {
                enviarMensaje(Mensaje.fin(-1));
            }
            System.out.println("[Buzon Entrega] FIN global duplicado para todos los servidores (" 
                    + servidoresTotales + ")");
        }

        return mensaje;
    }
    // Verifica si el buzón de entrega está vacío
    public synchronized boolean estaVacio() {
        return contador == 0;
    }
}
