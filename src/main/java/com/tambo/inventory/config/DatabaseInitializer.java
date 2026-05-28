package com.tambo.inventory.config;

import com.tambo.inventory.entity.Rol;
import com.tambo.inventory.entity.RolNombre;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.repository.RolRepository;
import com.tambo.inventory.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Rol adminRol = inicializarRol(RolNombre.ROLE_ADMIN);
        Rol userRol = inicializarRol(RolNombre.ROLE_USER);

        inicializarUsuario("admin", "admin123", "Administrador Tambo", "admin@tambo.pe", adminRol);

        inicializarUsuario("empleado", "empleado123", "Empleado Tambo", "empleado@tambo.pe", userRol);
    }

    private Rol inicializarRol(RolNombre nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre(nombre);
                    return rolRepository.save(nuevoRol);
                });
    }

    private void inicializarUsuario(String username, String password, String nombre, String correo, Rol rol) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setUsername(username);
                    nuevo.setNombre(nombre);
                    nuevo.setCorreo(correo);
                    nuevo.setActivo(true);

                    Set<Rol> roles = new HashSet<>();
                    roles.add(rol);
                    nuevo.setRoles(roles);
                    return nuevo;
                });

        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
        System.out.println(">>> Usuario " + username + " inicializado con éxito (" + username + "/" + password + ")");
    }
}
