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
                System.out.println("[Buzon Entrada] Lleno. Cliente con mensaje " + mensaje.getId() + " está esperando. Mensajes actuales: " + contador + "/" + capacidad + mensaje);
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

        System.out.println("[Buzon Entrada] El mensaje: " + mensaje.getId() + " ha sido enviado al Buzon de Entrada." + mensaje + contador);
        notifyAll();
    }

    public synchronized Mensaje extraerMensaje() {
    while (contador == 0) {
        try {
            System.out.println("[Buzon Entrada] Vacío. Filtro espera mensaje...");
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

    System.out.println("[Buzon Entrada] El mensaje " + mensaje.getId() + " ha sido extraído por el FiltroDeSpam." + mensaje + contador);
    notifyAll();

    return mensaje;
}

//necesita revisar periódicamente si ya debe emitir el FIN global sin quedar bloqueado indefinidamente.
    // Intento de extracción con tiempo de espera; devuelve null si no hay mensajes tras el timeout.
    public synchronized Mensaje extraerMensajeConEspera(long millis) {
        if (contador == 0) {
            try {
                System.out.println("[Buzon Entrada] Vacío (espera con timeout). Filtro espera hasta " + millis + "ms...");
                wait(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (contador == 0) {
                return null;
            }
        }

        Mensaje mensaje = listaMensajes[extraerIndex];
        extraerIndex++;
        if (extraerIndex == capacidad) {
            extraerIndex = 0;
        }
        contador--;

        System.out.println("[Buzon Entrada] (timeout) El mensaje " + mensaje.getId() +
                " ha sido extraído por el FiltroDeSpam." + mensaje + contador);
        notifyAll();

        return mensaje;
    }


    public synchronized boolean estaVacio() {
        return contador == 0;
    }    
    
}
