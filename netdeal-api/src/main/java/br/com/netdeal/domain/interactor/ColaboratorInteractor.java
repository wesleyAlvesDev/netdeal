package br.com.netdeal.domain.interactor;

import br.com.netdeal.application.dto.input.InsertColaborator;
import br.com.netdeal.application.dto.input.InsertHierarchyColaborator;
import br.com.netdeal.application.dto.input.UpdateColaborator;
import br.com.netdeal.application.dto.output.ColaboratorDto;
import br.com.netdeal.application.mapper.ColaboratorMapper;
import br.com.netdeal.application.usecases.ColaboratorUsecase;
import br.com.netdeal.domain.model.entity.Colaborator;
import br.com.netdeal.domain.repository.ColaboratorRepository;
import br.com.netdeal.domain.util.AES;
import br.com.netdeal.domain.util.PasswordValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ColaboratorInteractor implements ColaboratorUsecase {

    private static String PRIVATE_KEY;
    private final ColaboratorMapper colaboratorMapper;
    private final ColaboratorRepository colaboratorRepository;

    @Autowired
    public ColaboratorInteractor(
            @Value("${password.hashkey.encripta}") String privateKey,
            ColaboratorMapper colaboratorMapper,
            ColaboratorRepository colaboratorRepository) {
        this.PRIVATE_KEY = privateKey;
        this.colaboratorMapper = colaboratorMapper;
        this.colaboratorRepository = colaboratorRepository;
    }

    @Override
    public ColaboratorDto getOne(Long id) {
        Colaborator colaborator = colaboratorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("O Colaborador com o id %d não foi encontrado!", id)));

        return colaboratorMapper.toDto(colaborator);
    }

    @Override
    public List<ColaboratorDto> getAll() {
        return colaboratorRepository.findAll().stream()
                .map(colaboratorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ColaboratorDto> getLinkedList() {
        return colaboratorRepository.findAllByManagerIdIsNull().stream()
                .map(colaboratorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ColaboratorDto create(InsertColaborator insertColaborator) {
        Colaborator colaborator = colaboratorMapper.toEntity(insertColaborator);

        int percentage = passwordStrengthPercentage(colaborator.getPassword());
        colaborator.setScrorePassword(percentage);

        colaborator.setPassword(encripta(colaborator.getPassword()));

        return colaboratorMapper.toDto(colaboratorRepository.save(colaborator));
    }

    @Override
    public ColaboratorDto update(UpdateColaborator updateColaborator) {
        Colaborator colaborator = colaboratorMapper.toEntity(updateColaborator);

        Colaborator manager = findColaboratorById(colaborator.getId());

        int percentage = 0;
        String password = null;
        if (!manager.getPassword().equals(colaborator.getPassword())) {
            password = encripta(colaborator.getPassword());
            percentage = passwordStrengthPercentage(password);
        } else {
            password = decripta(colaborator.getPassword());
            percentage = passwordStrengthPercentage(password);
        }

        manager.setFullName(colaborator.getFullName());
        manager.setScrorePassword(percentage);
        manager.setPassword(password);

        return colaboratorMapper.toDto(colaboratorRepository.save(manager));
    }

    @Override
    public ColaboratorDto createSubordinate(InsertHierarchyColaborator colaborator) {
        Colaborator manager = findColaboratorById(colaborator.getManagerId());
        Colaborator subordinate = findColaboratorById(colaborator.getSubordinateId());

        manager.getSubordinates().add(subordinate);
        subordinate.setManager(manager);

        return colaboratorMapper.toDto(colaboratorRepository.save(manager));
    }

    @Override
    public void delete(Long id) {
        colaboratorRepository.findById(id).map(colaborator -> {
            colaboratorRepository.deleteById(id);
            return Void.TYPE;
        });
    }

    @Override
    public int passwordStrengthPercentage(String password) {
        return PasswordValidators.passwordStrengthPercentage(password);
    }

    private Colaborator findColaboratorById(Long id) {
        return colaboratorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("O Colaborador com o id %d não foi encontrado!", id)));
    }

    private String encripta(final String value) {
        final AES aes = new AES(UUID.fromString(PRIVATE_KEY));
        return aes.encripta(value);
    }

    private String decripta(final String value) {
        final AES aes = new AES(UUID.fromString(PRIVATE_KEY));
        return aes.decripta(value);
    }
}
