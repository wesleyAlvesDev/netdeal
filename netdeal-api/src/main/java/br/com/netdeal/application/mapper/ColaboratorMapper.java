package br.com.netdeal.application.mapper;

import br.com.netdeal.application.dto.input.InsertColaborator;
import br.com.netdeal.application.dto.input.UpdateColaborator;
import br.com.netdeal.application.dto.output.ColaboratorDto;
import br.com.netdeal.domain.model.entity.Colaborator;

import java.util.List;

public interface ColaboratorMapper {

    ColaboratorDto toDto(Colaborator colaborator);

    List<ColaboratorDto> toDto(List<Colaborator> entities);

    Colaborator toEntity(ColaboratorDto colaborator);

    Colaborator toEntity(InsertColaborator colaborator);

    Colaborator toEntity(UpdateColaborator colaborator);
}
