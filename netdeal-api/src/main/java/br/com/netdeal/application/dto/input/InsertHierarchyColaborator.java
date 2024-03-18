package br.com.netdeal.application.dto.input;

import br.com.netdeal.application.dto.output.ColaboratorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsertHierarchyColaborator {
    private Long managerId;
    private Long subordinateId;
}
