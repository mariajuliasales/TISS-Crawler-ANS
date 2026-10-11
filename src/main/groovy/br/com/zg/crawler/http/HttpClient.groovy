package br.com.zg.crawler.http

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download
import org.jsoup.nodes.Document

class HttpClient {

    // Alguns servidores gov.br recusam requisições sem User-Agent
    static final String USER_AGENT = 'Mozilla/5.0 (crawler-academico-zg)'

    Document getHtml(String url) {
        println "Abrindo: ${url}"
        sleep(500)   

        HttpBuilder http = HttpBuilder.configure {
            request.uri = url
            request.headers['User-Agent'] = USER_AGENT
        }
        return http.get() as Document
    }

    File download(String url, File destino) {
        println "Baixando: ${url}"
        sleep(500)

        destino.parentFile.mkdirs()   

        HttpBuilder http = HttpBuilder.configure {
            request.uri = url
            request.headers['User-Agent'] = USER_AGENT
        }
        http.get {
            Download.toFile(delegate, destino) 
        }
        return destino
    }
}