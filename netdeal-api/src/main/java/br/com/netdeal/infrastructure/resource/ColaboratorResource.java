package br.com.netdeal.infrastructure.resource;

import br.com.netdeal.application.dto.input.InsertColaborator;
import br.com.netdeal.application.dto.input.InsertHierarchyColaborator;
import br.com.netdeal.application.dto.input.UpdateColaborator;
import br.com.netdeal.application.dto.output.ColaboratorDto;
import br.com.netdeal.application.usecases.ColaboratorUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colaborator")
@CrossOrigin("*")
public class ColaboratorResource {

    private final ColaboratorUsecase colaboratorUsecase;

    public ColaboratorResource(ColaboratorUsecase colaboratorUsecase) {
        this.colaboratorUsecase = colaboratorUsecase;
    }

    @GetMapping
    private ResponseEntity<ColaboratorDto> getOne(@RequestParam("colaboratorId") Long id) {
        return ResponseEntity.ok(colaboratorUsecase.getOne(id));
    }

    @GetMapping("/all")
    private ResponseEntity<List<ColaboratorDto>> getAll() {
        return ResponseEntity.ok(colaboratorUsecase.getAll());
    }

    @GetMapping("/linked-list")
    private ResponseEntity<List<ColaboratorDto>> getLinkedList() {
        return ResponseEntity.ok(colaboratorUsecase.getLinkedList());
    }

    @PostMapping
    private ResponseEntity<ColaboratorDto> create(@RequestBody InsertColaborator colaborator) {
        return ResponseEntity.ok(colaboratorUsecase.create(colaborator));
    }

    @PostMapping("/create-subordinate")
    private ResponseEntity<ColaboratorDto> createSubordinate(@RequestBody InsertHierarchyColaborator colaborator) {
        return ResponseEntity.ok(colaboratorUsecase.createSubordinate(colaborator));
    }

    @PutMapping
    private ResponseEntity<ColaboratorDto> update(@RequestBody UpdateColaborator colaborator) {
        return ResponseEntity.ok(colaboratorUsecase.update(colaborator));
    }

    @DeleteMapping
    private ResponseEntity<ColaboratorDto> delete(@RequestParam("colaboratorId") Long id) {
        colaboratorUsecase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
