package br.com.criandoapi.projeto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import br.com.criandoapi.projeto.entity.Usuario;
import br.com.criandoapi.projeto.service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin("*")
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listUsuarios() {
        return ResponseEntity.status(200).body(usuarioService.listaUsuario());
    }

    @PostMapping
    public ResponseEntity<?> criarUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            // Validação básica dos campos
            if (usuario.getNome() == null || usuario.getEmail() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("mensagem", "Nome e e-mail são obrigatórios!"));
            }

            // Salva o usuário
            usuarioService.criarUsuario(usuario);

            // Resposta de sucesso
            Map<String, Object> resposta = Map.of(
                    "mensagem", "Usuário criado com sucesso!",
                    "usuario", usuarioService.criarUsuario(usuario).getNome());

            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);

        } catch (DataIntegrityViolationException e) {
            // Erro específico de violação de dados (ex: e-mail duplicado)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("mensagem", "E-mail já cadastrado!"));

        } catch (Exception e) {
            // Erro genérico (logar o erro para debug)
            System.err.println("Erro ao criar usuário: " + e.getMessage());

            return ResponseEntity.internalServerError().body(
                    Map.of("mensagem", "Erro interno no servidor"));
        }
    }

    @PutMapping
    public ResponseEntity<Usuario> editarUsuario(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(usuarioService.editarUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Integer id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.status(204).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> validarSenha(@Valid @RequestBody Usuario usuario) {
        Boolean valid = usuarioService.validarSenha(usuario);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(200).build();

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                // Opcional: tratar outros tipos de erros, como ObjectError
                String errorName = error.getObjectName();
                String errorMessage = error.getDefaultMessage();
                errors.put(errorName, errorMessage);
            }
        });

        return errors;
    }

}
