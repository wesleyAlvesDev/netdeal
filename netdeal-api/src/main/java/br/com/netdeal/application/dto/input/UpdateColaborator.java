package br.com.netdeal.application.dto.input;

import br.com.netdeal.application.dto.output.ColaboratorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateColaborator {
    private Long id;
    private String fullName;
    private String password;
    private Integer scorePassword;
    private List<ColaboratorDto> subordinates;
}
