package br.com.zg.crawler.persistencia

import br.com.zg.crawler.model.Interessado

// Os dados ficam no arquivo config/interessados.csv
//   Maria;maria@exemplo.com
//   João;joao@exemplo.com
class InteressadoRepository {

    File arquivo

    InteressadoRepository(File arquivo) {
        this.arquivo = arquivo
    }

    List<Interessado> listar() {
        List<Interessado> lista = []
        if (!arquivo.exists()) {
            return lista   //
        }
        for (String linha : arquivo.readLines('UTF-8')) {
            if (linha.trim().isEmpty()) {
                continue
            }
            String[] partes = linha.split(';')  
            lista << new Interessado(partes[0], partes[1])
        }
        return lista
    }

    Interessado buscar(String email) {
        for (Interessado pessoa : listar()) {
            if (pessoa.email == email) {
                return pessoa
            }
        }
        return null
    }

    void adicionar(String nome, String email) {
        if (!emailValido(email)) {
            throw new IllegalArgumentException("E-mail inválido: ${email}")
        }
        if (buscar(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado: ${email}")
        }
        List<Interessado> lista = listar()
        lista << new Interessado(nome, email)
        salvar(lista)
    }

    void atualizar(String emailAntigo, String novoNome, String novoEmail) {
        if (!emailValido(novoEmail)) {
            throw new IllegalArgumentException("E-mail inválido: ${novoEmail}")
        }
        List<Interessado> lista = listar()
        for (Interessado pessoa : lista) {
            if (pessoa.email == emailAntigo) {
                pessoa.nome = novoNome
                pessoa.email = novoEmail
            }
        }
        salvar(lista)
    }

    void remover(String email) {
        List<Interessado> lista = listar()
        lista.removeIf { pessoa -> pessoa.email == email }
        salvar(lista)
    }

    static boolean emailValido(String email) {
        return email ==~ /^[\w.+-]+@[\w-]+(\.[\w-]+)+$/   
    }

    private void salvar(List<Interessado> lista) {
        arquivo.parentFile.mkdirs()
        String texto = ''
        for (Interessado pessoa : lista) {
            texto += "${pessoa.nome};${pessoa.email}\n"
        }
        arquivo.write(texto, 'UTF-8')
    }
}
