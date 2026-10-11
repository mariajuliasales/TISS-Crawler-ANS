# Crawler ANS — Padrão TISS

Desenvolvido por Maria Julia Sales.

Programa em Groovy que entra no site da ANS, clica nos links como uma pessoa faria e baixa:

1. **Task 1:** o Componente de Comunicação da versão mais recente do Padrão TISS → `Downloads/Arquivos_padrao_TISS/`
2. **Task 2:** o histórico de versões (de jan/2016 em diante) em CSV → `Downloads/Historico_TISS/historico_versoes.csv`
3. **Task 3:** a Tabela de erros no envio para a ANS → `Downloads/Tabelas_relacionadas/`


## Como rodar

Precisa do JDK 21 instalado.

```bash
./gradlew run                  # roda as 3 tasks
./gradlew run --args='menu'    # menu: rodar crawler e cadastrar e-mails
./gradlew test                 # testes
```

## Envio do relatório por e-mail

Ao final do `./gradlew run`, o programa manda um e-mail para os interessados cadastrados. O e-mail leva:

- a data e a hora da execução;
- a lista dos arquivos baixados, com o tamanho de cada um;
- os arquivos em anexo.

Se algo não estiver configurado, o crawler funciona normalmente e só avisa que o e-mail não foi enviado.

### 1. Criar uma senha de app (Gmail)

O Gmail não aceita a senha normal da conta para programas. Para criar uma senha de app:

1. Ative a **verificação em duas etapas** em [myaccount.google.com](https://myaccount.google.com) → Segurança.
2. Acesse [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords), dê um nome e crie a senha.
3. Copie a senha de 16 letras.

### 2. Definir as variáveis de ambiente

As credenciais nunca ficam no código. Defina as variáveis no terminal:

```bash
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USER=seu-email@gmail.com
export SMTP_PASSWORD='suasenhadeapp'   # sem espaços
```


As variáveis valem só para aquela janela do terminal. Se você roda o projeto pelo IntelliJ, cadastre as variáveis em *Run → Edit Configurations → Environment variables*.

### 3. Cadastrar os destinatários

```bash
./gradlew run --args='menu'   # opção 3 para adicionar, 0 para sair
```

Os destinatários ficam em `config/interessados.csv`. O `.gitignore` deixa esse arquivo fora do Git, porque ele guarda e-mails pessoais.

### 4. Rodar

```bash
./gradlew run
```

Se der certo, aparece `E-mail enviado para N pessoa(s).` no fim.


## Arquivos

```
src/main/groovy/br/com/zg/crawler/
├── Main.groovy                       começa aqui: roda as tasks e o menu
├── http/HttpClient.groovy            abre páginas e baixa arquivos
├── navegacao/Navegador.groovy        acha links pelo regex
├── tasks/ComponentesTissTask.groovy  Task 1
├── tasks/HistoricoVersoesTask.groovy Task 2
├── tasks/TabelaErrosTask.groovy      Task 3
├── model/                            VersaoTiss e Interessado (só guardam dados)
├── persistencia/CsvWriter.groovy     grava o CSV da Task 2
├── persistencia/InteressadoRepository.groovy  cadastro de e-mails
└── email/EmailService.groovy         envio do e-mail
```