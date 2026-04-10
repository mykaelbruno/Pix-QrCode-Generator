import dados.PixData;
import ferramentas.Builder;
import ferramentas.CalculateCRC;
import java.math.BigDecimal;

public class Cobrador {

    Builder builder = new Builder();
    CalculateCRC crc = new CalculateCRC();

    public PixData GerarCobranca(String chavePix,boolean istelefone, String nomeDoRecebedor, String cidadeDoRecebedor, String idDaTransação, BigDecimal valor) {
        return new PixData(chavePix, istelefone, nomeDoRecebedor, cidadeDoRecebedor, idDaTransação, valor);
    }

    public String gerarPayload(PixData pix) {
        StringBuilder payload = new StringBuilder();
        payload.append(builder.buildPayloadBase(pix));
        payload.append(crc.calcular(payload.toString()));

        return payload.toString();
    }
}
