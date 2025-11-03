public class Mensaje {
    private final String tipo;
    private final int id;
    private final boolean esSpam;
    private int tiempoCuarentena = -1;

    private Mensaje(String tipo, int id, boolean esSpam) {
        this.tipo = tipo;
        this.id = id;
        this.esSpam = esSpam;
    }

    public static Mensaje inicio(int idCliente) {
        return new Mensaje("INICIO", idCliente, false);
    }

    public static Mensaje fin(int idCliente) {
        return new Mensaje("FIN", idCliente, false);
    }

    public static Mensaje normal(int idCliente, boolean spam) {
        return new Mensaje("NORMAL", idCliente, spam);
    }

    public int getTiempoCuarentena() {
        return tiempoCuarentena;
    }

    public void setTiempoCuarentena(int tiempo) {
        this.tiempoCuarentena = tiempo;
    }

    public boolean esSpam() {
        return esSpam;
    }

    public String getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "[Mensaje tipo=" + tipo + ", idCliente=" + id + ", spam=" + esSpam + ", cuarentena=" + tiempoCuarentena + "]";
    }
}
