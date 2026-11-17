package cacheatividade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/conteudos")
public class ConteudoController {

    @Autowired
    private ConteudoService service;

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponseDto> buscarPorId(@PathVariable Long id) {
        ConteudoService.BuscaResultado resultado = service.buscarComStatusCache(id);
        String statusCache = resultado.doCache() ? "HIT" : "MISS";

        ConteudoResponseDto dto = ConteudoMapper.toDto(resultado.conteudo());

        return ResponseEntity.ok()
                .header("X-Cache", statusCache)
                .body(dto);
    }

    @PostMapping
    public ResponseEntity<ConteudoResponseDto> cadastrar(@RequestBody ConteudoRequestDto requestDto) {
        Conteudo conteudo = ConteudoMapper.toEntity(requestDto);
        Conteudo salvo = service.cadastrar(conteudo);
        ConteudoResponseDto dto = ConteudoMapper.toDto(salvo);

        return ResponseEntity.created(URI.create("/conteudos/" + dto.getId())).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConteudoResponseDto> atualizar(@PathVariable Long id, @RequestBody ConteudoRequestDto requestDto) {
        Conteudo conteudo = ConteudoMapper.toEntity(requestDto);
        Conteudo atualizado = service.atualizar(id, conteudo);
        ConteudoResponseDto dto = ConteudoMapper.toDto(atualizado);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}