public class ClientesEmisores extends Thread {

    private BuzonDeEntrada buzon;
    private int idCliente;
    private int cantidadMensajes;

    public ClientesEmisores(BuzonDeEntrada buzon, int idCliente, int cantidadMensajes) {
        this.buzon = buzon;
        this.idCliente = idCliente;
        this.cantidadMensajes = cantidadMensajes;
    }

    @Override
    public void run() {
        Mensaje inicio = Mensaje.inicio(idCliente);
        buzon.enviarMensaje(inicio);
        System.out.println("Cliente " + idCliente + " envio mensaje de inicio al Buzon de Entrada: ");


        for (int i = 0; i < cantidadMensajes; i++) {
            boolean spam = Math.random() < 0.3;
            Mensaje normal = Mensaje.normal(idCliente, spam);
            buzon.enviarMensaje(normal);
            System.out.println("Cliente " + idCliente + " envio mensaje al Buzon de Entrada: ");
        }

        Mensaje fin = Mensaje.fin(idCliente);
        buzon.enviarMensaje(fin);
        System.out.println("Cliente " + idCliente + " envio mensaje de fin al Buzon de Entrada: ");
        
    }
    
}


