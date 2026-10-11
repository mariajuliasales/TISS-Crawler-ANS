package br.com.zg.crawler.tasks

import br.com.zg.crawler.http.HttpClient
import br.com.zg.crawler.model.VersaoTiss
import br.com.zg.crawler.navegacao.Navegador
import br.com.zg.crawler.persistencia.CsvWriter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

// TASK 2: ler a tabela "Histórico das versões dos Componentes do Padrão TISS"
// e salvar Competência, Publicação e Início de Vigência (>= jan/2016) num CSV.
class HistoricoVersoesTask {

    static final String ARQUIVO_CSV = 'Downloads/Historico_TISS/historico_versoes.csv'

    HttpClient http
    Navegador navegador

    HistoricoVersoesTask(HttpClient http, Navegador navegador) {
        this.http = http
        this.navegador = navegador
    }

    File executar() {
        Document paginaTiss = navegador.irParaPaginaTiss()


        String linkHistorico = navegador.acharLink(paginaTiss, /todas as vers[oõ]es/)
        Document paginaHistorico = http.getHtml(linkHistorico)

        List<VersaoTiss> versoes = lerTabela(paginaHistorico)

        File arquivo = new File(ARQUIVO_CSV)
        CsvWriter.salvar(arquivo, versoes)
        println "${versoes.size()} versões salvas em ${arquivo.path}"
        return arquivo
    }

    //   | Competência | Publicação | Início de Vigência | Limite de Implantação | ... |
    //   | set/2026    | 30/09/2026 | 01/10/2026         | 31/12/2026            | ... |
    List<VersaoTiss> lerTabela(Document paginaHistorico) {
        Elements linhas = paginaHistorico.select('table tr')

        List<String> cabecalho = linhas[0].select('th')*.text()   
        int colunaCompetencia = cabecalho.indexOf('Competência')
        int colunaPublicacao = cabecalho.indexOf('Publicação')
        int colunaVigencia = cabecalho.indexOf('Início de Vigência')

        List<VersaoTiss> versoes = []

        for (int i = 1; i < linhas.size(); i++) {
            Elements celulas = linhas[i].select('td')
            String competencia = celulas[colunaCompetencia].text()

            if (aPartirDe2016(competencia)) {
                versoes << new VersaoTiss(
                        competencia,
                        celulas[colunaPublicacao].text(),
                        celulas[colunaVigencia].text())
            }
        }
        return versoes
    }


    static boolean aPartirDe2016(String competencia) {
        def resultado = competencia.trim() =~ /(?i)^(jan|fev|mar|abr|mai|jun|jul|ago|set|out|nov|dez)\/(\d{4})$/

        if (!resultado.matches()) {
            println "Aviso: competência fora do padrão, ignorada: '${competencia}'"
            return false
        }

        int ano = resultado.group(2) as int   // grupo 2 é o ano
        return ano >= 2016
    }
}