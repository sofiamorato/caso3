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

            if (!enviado) {
                try {
                    Thread.sleep(500); // espera semiactiva
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public Mensaje extraerMensaje() {
        Mensaje mensaje = null;
        boolean extraido = false;

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

                    if (mensaje.getTipo().equals("FIN") && mensaje.getId() == -1 && !finInsertado) {
                        finInsertado = true;
                        for (int i = 1; i < servidoresTotales; i++) {
                            enviarMensaje(Mensaje.fin(-1));
                        }
                        System.out.println("[Buzon Entrega] FIN global duplicado para todos los servidores (" 
                                + servidoresTotales + ")");
                    }

                    extraido = true;
                } else {
                    System.out.println("[Buzon Entrega] Vacío. Servidor de Entrega espera mensaje...");
                }
            }

            if (!extraido) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return mensaje;
    }

    public synchronized boolean estaVacio() {
        return contador == 0;
    }
}
