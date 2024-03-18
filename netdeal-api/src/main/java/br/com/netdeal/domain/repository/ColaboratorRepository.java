package br.com.netdeal.domain.repository;

import br.com.netdeal.domain.model.entity.Colaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColaboratorRepository extends JpaRepository<Colaborator, Long> {
    List<Colaborator> findAllByManagerIdIsNull();
}
