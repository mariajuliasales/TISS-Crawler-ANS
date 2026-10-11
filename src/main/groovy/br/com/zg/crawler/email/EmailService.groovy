package br.com.zg.crawler.email

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.MultiPartEmail

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//   export SMTP_HOST=smtp.gmail.com
//   export SMTP_PORT=587
//   export SMTP_USER=seu-email@gmail.com
//   export SMTP_PASSWORD=sua-senha-de-app
class EmailService {

    void enviar(List<String> destinatarios, List<File> arquivos) {
        String host = System.getenv('SMTP_HOST')
        if (host == null) {
            println 'E-mail não enviado: a variável SMTP_HOST não foi definida.'
            return
        }
        if (destinatarios.isEmpty()) {
            println 'E-mail não enviado: nenhum interessado cadastrado.'
            return
        }

        String usuario = System.getenv('SMTP_USER')
        String senha = System.getenv('SMTP_PASSWORD')

        MultiPartEmail email = new MultiPartEmail()
        email.setHostName(host)
        email.setSmtpPort((System.getenv('SMTP_PORT') ?: '587') as int)  
        email.setAuthenticator(new DefaultAuthenticator(usuario, senha))
        email.setStartTLSEnabled(true)   // conexão segura exigida pelo Gmail
        email.setCharset('UTF-8')
        email.setFrom(usuario)
        email.setSubject('Relatório do Crawler ANS - Padrão TISS')
        email.setMsg(montarRelatorio(arquivos))

        for (String destinatario : destinatarios) {
            email.addTo(destinatario)
        }

        for (File arquivo : arquivos) {
            EmailAttachment anexo = new EmailAttachment()
            anexo.setPath(arquivo.path)
            anexo.setName(arquivo.name)
            email.attach(anexo)
        }

        email.send()
        println "E-mail enviado para ${destinatarios.size()} pessoa(s)."
    }

    String montarRelatorio(List<File> arquivos) {
        String texto = "Relatório do Crawler ANS - Padrão TISS\n"
        String agora = LocalDateTime.now().format(DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm'))
        texto += "Executado em: ${agora}\n\n"
        texto += "Arquivos baixados:\n"
        for (File arquivo : arquivos) {
            texto += "- ${arquivo.name} (${(arquivo.length() / 1024) as int} KB)\n"
        }
        return texto
    }
}