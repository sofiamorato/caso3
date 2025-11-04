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
            // Verificación antes de intentar bloquearse esperando mensajes:
            if (fin.todosFinalizados() && buzon.estaVacio() && cuarentena.estaVacia() && !fin.finYaEnviado()) {
                if (fin.marcarFinGlobalEnviadoSiNoLoEsta()) {
                    entrega.enviarMensaje(Mensaje.fin(-1));
                    cuarentena.enviarMensaje(Mensaje.fin(-1));
                    System.out.println("[Filtro] >>> FIN GLOBAL enviado a Entrega y Cuarentena ");
                }
            }

            // Cierre de los filtros según enunciado: cuando se recibieron todos los FIN
            // y ya se entregó FIN a entrega y cuarentena (señalado por finYaEnviado)
            if (fin.finYaEnviado() && fin.todosFinalizados()) {
                System.out.println("[Filtro] Finalizando (todos FIN recibidos y FIN global entregado)");
                activo = false;
                break;
            }

            // Intento de extracción con timeout para no quedar bloqueado indefinidamente
            Mensaje mensaje = buzon.extraerMensajeConEspera(500);
            if (mensaje == null) {
                // No hubo mensaje en el intervalo; reintenta ciclo tras las verificaciones
                continue;
            }
            // Procesamiento del mensaje extraído
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
            // Verificación después de procesar el mensaje:
            if (fin.todosFinalizados() && buzon.estaVacio() && cuarentena.estaVacia() && !fin.finYaEnviado()) {
                if (fin.marcarFinGlobalEnviadoSiNoLoEsta()) {
                    entrega.enviarMensaje(Mensaje.fin(-1));
                    cuarentena.enviarMensaje(Mensaje.fin(-1));
                    System.out.println("[Filtro] >>> FIN GLOBAL enviado a Entrega y Cuarentena <<<");
                }
            }
            // Cierre de los filtros según enunciado: cuando se recibieron todos los FIN
            if (fin.finYaEnviado() && fin.todosFinalizados()) {
                System.out.println("[Filtro] Finalizando (post-proceso): FIN global entregado y todos FIN recibidos");
                activo = false;
            }
            // Pequeña pausa para evitar un bucle muy rápido
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
