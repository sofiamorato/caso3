import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java Main <archivo_config>");
            System.exit(1);
        }

        String archivoConfig = args[0];
        int numClientes = 0, mensajesPorCliente = 0, numFiltros = 0, numServidores = 0, capacidadEntrada = 0, capacidadEntrega = 0;

        try (Scanner scanner = new Scanner(new File(archivoConfig))) {
            numClientes = scanner.nextInt();
            mensajesPorCliente = scanner.nextInt();
            numFiltros = scanner.nextInt();
            numServidores = scanner.nextInt();
            capacidadEntrada = scanner.nextInt();
            capacidadEntrega = scanner.nextInt();
        } catch (FileNotFoundException e) {
            System.err.println("Archivo de configuración no encontrado.");
            System.exit(1);
        }

        BuzonDeEntrada buzonEntrada = new BuzonDeEntrada(capacidadEntrada);
        BuzonDeEntrega buzonEntrega = new BuzonDeEntrega(capacidadEntrega, numServidores);
        BuzonDeCuarentena buzonCuarentena = new BuzonDeCuarentena();
        Fin fin = new Fin(numClientes, buzonEntrada, buzonCuarentena, buzonEntrega);

        for (int i = 0; i < numClientes; i++) {
            new Thread(new ClientesEmisores(buzonEntrada, mensajesPorCliente, i)).start();
        }

        for (int i = 0; i < numFiltros; i++) {
            new Thread(new FiltroDeSpam(buzonEntrada, buzonCuarentena, buzonEntrega, fin)).start();
        }

        new Thread(new ManejadorDeCuarentena(buzonCuarentena, buzonEntrega)).start();

        for (int i = 0; i < numServidores; i++) {
            new Thread(new ServidorDeEntrega(i, buzonEntrega)).start();
        }
    }
}
