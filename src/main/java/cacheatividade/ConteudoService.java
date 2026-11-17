package cacheatividade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConteudoService {

    public record BuscaResultado(Conteudo conteudo, boolean doCache) {}

    @Autowired
    private ConteudoRepository repository;

    @Autowired
    private ConteudoCache conteudoCache;

    public BuscaResultado buscarComStatusCache(Long id) {
        // busca no cache primeiro
        Conteudo conteudo = conteudoCache.buscar(id);

        if (conteudo != null) {
            return new BuscaResultado(conteudo, true);
        }

        // se não ta no cache, simula que ta lento e busca no banco
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        conteudo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conteúdo não encontrado"));

        // salva no cache
        conteudoCache.guardar(id, conteudo);

        return new BuscaResultado(conteudo, false);
    }

    public Conteudo buscarPorId(Long id) {
        return buscarComStatusCache(id).conteudo();
    }

    public Conteudo cadastrar(Conteudo conteudo) {
        return repository.save(conteudo);
    }

    public Conteudo atualizar(Long id, Conteudo novoConteudo) {
        conteudoCache.invalidar(id);
        novoConteudo.setId(id);
        return repository.save(novoConteudo);
    }

    public void excluir(Long id) {
        conteudoCache.invalidar(id);
        repository.deleteById(id);
    }
}