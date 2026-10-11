package br.com.zg.crawler.tasks

import br.com.zg.crawler.http.HttpClient
import br.com.zg.crawler.navegacao.Navegador
import org.jsoup.nodes.Document

// TASK 3: baixar a "Tabela de erros no envio para a ANS", que fica em "Tabelas relacionadas".
class TabelaErrosTask {

    static final String PASTA = 'Downloads/Tabelas_relacionadas'

    HttpClient http
    Navegador navegador

    TabelaErrosTask(HttpClient http, Navegador navegador) {
        this.http = http
        this.navegador = navegador
    }

    File executar() {
        Document paginaTiss = navegador.irParaPaginaTiss()

        String linkTabelas = navegador.acharLink(paginaTiss, /acessar as planilhas/)
        Document paginaTabelas = http.getHtml(linkTabelas)

        String linkArquivo = navegador.acharLink(paginaTabelas, /tabela de erros no envio para a ANS/)

        File destino = new File(PASTA, ComponentesTissTask.nomeDoArquivo(linkArquivo))
        return http.download(linkArquivo, destino)
    }
}
