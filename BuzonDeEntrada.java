public class BuzonDeEntrada {

    private final Mensaje[] listaMensajes;
    private final int capacidad;
    private int envioIndex = 0;
    private int extraerIndex = 0;
    private int contador = 0;

    public BuzonDeEntrada(int capacidad) {
        this.capacidad = capacidad;
        this.listaMensajes = new Mensaje[capacidad];
    }


    public synchronized void enviarMensaje(Mensaje mensaje) {
        while (contador == capacidad) {
            try {
                System.out.println("[Buzon] Lleno. Cliente con mensaje " + mensaje.getId() + " está esperando. Mensajes actuales: " + contador + "/" + capacidad);
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (envioIndex == capacidad) {
            envioIndex = 0;
        }

        listaMensajes[envioIndex] = mensaje;
        envioIndex++;
        contador++;

        System.out.println("[Buzon] El mensaje: " + mensaje.getId() + " ha sido enviado al Buzon de Entrada.");
        notifyAll();
    }

    public synchronized Mensaje extraerMensaje() {
    while (contador == 0) {
        try {
            System.out.println("[Buzon] Vacío. Filtro espera mensaje...");
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    Mensaje mensaje = listaMensajes[extraerIndex];
    extraerIndex++;
    if (extraerIndex == capacidad) {
        extraerIndex = 0;
    }
    contador--;

    System.out.println("[Buzon] El mensaje " + mensaje.getId() + " ha sido extraído por el FiltroDeSpam.");
    notifyAll();

    return mensaje;
}


    public synchronized boolean estaVacio() {
        return contador == 0;
    }


        
    
}
