package br.com.netdeal.domain.interactor;

import br.com.netdeal.application.dto.input.InsertColaborator;
import br.com.netdeal.application.dto.input.InsertHierarchyColaborator;
import br.com.netdeal.application.dto.input.UpdateColaborator;
import br.com.netdeal.application.dto.output.ColaboratorDto;
import br.com.netdeal.application.mapper.ColaboratorMapper;
import br.com.netdeal.domain.model.entity.Colaborator;
import br.com.netdeal.domain.repository.ColaboratorRepository;
import br.com.netdeal.domain.util.AES;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ColaboratorInteractorUnitTest {

    @Mock
    private AES aes;
    @Mock
    private ColaboratorMapper colaboratorMapper;

    @Mock
    private ColaboratorRepository colaboratorRepository;

    @InjectMocks
    private ColaboratorInteractor colaboratorInteractor;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        Field field = ColaboratorInteractor.class.getDeclaredField("PRIVATE_KEY");
        field.setAccessible(true);
        field.set(null, "be0a6474-fc7f-4978-805d-f641619c136c");

        when(aes.encripta(Mockito.anyString())).thenReturn("decriptedPassword");
        when(aes.decripta(Mockito.anyString())).thenReturn("encriptedPassword");
    }

    @Test
    public void testGetOne_ValidId_ReturnsColaboratorDto() {
        Long id = 1L;
        Colaborator colaborator = new Colaborator();
        colaborator.setId(id);
        ColaboratorDto expectedDto = new ColaboratorDto();
        when(colaboratorRepository.findById(id)).thenReturn(Optional.of(colaborator));
        when(colaboratorMapper.toDto(colaborator)).thenReturn(expectedDto);

        ColaboratorDto result = colaboratorInteractor.getOne(id);

        assertEquals(expectedDto, result);
    }

    @Test
    public void testGetOne_InvalidId_ThrowsNotFoundException() {
        Long id = 1L;
        when(colaboratorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> colaboratorInteractor.getOne(id));
    }

    @Test
    public void testGetAll_ReturnsListOfColaboratorDto() {
        when(colaboratorRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(colaboratorInteractor.getAll().isEmpty());
    }

    @Test
    public void testGetLinkedList_ReturnsListOfColaboratorDto() {
        when(colaboratorRepository.findAllByManagerIdIsNull()).thenReturn(Collections.emptyList());

        assertTrue(colaboratorInteractor.getLinkedList().isEmpty());
    }

    @Test
    public void testCreate_ReturnsCreatedColaboratorDto() {
        InsertColaborator insertColaborator = new InsertColaborator();
        Colaborator colaborator = new Colaborator();

        colaborator.setPassword("StrongPassword123");
        ColaboratorDto expectedDto = new ColaboratorDto();
        when(colaboratorMapper.toEntity(insertColaborator)).thenReturn(colaborator);
        when(colaboratorRepository.save(colaborator)).thenReturn(colaborator);
        when(colaboratorMapper.toDto(colaborator)).thenReturn(expectedDto);

        ColaboratorDto result = colaboratorInteractor.create(insertColaborator);

        assertEquals(expectedDto, result);
    }

    @Test
    public void testUpdate_ReturnsUpdatedColaboratorDto() {
        UpdateColaborator updateColaborator = new UpdateColaborator();
        Colaborator colaborator = new Colaborator();
        colaborator.setPassword("98PH2umnfA6vsZKfir6f77HdUaekI9x4K8gU99vlcB8=");
        ColaboratorDto expectedDto = new ColaboratorDto();
        when(colaboratorMapper.toEntity(updateColaborator)).thenReturn(colaborator);
        when(colaboratorRepository.findById(colaborator.getId())).thenReturn(Optional.of(colaborator));
        when(colaboratorRepository.save(colaborator)).thenReturn(colaborator);
        when(colaboratorMapper.toDto(colaborator)).thenReturn(expectedDto);

        ColaboratorDto result = colaboratorInteractor.update(updateColaborator);

        assertEquals(expectedDto, result);
    }

    @Test
    public void testCreateSubordinate_ReturnsUpdatedManagerDto() {
        List<Colaborator> list = new ArrayList<Colaborator>();
        InsertHierarchyColaborator colaborator = new InsertHierarchyColaborator();
        Colaborator manager = new Colaborator();
        manager.setSubordinates(list);
        Colaborator subordinate = new Colaborator();

        subordinate.setSubordinates(list);
        ColaboratorDto expectedDto = new ColaboratorDto();
        when(colaboratorRepository.findById(colaborator.getManagerId())).thenReturn(Optional.of(manager));
        when(colaboratorRepository.findById(colaborator.getSubordinateId())).thenReturn(Optional.of(subordinate));
        when(colaboratorRepository.save(Mockito.any())).thenReturn(manager);
        when(colaboratorMapper.toDto(manager)).thenReturn(expectedDto);

        ColaboratorDto result = colaboratorInteractor.createSubordinate(colaborator);

        assertEquals(expectedDto, result);
    }

    @Test
    public void testDelete_ValidId_DeletesColaborator() {
        Long id = 1L;
        Colaborator colaborator = new Colaborator();
        colaborator.setId(id);
        when(colaboratorRepository.findById(id)).thenReturn(Optional.of(colaborator));

        colaboratorInteractor.delete(id);

        verify(colaboratorRepository, times(1)).deleteById(id);
    }

    @Test
    public void testPasswordStrengthPercentage_ReturnsPercentage() {
        String password = "StrongPassword123";
        int expectedPercentage = 93;

        int result = colaboratorInteractor.passwordStrengthPercentage(password);

        assertEquals(expectedPercentage, result);
    }
}