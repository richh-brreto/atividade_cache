package cacheatividade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConteudoCache {
    private static final Logger logger = LoggerFactory.getLogger(ConteudoCache.class);

    ConcurrentHashMap<Long, EntradaCache> cache = new ConcurrentHashMap<>();

    @Value("${app.cache.max-itens:5}")
    private int maxItens;

    @Value("${app.cache.ttl-segundos:60}")
    private long ttlSegundos;

    private long hits = 0;
    private long misses = 0;
    private long emissoes = 0;

    public Conteudo buscar(Long id) {
        long inicio = System.currentTimeMillis();
        EntradaCache entrada = cache.get(id);

        if (entrada == null) {
            misses++;
            long tempo = System.currentTimeMillis() - inicio;
            logger.info("Cache MISS - id={} - {} ms", id, tempo);
            return null;
        }

        // se deu MISS
        long segundosDesdeArmazenamento = Duration.between(entrada.getArmazenadoEm(), LocalDateTime.now()).getSeconds();
        if (segundosDesdeArmazenamento > ttlSegundos) {
            cache.remove(id);
            misses++;
            long tempo = System.currentTimeMillis() - inicio;
            logger.info("cache MISS (expirado TTL) - id={} - {} ms", id, tempo);
            return null;
        }

        // se deu HIT
        entrada.setUltimoAcesso(LocalDateTime.now());
        hits++;
        long tempo = System.currentTimeMillis() - inicio;
        logger.info("Cache HIT  - id={} - {} ms", id, tempo);

        return entrada.getConteudo();
    }

    public void guardar(Long id, Conteudo conteudo) {
        // se ta no limite, tira o mais antigo
        if (cache.size() >= maxItens && !cache.containsKey(id)) {
            removerMaisAntigoLRU();
        }
        cache.put(id, new EntradaCache(conteudo));
    }

    private void removerMaisAntigoLRU() {
        Long chaveMaisAntiga = null;
        LocalDateTime menorAcesso = null;

        for (Map.Entry<Long, EntradaCache> entry : cache.entrySet()) {
            if (menorAcesso == null || entry.getValue().getUltimoAcesso().isBefore(menorAcesso)) {
                menorAcesso = entry.getValue().getUltimoAcesso();
                chaveMaisAntiga = entry.getKey();
            }
        }

        if (chaveMaisAntiga != null) {
            cache.remove(chaveMaisAntiga);
            emissoes++;
        }
    }

    public void invalidar(Long id) {
        cache.remove(id);
    }

    public void limpar() {
        cache.clear();
        hits = 0;
        misses = 0;
        emissoes = 0;
    }

    // retornar as estatisticas
    public Map<String, Object> obterEstatisticas() {
        long totalRequisicoes = hits + misses;
        double hitRatio = totalRequisicoes == 0 ? 0.0 : (double) hits / totalRequisicoes;

        return Map.of(
                "tamanhoAtual", cache.size(),
                "limiteMaximo", maxItens,
                "hits", hits,
                "misses", misses,
                "evictions", emissoes,
                "hitRatio", hitRatio
        );
    }
}

