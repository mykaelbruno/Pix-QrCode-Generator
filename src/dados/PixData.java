import java.math.BigDecimal;

public class PixData {
    private String chavePix;
    private String nomeDoRecebedor;
    private String cidadeDoRecebedor;
    private String idDaTransação;
    private BigDecimal valor;

    public PixData(String ChavePix, String nomeDoRecebedor, String cidadeDoRecebedor, String idDaTransação, BigDecimal valor) {
        this.chavePix = ChavePix;
        this.nomeDoRecebedor = nomeDoRecebedor;
        this.cidadeDoRecebedor = cidadeDoRecebedor;
        this.idDaTransação = idDaTransação;
        this.valor = valor;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public String getNomeDoRecebedor() {
        return nomeDoRecebedor;
    }

    public void setNomeDoRecebedor(String nomeDoRecebedor) {
        this.nomeDoRecebedor = nomeDoRecebedor;
    }

    public String getCidadeDoRecebedor() {
        return cidadeDoRecebedor;
    }

    public void setCidadeDoRecebedor(String cidadeDoRecebedor) {
        this.cidadeDoRecebedor = cidadeDoRecebedor;
    }

    public String getIdDaTransação() {
        return idDaTransação;
    }

    public void setIdDaTransação(String idDaTransação) {
        this.idDaTransação = idDaTransação;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
