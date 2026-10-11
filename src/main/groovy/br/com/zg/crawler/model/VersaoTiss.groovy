package br.com.zg.crawler.model

import groovy.transform.Canonical

@Canonical
class VersaoTiss {
    String competencia      // o nome/identificador da versão.
    String publicacao       // quando a ANS divulgou.
    String inicioVigencia   // quando a versão começa a valer
}