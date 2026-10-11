package br.com.zg.crawler.navegacao

import br.com.zg.crawler.http.HttpClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Navegador {

    static final String HOME_ANS = 'https://www.gov.br/ans/pt-br'

    HttpClient http

    Navegador(HttpClient http) {
        this.http = http
    }

    String acharLink(Document pagina, String regex) {
        for (Element link : pagina.select('a')) {
            if (link.text() =~ "(?i)${regex}") {
                return link.absUrl('href')
            }
        }
        throw new RuntimeException("Não achei nenhum link com o texto: ${regex}")
    }

    // Home da ANS  ->  "Espaço do Prestador de Serviços de Saúde"  ->  "TISS - Padrão para Troca de Informação..."
    Document irParaPaginaTiss() {
        Document home = http.getHtml(HOME_ANS)

        String linkPrestador = acharLink(home, /Espa[cç]o do Prestador/)
        Document paginaPrestador = http.getHtml(linkPrestador)

        String linkTiss = acharLink(paginaPrestador, /TISS - Padr[aã]o para Troca/)
        return http.getHtml(linkTiss)
    }
}
