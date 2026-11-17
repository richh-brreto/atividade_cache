package cacheatividade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private ConteudoCache conteudoCache;

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> estatisticasCache() {
        return ResponseEntity.ok(conteudoCache.obterEstatisticas());
    }

    @DeleteMapping
    public ResponseEntity<Void> limparCache() {
        conteudoCache.limpar();
        return ResponseEntity.noContent().build();
    }
}