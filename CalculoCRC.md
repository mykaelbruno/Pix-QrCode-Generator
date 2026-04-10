# Cálculo do CRC no BR Code / PIX

## Visão geral

No BR Code, o campo `63` representa o **CRC** do payload.
Esse campo é obrigatório, tem tamanho fixo `04`, e o valor final deve ser escrito em **4 caracteres hexadecimais**. O manual do BR Code diz que esse cálculo usa o polinômio hexadecimal `1021` e valor inicial `FFFF`, correspondente ao algoritmo **CRC-16-CCITT-FFFF**.

Exemplo mostrado no manual:

```text
6304AD38
```

Nesse exemplo:

* `63` = ID do campo CRC
* `04` = tamanho do valor do CRC
* `AD38` = resultado do cálculo do CRC

---

## O que é o CRC?

CRC significa **Cyclic Redundancy Check**.

Na prática, ele funciona como uma **assinatura de integridade** do payload:

* Você monta a string inteira do BR Code;
* Roda o algoritmo de CRC em cima dela;
* O resultado vira o valor do campo `63`

Quando outro sistema lê esse QR Code, ele pode recalcular o CRC e verificar se o conteúdo foi montado corretamente.

---

## Onde o CRC entra no payload?

O BR Code é uma sequência de campos no formato **TLV** ferramentas.FormatTLV (`ID + tamanho + valor`). O manual explica que o payload é uma sequência de objetos nesse formato e que eles podem inclusive ser aninhados.

O CRC é basicamente um campo que fica ao final do payload para verificar a integridade do mesmo.

```text
63 04 XXXX
```
- Campo **63**
- Tamanho **04**
- Valor **XXXX** <br>
 ^ Por isso a sigla TLV (TagDoCampo + Length + Value)

Só que tem um detalhe importante:

## regra principal

O CRC é calculado sobre o payload **já contendo**:

```text
6304
```

mas **sem** o valor final ainda.

Ou seja:

1. tu monta todo o payload
2. adiciona `6304` no final
3. calcula o CRC dessa string inteira
4. pega o resultado hexadecimal
5. concatena no final

---

## Exemplo conceitual

Supondo que o payload base fique assim:

*Repare no finalzinho*

```text
00020126380014BR.GOV.BCB.PIX0114teste@pix.com5204000053039865802BR5910LOJA TESTE6008JUAZEIRO62100506PED1236304
```

Esse payload ainda não está completo.
Ele termina com `6304`, mas ainda falta o valor real do CRC.

Depois do cálculo, imagina que o resultado seja:

```text
A1B2
```

Então o payload final vira:

```text
00020126380014BR.GOV.BCB.PIX0114teste@pix.com5204000053039865802BR5910LOJA TESTE6008JUAZEIRO62100506PED1236304A1B2
```

---

## Parâmetros oficiais do cálculo

Segundo o manual do BR Code:

* algoritmo: **CRC-16-CCITT-FFFF**
* polinômio: `0x1021`
* valor inicial: `0xFFFF`
* saída: **4 nibbles em hexadecimal**

O manual também mostra um exemplo explícito em que o campo `63` tem valor `0xAD38`, e no payload concatenado isso aparece como `6304AD38`.

---

## Lógica do algoritmo

O algoritmo funciona byte a byte.

### fluxo geral

1. começa com `crc = 0xFFFF`
2. percorre todos os bytes da string
3. mistura cada byte no valor atual do CRC
4. para cada byte, processa 8 bits
5. se o bit mais significativo estiver ligado, faz:

    * deslocamento para a esquerda
    * XOR com `0x1021`
6. se não estiver, só desloca
7. no final, mantém apenas 16 bits
8. converte o resultado para hexadecimal com 4 caracteres

---

## Implementação em Java

```java
public static String calculateCRC16(String payload) {
    int polynomial = 0x1021;
    int crc = 0xFFFF;

    byte[] bytes = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);

    for (byte b : bytes) {
        crc ^= (b & 0xFF) << 8;

        for (int i = 0; i < 8; i++) {
            if ((crc & 0x8000) != 0) {
                crc = (crc << 1) ^ polynomial;
            } else {
                crc = crc << 1;
            }

            crc &= 0xFFFF;
        }
    }

    return String.format("%04X", crc);
}
```

---

## Explicação linha por linha

### `int polynomial = 0x1021;`

Define o polinômio exigido pelo padrão.

### `int crc = 0xFFFF;`

Define o valor inicial do cálculo.

