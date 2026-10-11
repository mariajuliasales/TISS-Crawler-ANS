package br.com.zg.crawler

import br.com.zg.crawler.navegacao.Navegador
import br.com.zg.crawler.tasks.ComponentesTissTask
import br.com.zg.crawler.tasks.HistoricoVersoesTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Specification

// Em vez de acessar a internet, usam cópias das páginas reais da ANS salvas em src/test/resources/html/.
class CrawlerSpec extends Specification {

    Document abrirPaginaSalva(String nomeArquivo) {
        File arquivo = new File("src/test/resources/html/${nomeArquivo}")
        return Jsoup.parse(arquivo, 'UTF-8', 'https://www.gov.br/ans/pt-br')
    }

    def "acha os links da página do TISS pelo texto"() {
        given:
        Navegador navegador = new Navegador(null)  
        Document paginaTiss = abrirPaginaSalva('tiss.html')

        expect:
        navegador.acharLink(paginaTiss, /acessar a vers[aã]o/).endsWith('padrao-tiss-setembro-2026')
        navegador.acharLink(paginaTiss, /todas as vers[oõ]es/).endsWith('historico-das-versoes-dos-componentes-do-padrao-tiss')
        navegador.acharLink(paginaTiss, /acessar as planilhas/).endsWith('padrao-tiss-tabelas-relacionadas')
    }

    def "acha o link da tabela de erros"() {
        given:
        Navegador navegador = new Navegador(null)

        when:
        String link = navegador.acharLink(abrirPaginaSalva('tabelas.html'), /tabela de erros no envio para a ANS/)

        then:
        link.endsWith('.xlsx')
    }

    def "dá erro quando o link não existe"() {
        when:
        new Navegador(null).acharLink(abrirPaginaSalva('tiss.html'), /link que nao existe/)

        then:
        thrown(RuntimeException)
    }

    def "acha o link do Componente de Comunicação na tabela da versão"() {
        given:
        ComponentesTissTask task = new ComponentesTissTask(null, null)

        when:
        String link = task.acharLinkComunicacao(abrirPaginaSalva('versao.html'))

        then:
        link.endsWith('PadroTISSComunicao_202511.zip')
    }

    def "pega o nome do arquivo do final do link"() {
        expect:
        ComponentesTissTask.nomeDoArquivo('https://www.gov.br/ans/a/b/Tabela.xlsx') == 'Tabela.xlsx'
    }

    def "filtra competências a partir de jan/2016"() {
        expect:
        HistoricoVersoesTask.aPartirDe2016(competencia) == esperado

        where:
        competencia | esperado
        'jan/2016'  | true
        'Jan/2016'  | true
        'set/2026'  | true
        'dez/2015'  | false
        'Mai/2014'  | false
        'janeiro'   | false
    }

    def "lê a tabela do histórico e guarda só de jan/2016 em diante"() {
        given:
        HistoricoVersoesTask task = new HistoricoVersoesTask(null, null)

        when:
        def versoes = task.lerTabela(abrirPaginaSalva('historico.html'))

        then:
        versoes.first().competencia == 'set/2026'     // mais recente
        versoes.first().publicacao == '30/09/2026'
        versoes.first().inicioVigencia == '01/10/2026'
        versoes.last().competencia == 'Jan/2016'      // mais antiga
    }
}
