package cacheatividade;

import java.time.LocalDateTime;

public class ConteudoMapper {

    public static Conteudo toEntity(ConteudoRequestDto dto) {
        Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(dto.getTitulo());
        conteudo.setCorpo(dto.getCorpo());
        conteudo.setAutor(dto.getAutor());
        conteudo.setAtualizadoEm(LocalDateTime.now());
        return conteudo;
    }

    public static ConteudoResponseDto toDto(Conteudo conteudo) {
        ConteudoResponseDto dto = new ConteudoResponseDto();
        dto.setId(conteudo.getId());
        dto.setTitulo(conteudo.getTitulo());
        dto.setCorpo(conteudo.getCorpo());
        dto.setAutor(conteudo.getAutor());
        dto.setAtualizadoEm(conteudo.getAtualizadoEm());
        return dto;
    }
}
