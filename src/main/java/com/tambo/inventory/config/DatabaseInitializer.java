package com.tambo.inventory.config;

import com.tambo.inventory.entity.Rol;
import com.tambo.inventory.entity.RolNombre;
import com.tambo.inventory.entity.Sucursal;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.repository.RolRepository;
import com.tambo.inventory.repository.SucursalRepository;
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
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            SucursalRepository sucursalRepository,
            PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Inicializar Roles
        Rol adminRol = inicializarRol(RolNombre.ROLE_ADMIN);
        Rol empleadoRol = inicializarRol(RolNombre.ROLE_EMPLEADO);

        // 2. Inicializar Sucursal por defecto
        Sucursal sedeCentral = inicializarSucursalCentral();

        // 3. Inicializar Usuario Administrador
        inicializarUsuarioAdmin(adminRol, sedeCentral);
    }

    private Rol inicializarRol(RolNombre nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre(nombre);
                    return rolRepository.save(nuevoRol);
                });
    }

    private Sucursal inicializarSucursalCentral() {
        return sucursalRepository.findByNombre("Sede Central")
                .orElseGet(() -> {
                    Sucursal sucursal = new Sucursal();
                    sucursal.setNombre("Sede Central");
                    sucursal.setDireccion("Av. Javier Prado Este 1020, San Isidro");
                    sucursal.setTelefono("01-4445555");
                    return sucursalRepository.save(sucursal);
                });
    }

    private void inicializarUsuarioAdmin(Rol adminRol, Sucursal sucursal) {
        Usuario admin = usuarioRepository.findByUsername("admin")
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setUsername("admin");
                    nuevo.setNombre("Administrador Tambo");
                    nuevo.setEmail("admin@tambo.pe");
                    nuevo.setActivo(true);
                    nuevo.setSucursal(sucursal);

                    Set<Rol> roles = new HashSet<>();
                    roles.add(adminRol);
                    nuevo.setRoles(roles);
                    return nuevo;
                });

        admin.setPassword(passwordEncoder.encode("admin123"));
        usuarioRepository.save(admin);
        System.out.println(">>> Usuario administrador inicial configurado/actualizado con éxito (admin/admin123)");
    }
}