### `byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);`

Converte a string em bytes para o algoritmo processar.

### `crc ^= (b & 0xFF) << 8;`

Mistura o byte atual dentro do CRC.

* `b & 0xFF` evita problemas de sinal do tipo `byte` em Java
* `<< 8` desloca esse valor para a parte alta dos 16 bits

### `for (int i = 0; i < 8; i++)`

Cada byte tem 8 bits, então o algoritmo processa um bit por vez.

### `if ((crc & 0x8000) != 0)`

Verifica se o bit mais significativo dos 16 bits está ligado.

### `crc = (crc << 1) ^ polynomial;`

Se o bit mais alto estiver ligado, o algoritmo desloca e aplica XOR com o polinômio.

### `crc = crc << 1;`

Se não estiver ligado, só desloca.

### `crc &= 0xFFFF;`

Mantém o valor limitado a 16 bits.

### `return String.format("%04X", crc);`

Converte o resultado para hexadecimal maiúsculo com 4 caracteres.

Exemplos:

* `15` vira `000F`
* `44037` pode virar `AC05`

O manual dá exatamente esse tipo de representação como exemplo: `0xAC05 => "AC05"`.

---

## Como usar corretamente no builder

O ponto mais importante é a ordem.

### Passo 1: montar o payload sem o valor final do CRC

```java
String payloadBase = "000201...62100506PED1236304";
```

### Passo 2: calcular o CRC em cima desse payload

```java
String crc = calculateCRC16(payloadBase);
```

### Passo 3: concatenar o resultado

```java
String payloadFinal = payloadBase + crc;
```

---

## Exemplo completo

```java
public static void main(String[] args) {
    String payloadBase =
            "000201" +
            "26380014BR.GOV.BCB.PIX0114teste@pix.com" +
            "52040000" +
            "5303986" +
            "540525.90" +
            "5802BR" +
            "5910LOJA TESTE" +
            "6008JUAZEIRO" +
            "62100506PED123" +
            "6304";

    String crc = calculateCRC16(payloadBase);
    String payloadFinal = payloadBase + crc;

    System.out.println("CRC: " + crc);
    System.out.println("Cobrador final: " + payloadFinal);
}
```

---

## O que significa `6304`

Esse trecho costuma confundir.

### `63`

É o ID do campo CRC.

### `04`

É o tamanho do valor do CRC.

Então:

```text
6304
```

não é o CRC em si.
É só a abertura do campo dizendo:

> “o próximo valor será o CRC e ele terá 4 caracteres”.

Depois do cálculo, tu preenche esses 4 caracteres.

---

## Erros mais comuns (alguns aconteceram comigo)

## 1. Calcular o CRC sem adicionar `6304`

Errado:

```java
String crc = calculateCRC16(payloadSemCampo63);
```

Certo:

```java
String crc = calculateCRC16(payloadSemCampo63 + "6304");
```

---

## 2. Concatenar um valor inventado

O CRC não pode ser chutado nem fixo.

Ele depende do conteúdo completo do payload.
Se qualquer caractere mudar, o CRC muda também.

---

## 3. Usar formato decimal em vez de hexadecimal

O valor final precisa ser hexadecimal com 4 caracteres.

Errado:

```text
630417245
```

Certo:

```text
6304AD38
```

---

## 4. Retornar menos de 4 caracteres

Mesmo que o valor seja pequeno, ele precisa ser preenchido até 4 posições.

Exemplo:

* `F` -> `000F`
* `A5` -> `00A5`

---

## 5. Mudar o payload depois de calcular o CRC

Se tu calcular o CRC e depois alterar qualquer campo do payload, o resultado fica inválido.

A ordem correta é:

1. montar tudo
2. adicionar `6304`
3. calcular CRC
4. concatenar
5. não mexer mais na string

Se mexer, calcula de novo, claro, removendo o antigo CRC.

---

## Como validar mentalmente se está certo

Teu fluxo deve sempre ser este:

```text
monta payload sem CRC final
-> adiciona 6304
-> calcula CRC
-> concatena 4 hexadecimais
-> payload pronto
```

Se tu estiver fazendo isso, a lógica está correta.

---

## Resumo final

O cálculo do CRC no BR Code funciona assim:

* usa **CRC-16-CCITT-FFFF**
* polinômio `0x1021`
* valor inicial `0xFFFF`
* calcula sobre o payload já contendo `6304`
* o resultado final é hexadecimal com 4 caracteres
* esse valor é concatenado logo após `6304`

---