# Projeto BR Code - Iniciação de Pagamentos no SPB (Sistema de Pagamentos Brasileiro)

## Visão Geral

Este projeto foi desenvolvido como um **estudo de implementação** para gerar um código de pagamento (QR Code) utilizando o padrão **BR Code** para a iniciação de pagamentos via **PIX** no **Sistema de Pagamentos Brasileiro (SPB)**. O foco principal deste projeto é entender a lógica de **geração do payload** e **cálculo do CRC** necessário para criar um QR Code válido que será utilizado na plataforma PIX.

O projeto segue o padrão **EMV® QRCPS-MPM**, um padrão aberto e extensível, com foco na interoperabilidade e segurança, que foi adotado pelo **Banco Central do Brasil**.

PS.: O projeto trata apenas do backend, então ele gera a String dox pix (Estilo Copia e cola) e não a imagem do QR Code em si, que precisaria ser gerada por um front-end

### Objetivos do Projeto

* **Calcular o payload** com as informações do recebedor e da transação.
* **Calcular o CRC-16-CCITT-FFFF** para garantir a integridade do payload.
* **Gerar o QR Code** para a iniciação de pagamentos utilizando o **BR Code**.

---

## Arquitetura do Projeto

Este projeto é composto por várias classes que desempenham funções específicas para montar o payload e calcular o CRC. Abaixo estão as principais classes do projeto:

### 1. **Cobrador** (`Cobrador.java`)

A classe **Cobrador** tem a função principal de gerar a cobrança e calcular o payload, utilizando os dados fornecidos (como chave PIX, nome do recebedor, valor da transação, etc.). Ela invoca os métodos das outras classes para montar a string final de pagamento e calcular o CRC.

#### Principais métodos:

* **`GerarCobranca()`**: Gera os dados de cobrança, criando um objeto `PixData`.
* **`gerarPayload()`**: Monta o payload com base nos dados de cobrança e calcula o CRC utilizando a classe `CalculateCRC`.

### 2. **Main** (`Main.java`)

A classe **Main** inicia o processo de geração da cobrança. Ela cria um objeto **Cobrador**, define os parâmetros da transação (como chave PIX, nome do recebedor, valor, etc.), e exibe o QR Code gerado no formato de payload.

#### Principais componentes:

* Criação de um objeto **PixData**.
* Geração do payload chamando o método `gerarPayload()` da classe **Cobrador**.

### 3. **Builder** (`Builder.java`)

A classe **Builder** é responsável por criar os diferentes campos do payload no formato **TLV** (Type-Length-Value), que será utilizado para a codificação do QR Code. Ela monta campos como a chave PIX, a transação e os dados do recebedor.

#### Principais métodos:

* **`campo26DadosPix()`**: Formata o campo da chave PIX.
* **`campo26IdDaTransacao()`**: Formata o campo do ID da transação.
* **`buildPayloadBase()`**: Monta a base do payload com todos os dados necessários para a próxima etapa (Cáclulo do CRC verificador).

### 4. **CalculateCRC** (`CalculateCRC.java`)

Esta classe contém o algoritmo necessário para calcular o **CRC-16-CCITT-FFFF** sobre o payload gerado. Trata-se de um código hexadecimal ao final da string de payload que valida a quantidade de caracteres da string completa, para verificar a itegridade da mesma. <br>
A implementação segue o padrão do manual do BR Code, utilizando o polinômio `0x1021` e valor inicial `0xFFFF`. <br>
**Documentação detalhada em separado**

#### Principais métodos:

* **`calcular()`**: Calcula o CRC do payload, retornando o valor de 4 caracteres hexadecimais.

### 5. **FormatTLV** (`FormatTLV.java`)

A classe **FormatTLV** auxilia na formatação dos campos do payload de acordo com o padrão **TLV** (Type-Length-Value), garantindo que os campos sejam codificados corretamente para gerar o QR Code.

#### Principais métodos:

* **`formatar()`**: Formata uma string no formato TLV.

### 6. **PixData** (`PixData.java`)

A classe **PixData** representa os dados da transação, como a chave PIX, nome do recebedor, valor e cidade. Ela armazena as informações necessárias para a geração do payload.

#### Atributos:

* **`chavePix`**: Chave PIX do recebedor.
* **`isTelefone`**: Flag indicando se a chave é do tipo telefone.
* **`nomeDoRecebedor`**: Nome do recebedor.
* **`cidadeDoRecebedor`**: Cidade do recebedor.
* **`idDaTransação`**: Identificador único da transação.
* **`valor`**: Valor da transação.

---

## Passos para execução:

1. Clone ou baixe o repositório do projeto.
2. Compile o projeto utilizando `javac` ou seu IDE preferido.
3. Execute a classe `Main.java` para gerar o QR Code com o payload.

**Atenção:** Por ser um projeto mais focado na lógica do backend, o código não tem entrada para o usuário, então os dados da transação devem ser inseridas direto no Main. 

```bash
javac Main.java Cobrador.java Builder.java CalculateCRC.java FormatTLV.java PixData.java
java Main
```

### Exemplo de Saída:

O programa exibirá o payload final gerado, que será algo similar a:

```text
00020126380014BR.GOV.BCB.PIX0114teste@pix.com5204000053039865802BR5910LOJA TESTE6008JUAZEIRO62100506PED1236304AD38
```

---

## Funcionamento do Cálculo CRC

O cálculo do **CRC-16-CCITT-FFFF** é um processo crítico no BR Code. Ele garante a integridade do payload e valida se o código gerado está correto. O CRC é calculado após a montagem do payload, incluindo o campo `6304`, mas sem o valor final do CRC.

### Passos para cálculo do CRC:

1. Montar o payload base, incluindo o campo `6304` no final, mas sem o valor do CRC.
2. Aplicar o algoritmo CRC-16-CCITT-FFFF no payload base.
3. Adicionar o valor do CRC gerado ao final do payload.

A classe **CalculateCRC** implementa esse cálculo, utilizando o polinômio `0x1021` e valor inicial `0xFFFF`.


### **Nota sobre o Cálculo do CRC**  ###

A documentação detalhada sobre o cálculo do **CRC** está separada em outro documento (CalculoCRC.md), pois este processo exige explicações mais detalhadas sobre como ele garante a integridade do payload e os detalhes da implementação.

---
## Exemplo de Uso

Aqui está um exemplo básico de como o sistema funciona na classe `Main.java`:

```java
public class Main {
    public static void main(String[] args) {
        Cobrador cobrador = new Cobrador();

        String chavePix = "exemplo@gmail.com";
        String nomeDoRecebedor = "MARIA_JOSE";
        String cidadeDoRecebedor = "Mamanguape";
        String idDaTransacao = "BUY74";
        BigDecimal valor = new BigDecimal("1.00");

        PixData pix = cobrador.GerarCobranca(
                chavePix,
                nomeDoRecebedor,
                cidadeDoRecebedor,
                idDaTransacao,
                valor
        );

        String pixChave = cobrador.gerarPayload(pix);
        System.out.println(pixChave);
    }
}
```

---

## Conclusão

Este projeto serve como uma implementação completa para o cálculo do payload e CRC para transações de pagamento via **PIX** utilizando o **BR Code**. Ele segue o padrão **EMV® QRCPS-MPM** e pode ser usado como base para aplicações reais que envolvem o **Sistema de Pagamentos Brasileiro (SPB)**.
