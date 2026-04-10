import dados.PixData;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Cobrador cobrador = new Cobrador();

        String chavePix = "example@gmail.com";
        boolean isTelefone = false;
        String nomeDoRecebedor = "MARIA_JOSE";
        String cidadeDoRecebedor = "Cidade";
        String idDaTransacao = "COMRA_765";
        BigDecimal valor = new BigDecimal("15.50");

        PixData pix = cobrador.GerarCobranca(
                chavePix,
                isTelefone,
                nomeDoRecebedor,
                cidadeDoRecebedor,
                idDaTransacao,
                valor
        );

        String pixChave = cobrador.gerarPayload(pix);
        System.out.println(pixChave);
    }
}