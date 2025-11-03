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

            switch (mensaje.getTipo()) {
                case "INICIO":
                    entrega.enviarMensaje(mensaje);
                    System.out.println("[Filtro] Mensaje INICIO " + mensaje.getId() + " → enviado al Servidor de Entrega");
                    break;

                case "NORMAL":
                    if (mensaje.esSpam()) {
                        cuarentena.enviarMensaje(mensaje);
                        System.out.println("[Filtro] SPAM detectado: " + mensaje.getId() + " → enviado a Cuarentena");
                    } else {
                        entrega.enviarMensaje(mensaje);
                        System.out.println("[Filtro] Mensaje limpio: " + mensaje.getId() + " → enviado a Entrega");
                    }
                    break;

                case "FIN":
                    fin.registrarFinCliente(mensaje);
                    break;
            }

            if (fin.todosFinalizados() && buzon.estaVacio() && cuarentena.estaVacia() && !fin.finYaEnviado()) {
                if (fin.marcarFinGlobalEnviadoSiNoLoEsta()) {
                    entrega.enviarMensaje(Mensaje.fin(-1));
                    cuarentena.enviarMensaje(Mensaje.fin(-1));
                    System.out.println("[Filtro] >>> FIN GLOBAL enviado a Entrega y Cuarentena <<<");
                }
            }

            if (fin.finYaEnviado() && buzon.estaVacio() && cuarentena.estaVacia()) {
                System.out.println("[Filtro] Condiciones de cierre cumplidas → Finalizando hilo");
                activo = false;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
