package com.belos.microservicio_usuarios.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.belos.microservicio_usuarios.model.Usuario;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    

    @GetMapping("{id}")
    public Usuario getUsuarioById(Long id) {

        // En una aplicación real, aquí iría la lógica para buscar en una base de datos
        if (id == 1L) {
            return new Usuario(1L, "Alice", "alice@example.com");
        } else if (id == 2L) {
            return new Usuario(2L, "Bob", "bob@example.com");
        } else {
            return new Usuario(id, "Usuario Desconocido", "desconocido@example.com");
        }        
    }
}
