package br.com.criandoapi.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.criandoapi.projeto.entity.Usuario;

public interface IUsuario extends JpaRepository<Usuario, Integer> {

}
