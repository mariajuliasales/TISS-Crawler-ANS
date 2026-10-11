package br.com.zg.crawler

import br.com.zg.crawler.persistencia.InteressadoRepository
import spock.lang.Specification
import spock.lang.TempDir

class InteressadoRepositorySpec extends Specification {

    @TempDir
    File pasta  

    def "adiciona, edita e remove"() {
        given:
        InteressadoRepository repo = new InteressadoRepository(new File(pasta, 'interessados.csv'))

        when: 'adiciono duas pessoas'
        repo.adicionar('Maria', 'maria@exemplo.com')
        repo.adicionar('João', 'joao@exemplo.com.br')

        then:
        repo.listar().size() == 2
        repo.buscar('maria@exemplo.com').nome == 'Maria'

        when: 'edito a Maria'
        repo.atualizar('maria@exemplo.com', 'Maria Julia', 'mj@exemplo.com')

        then:
        repo.buscar('mj@exemplo.com').nome == 'Maria Julia'

        when: 'removo o João'
        repo.remover('joao@exemplo.com.br')

        then:
        repo.listar().size() == 1
    }

    def "valida o formato do e-mail"() {
        expect:
        InteressadoRepository.emailValido(email) == valido

        where:
        email               | valido
        'maria@exemplo.com' | true
        'a.b@x.com.br'      | true
        'sem-arroba.com'    | false
        'maria@'            | false
        'maria@dominio'     | false
    }

    def "não deixa cadastrar o mesmo e-mail duas vezes"() {
        given:
        InteressadoRepository repo = new InteressadoRepository(new File(pasta, 'interessados.csv'))
        repo.adicionar('Maria', 'maria@exemplo.com')

        when:
        repo.adicionar('Outra Maria', 'maria@exemplo.com')

        then:
        thrown(IllegalArgumentException)
    }
}