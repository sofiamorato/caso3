import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Verifica que el usuario haya pasado el nombre del archivo de configuración por consola
        if (args.length < 1) {
            System.err.println("Uso: java Main <archivo_config>");
            System.exit(1);
        }

        String archivoConfig = args[0];
        int numClientes = 0, mensajesPorCliente = 0, numFiltros = 0, numServidores = 0, capacidadEntrada = 0, capacidadEntrega = 0;
        
        // Lectura de configuración desde el archivo
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
        // Inicialización de buzones y componentes
        BuzonDeEntrada buzonEntrada = new BuzonDeEntrada(capacidadEntrada);
        BuzonDeEntrega buzonEntrega = new BuzonDeEntrega(capacidadEntrega, numServidores);
        BuzonDeCuarentena buzonCuarentena = new BuzonDeCuarentena();
        Fin fin = new Fin(numClientes);

        // Creación y arranque de hilos
        for (int i = 0; i < numClientes; i++) {
            new ClientesEmisores(buzonEntrada, i, mensajesPorCliente).start();
        }

        for (int i = 0; i < numFiltros; i++) {
            new FiltroDeSpam(buzonEntrada, buzonCuarentena, buzonEntrega, fin).start();
        }

        new ManejadorDeCuarentena(buzonCuarentena, buzonEntrega).start();

        for (int i = 0; i < numServidores; i++) {
            new ServidorDeEntrega(i, buzonEntrega).start();
        }
    }
}
