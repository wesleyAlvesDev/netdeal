package br.com.netdeal.domain.mapper;

import br.com.netdeal.application.dto.input.InsertColaborator;
import br.com.netdeal.application.dto.input.UpdateColaborator;
import br.com.netdeal.application.dto.output.ColaboratorDto;
import br.com.netdeal.application.mapper.ColaboratorMapper;
import br.com.netdeal.domain.model.entity.Colaborator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ColaboratorMapperImpl implements ColaboratorMapper {

    @Override
    public ColaboratorDto toDto(Colaborator colaborator) {
        List<ColaboratorDto> subordinates = new ArrayList<>();

        if (colaborator.getSubordinates() != null && !colaborator.getSubordinates().isEmpty()) {
            subordinates = colaborator.getSubordinates().stream().map(this::toDto).collect(Collectors.toList());
        }

        return new ColaboratorDto(
                colaborator.getId(),
                colaborator.getFullName(),
                colaborator.getPassword(),
                colaborator.getScrorePassword(),
                subordinates);
    }

    @Override
    public List<ColaboratorDto> toDto(List<Colaborator> entities) {
        if (entities == null) {
            return null;
        }

        List<ColaboratorDto> list = new ArrayList<>(entities.size());

        for (Colaborator colaborator: entities) {
            list.add(toDto(colaborator));
        }

        return list;
    }

    @Override
    public Colaborator toEntity(ColaboratorDto colaborator) {
        List<Colaborator> subordinates = new ArrayList<>();

        if (subordinates != null && !colaborator.getSubordinates().isEmpty()) {
            subordinates = colaborator.getSubordinates().stream().map(this::toEntity).collect(Collectors.toList());
        }

        return new Colaborator(
                null,
                colaborator.getFullName(),
                colaborator.getPassword(),
                colaborator.getScrorePassword(),
                subordinates,
                null);
    }

    @Override
    public Colaborator toEntity(InsertColaborator colaborator) {
        List<Colaborator> subordinates = new ArrayList<>();

        if (colaborator.getSubordinates() != null && !colaborator.getSubordinates().isEmpty()) {
            subordinates = colaborator.getSubordinates().stream().map(this::toEntity).collect(Collectors.toList());
        }

        return new Colaborator(
                null,
                colaborator.getFullName(),
                colaborator.getPassword(),
                null,
                subordinates,
                null);
    }

    @Override
    public Colaborator toEntity(UpdateColaborator colaborator) {
        List<Colaborator> subordinates = new ArrayList<>();

        if (colaborator.getSubordinates() != null && !colaborator.getSubordinates().isEmpty()) {
            subordinates = colaborator.getSubordinates().stream().map(this::toEntity).collect(Collectors.toList());
        }

        return new Colaborator(
                colaborator.getId(),
                colaborator.getFullName(),
                colaborator.getPassword(),
                colaborator.getScorePassword(),
                subordinates,
                null);
    }
}
