# Capital Gain

Aplicação para cálculo de imposto sobre ganho de capital em operações financeiras.

## Decisões Técnicas e Arquiteturais

A solução foi desenvolvida em Java 17, seguindo os princípios de orientação a objetos e boas práticas de desenvolvimento.

### Estrutura do Projeto

O projeto está organizado nos seguintes pacotes:

- `io.github.akumosstl`: Contém a classe principal `App` que gerencia a entrada e saída (CLI e HTTP).
- `io.github.akumosstl.model`: Contém as classes de modelo (`Operation`, `TaxResult`) que representam os dados de entrada e saída.
- `io.github.akumosstl.service`: Contém a lógica de negócio (`CapitalGainService`) responsável pelo cálculo dos impostos.

### Decisões de Design

1.  **Separação de Responsabilidades**: A lógica de cálculo foi isolada na classe `CapitalGainService`, mantendo a classe `App` focada apenas na interface com o usuário (CLI ou HTTP). Isso facilita testes e manutenção.
2.  **Imutabilidade e Estado**: O serviço `CapitalGainService` é stateless no sentido de que não mantém estado entre chamadas diferentes do método `calculateTaxes`. O estado da simulação (quantidade atual, preço médio, prejuízo acumulado) é mantido apenas durante o processamento de uma lista de operações, garantindo que cada linha de entrada seja independente.
3.  **Uso de BigDecimal**: Para garantir precisão nos cálculos financeiros, foi utilizado `BigDecimal` em vez de `double` ou `float`.
4.  **Processamento Assíncrono**: Utilizou-se `CompletableFuture` para processar as entradas de forma assíncrona, conforme solicitado.
5.  **Servidor HTTP**: Foi utilizada a classe `com.sun.net.httpserver.HttpServer` do core do Java para implementar o servidor HTTP, evitando dependências externas pesadas para essa funcionalidade.

### Bibliotecas Utilizadas

-   **GSON (com.google.code.gson:gson:2.10.1)**: Utilizada para serialização e deserialização de JSON. Escolhida por ser leve, simples de usar e atender perfeitamente aos requisitos de parsing do projeto.
-   **JUnit 5 (org.junit.jupiter:junit-jupiter:5.10.0)**: Utilizada para testes unitários. É o padrão atual para testes em Java.

## Como Compilar e Executar

### Pré-requisitos

-   Java JDK 17 ou superior.
-   Maven 3.6 ou superior.

### Compilação

Na raiz do projeto, execute:

```bash
mvn clean package
```

Isso irá gerar o arquivo `capital_gain-1.0-SNAPSHOT.jar` na pasta `target`.

### Execução (Modo CLI) windows

Para executar a aplicação no modo de linha de comando (CLI), onde ela lê da entrada padrão (stdin):

```bash
java -jar target/capital_gain-1.0-SNAPSHOT.jar
```

Você pode digitar ou colar as linhas de JSON. Exemplo:

```json
[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
```

### Execução (Modo Servidor HTTP) windows

Para executar a aplicação no modo servidor HTTP:

```bash
java -jar target/capital_gain-1.0-SNAPSHOT.jar server
```

O servidor iniciará na porta 8080. Você pode enviar requisições POST para `http://localhost:8080/capital-gain`.

Exemplo com `curl`:

```bash
curl -X POST -d "[{ \"operation\" : \"buy\" , \"unit-cost\" :10.00, \"quantity\" : 10000}, { \"operation\" : \"sell\" , \"unit-cost\" :20.00, \"quantity\" : 5000}]" http://localhost:8080/capital-gain
```

## Como Executar os Testes

Para executar os testes unitários:

```bash
mvn test
```

## Docker

### Construindo a Imagem

Para construir a imagem Docker da aplicação, execute o seguinte comando na raiz do projeto:

```bash
docker build -t capital-gain-app .
```

### Rodando o Container

Após construir a imagem, você pode iniciar o container com o comando:

```bash
docker run -p 8080:8080 capital-gain-app
```

A aplicação estará rodando na porta 8080 do seu host.

## Notas Adicionais

-   A aplicação assume que o JSON de entrada está bem formatado, conforme descrito nos requisitos.
-   O arredondamento é feito utilizando `RoundingMode.HALF_UP` para duas casas decimais.
-   No modo CLI, as entradas são processadas assincronamente, mas a saída é impressa no console assim que o processamento termina.
