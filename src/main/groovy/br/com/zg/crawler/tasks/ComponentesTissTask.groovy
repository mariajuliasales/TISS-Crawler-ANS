package br.com.zg.crawler.tasks

import br.com.zg.crawler.http.HttpClient
import br.com.zg.crawler.navegacao.Navegador
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

// TASK 1: baixar o "Componente de Comunicação" da versão mais recente do Padrão TISS.
class ComponentesTissTask {

    static final String PASTA = 'Downloads/Arquivos_padrao_TISS'

    HttpClient http
    Navegador navegador

    ComponentesTissTask(HttpClient http, Navegador navegador) {
        this.http = http
        this.navegador = navegador
    }

    File executar() {
        Document paginaTiss = navegador.irParaPaginaTiss()

        String linkVersao = navegador.acharLink(paginaTiss, /acessar a vers[aã]o/)
        Document paginaVersao = http.getHtml(linkVersao)

        String linkArquivo = acharLinkComunicacao(paginaVersao)

        File destino = new File(PASTA, nomeDoArquivo(linkArquivo))
        return http.download(linkArquivo, destino)
    }

    //   | Componente Organizacional  | 202609              | [Visualizar] |
    //   | Componente de Comunicação  | 04.03.00 e 01.06.00 | [Baixar]     |  
    String acharLinkComunicacao(Document paginaVersao) {
        // 'table tr' = todas as linhas (tr) de tabelas (table)
        for (Element linha : paginaVersao.select('table tr')) {
            Elements celulas = linha.select('td')   // células da linha
            if (celulas.size() == 0) {
                continue 
            }

            String nomeComponente = celulas[0].text()   // texto da 1ª coluna
            if (nomeComponente =~ /(?i)Componente de Comunica[cç][aã]o/) {
                Element link = linha.selectFirst('a')   // o link "Baixar" desta linha
                return link.absUrl('href')
            }
        }
        throw new RuntimeException('Não achei a linha "Componente de Comunicação" na tabela')
    }

    // "https://www.gov.br/.../PadroTISSComunicao_202511.zip"  ->  "PadroTISSComunicao_202511.zip"
    static String nomeDoArquivo(String url) {
        return (url =~ /[^\/]+$/)[0]
    }
}
