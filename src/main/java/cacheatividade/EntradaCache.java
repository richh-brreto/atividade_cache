package cacheatividade;

import java.time.LocalDateTime;

public class EntradaCache {
    private Conteudo conteudo;
    private LocalDateTime armazenadoEm;
    private LocalDateTime ultimoAcesso;

    public EntradaCache(Conteudo conteudo) {
        this.conteudo = conteudo;
        this.armazenadoEm = LocalDateTime.now();
        this.ultimoAcesso = LocalDateTime.now();
    }

    public Conteudo getConteudo() { return conteudo; }
    public LocalDateTime getArmazenadoEm() { return armazenadoEm; }

    public LocalDateTime getUltimoAcesso() { return ultimoAcesso; }
    public void setUltimoAcesso(LocalDateTime ultimoAcesso) { this.ultimoAcesso = ultimoAcesso; }
}