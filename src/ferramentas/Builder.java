package ferramentas;

import dados.PixData;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Builder {
    
    CalculateCRC crc = new CalculateCRC();

    public String campo26DadosPix(String chavePix, boolean isTelefone) {
        String BC = FormatTLV.formatar("00", "BR.GOV.BCB.PIX");
        String key;
        if (isTelefone) {
            chavePix = formataTelefone(chavePix);
            key = FormatTLV.formatar("02", chavePix);
        } else {
            key = FormatTLV.formatar("01", chavePix);
        }
        return FormatTLV.formatar("26", BC + key);
    }

    private static String formataTelefone(String chavePix) {
        if (!chavePix.startsWith("+")) { //não tem o + ?
            if (chavePix.startsWith("55")) { //então ele tem ao menos o 55 ?
                chavePix = "+" + chavePix; //tem o 55 sem o +, adiciona o +
            }
            chavePix = "+55" + chavePix; // não tem nenhum ? adiciona o +55
        }
        return chavePix;
    }

    public String campo62IdDaTransacao(String idTransacao) {
        String subcampo05 = FormatTLV.formatar("05", idTransacao);
        return FormatTLV.formatar("62", subcampo05);
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String buildPayloadBase(PixData pix) {
        StringBuilder payload = new StringBuilder();

        payload.append(FormatTLV.formatar("00", "01"));
        payload.append(campo26DadosPix(pix.getChavePix(), pix.isTelefone()));
        payload.append(FormatTLV.formatar("52", "0000"));
        payload.append(FormatTLV.formatar("53", "986"));

        if (pix.getValor() != null) {
            payload.append(FormatTLV.formatar("54", formatAmount(pix.getValor())));
        }

        payload.append(FormatTLV.formatar("58", "BR"));
        payload.append(FormatTLV.formatar("59", pix.getNomeDoRecebedor()));
        payload.append(FormatTLV.formatar("60", pix.getCidadeDoRecebedor()));

        payload.append(campo62IdDaTransacao(pix.getIdDaTransação()));

        payload.append("6304");

        return payload.toString();
    }



}
