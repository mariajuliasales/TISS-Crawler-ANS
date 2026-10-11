package br.com.zg.crawler.persistencia

import br.com.zg.crawler.model.VersaoTiss

// Grava a lista de versões num arquivo CSV
//   competencia;publicacao;inicio_vigencia
//   set/2026;30/09/2026;01/10/2026
//   jul/2026;31/07/2026;01/08/2026
class CsvWriter {

    static void salvar(File arquivo, List<VersaoTiss> versoes) {
        arquivo.parentFile.mkdirs()  

        String texto = 'competencia;publicacao;inicio_vigencia\n'
        for (VersaoTiss versao : versoes) {
            texto += "${versao.competencia};${versao.publicacao};${versao.inicioVigencia}\n"
        }

        arquivo.write(texto, 'UTF-8')   // UTF-8 mantem os acentos
    }
}
