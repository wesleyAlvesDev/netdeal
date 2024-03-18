package br.com.netdeal.application.usecases;

import br.com.netdeal.application.dto.input.InsertColaborator;
import br.com.netdeal.application.dto.input.InsertHierarchyColaborator;
import br.com.netdeal.application.dto.input.UpdateColaborator;
import br.com.netdeal.application.dto.output.ColaboratorDto;

import java.util.List;

public interface ColaboratorUsecase {

    ColaboratorDto getOne(Long id);

    List<ColaboratorDto> getAll();

    List<ColaboratorDto> getLinkedList();

    ColaboratorDto create(InsertColaborator insertColaborator);

    ColaboratorDto createSubordinate(InsertHierarchyColaborator insertHierarchyColaborator);

    ColaboratorDto update(UpdateColaborator insertColaborator);

    void delete(Long id);

    int passwordStrengthPercentage(String password);
}
