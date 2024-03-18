package br.com.netdeal.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColaboratorDto {
    private Long id;
    private String fullName;
    private String password;
    private Integer scrorePassword;
    private List<ColaboratorDto> subordinates;
}
