package br.com.zg.crawler

import br.com.zg.crawler.email.EmailService
import br.com.zg.crawler.http.HttpClient
import br.com.zg.crawler.model.Interessado
import br.com.zg.crawler.navegacao.Navegador
import br.com.zg.crawler.persistencia.InteressadoRepository
import br.com.zg.crawler.tasks.ComponentesTissTask
import br.com.zg.crawler.tasks.HistoricoVersoesTask
import br.com.zg.crawler.tasks.TabelaErrosTask

//   ./gradlew run                 -> roda as 3 tasks
//   ./gradlew run --args='menu'   -> abre o menu (rodar crawler + cadastro de e-mails)
class Main {

    static InteressadoRepository repositorio = new InteressadoRepository(new File('config/interessados.csv'))

    static void main(String[] args) {
        if (args.length > 0 && args[0] == 'menu') {
            menu()
        } else {
            rodarCrawler()
        }
    }

    static void rodarCrawler() {
        HttpClient http = new HttpClient()
        Navegador navegador = new Navegador(http)

        List<File> arquivos = []

        println '\n===== Task 1: Componente de Comunicação ====='
        try {
            arquivos << new ComponentesTissTask(http, navegador).executar()
        } catch (Exception e) {
            println "ERRO na Task 1: ${e.message}"
        }

        println '\n===== Task 2: Histórico das versões ====='
        try {
            arquivos << new HistoricoVersoesTask(http, navegador).executar()
        } catch (Exception e) {
            println "ERRO na Task 2: ${e.message}"
        }

        println '\n===== Task 3: Tabela de erros ====='
        try {
            arquivos << new TabelaErrosTask(http, navegador).executar()
        } catch (Exception e) {
            println "ERRO na Task 3: ${e.message}"
        }

        println "\nFim! ${arquivos.size()} de 3 tasks deram certo. Arquivos:"
        for (File arquivo : arquivos) {
            println "  ${arquivo.path}"
        }

        println '\n===== E-mail ====='
        try {
            List<String> emails = repositorio.listar().collect { pessoa -> pessoa.email }
            new EmailService().enviar(emails, arquivos)
        } catch (Exception e) {
            println "ERRO ao enviar e-mail: ${e.message}"
        }
    }

    static void menu() {
        Scanner teclado = new Scanner(System.in)   

        while (true) {
            println '\n1 - Rodar crawler'
            println '2 - Listar interessados'
            println '3 - Adicionar interessado'
            println '4 - Editar interessado'
            println '5 - Remover interessado'
            println '0 - Sair'
            print 'Escolha: '
            String opcao = teclado.nextLine()

            try {
                if (opcao == '1') {
                    rodarCrawler()
                } else if (opcao == '2') {
                    for (Interessado pessoa : repositorio.listar()) {
                        println "${pessoa.nome} - ${pessoa.email}"
                    }
                } else if (opcao == '3') {
                    print 'Nome: '
                    String nome = teclado.nextLine()
                    print 'E-mail: '
                    String email = teclado.nextLine()
                    repositorio.adicionar(nome, email)
                    println 'Adicionado!'
                } else if (opcao == '4') {
                    print 'E-mail atual: '
                    String emailAntigo = teclado.nextLine()
                    print 'Novo nome: '
                    String nome = teclado.nextLine()
                    print 'Novo e-mail: '
                    String email = teclado.nextLine()
                    repositorio.atualizar(emailAntigo, nome, email)
                    println 'Atualizado!'
                } else if (opcao == '5') {
                    print 'E-mail para remover: '
                    repositorio.remover(teclado.nextLine())
                    println 'Removido!'
                } else if (opcao == '0') {
                    return  
                } else {
                    println 'Opção inválida'
                }
            } catch (IllegalArgumentException e) {
                println "Erro: ${e.message}"  
            }
        }
    }
}