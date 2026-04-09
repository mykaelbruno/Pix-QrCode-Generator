public class FormatTLV {
    public static String formatar(String idCampo, String valor) {
        String size = String.format("%02d", valor.length());

        return idCampo + size + valor;
    }
}
