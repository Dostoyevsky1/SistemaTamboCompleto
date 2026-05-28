package com.tambo.inventory.service;

import com.tambo.inventory.dto.AuthResponse;
import com.tambo.inventory.dto.LoginRequest;
import com.tambo.inventory.dto.RegisterRequest;
import com.tambo.inventory.entity.Rol;
import com.tambo.inventory.entity.RolNombre;
import com.tambo.inventory.entity.Sucursal;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.exception.BadRequestException;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.RolRepository;
import com.tambo.inventory.repository.SucursalRepository;
import com.tambo.inventory.repository.UsuarioRepository;
import com.tambo.inventory.security.JwtTokenProvider;
import com.tambo.inventory.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            SucursalRepository sucursalRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.sucursalRepository = sucursalRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Obtener el ID de la sucursal de la base de datos
        Usuario usuario = usuarioRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Long sucursalId = (usuario.getSucursal() != null) ? usuario.getSucursal().getId() : null;

        return new AuthResponse(
                jwt,
                userPrincipal.getUsername(),
                userPrincipal.getNombre(),
                roles,
                sucursalId
        );
    }

    @Transactional
    public Usuario registrar(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya está en uso.");
        }

        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El correo electrónico ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registerRequest.getUsername());
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setActivo(true);

        // Asignación de Roles
        Set<Rol> roles = new HashSet<>();
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Rol userRol = rolRepository.findByNombre(RolNombre.ROLE_EMPLEADO)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: El Rol por defecto ROLE_EMPLEADO no fue encontrado en la base de datos."));
            roles.add(userRol);
        } else {
            registerRequest.getRoles().forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                    case "ROLE_ADMIN":
                        Rol adminRol = rolRepository.findByNombre(RolNombre.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: El Rol ROLE_ADMIN no fue encontrado en la base de datos."));
                        roles.add(adminRol);
                        break;
                    case "EMPLEADO":
                    case "ROLE_EMPLEADO":
                        Rol empleadoRol = rolRepository.findByNombre(RolNombre.ROLE_EMPLEADO)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: El Rol ROLE_EMPLEADO no fue encontrado en la base de datos."));
                        roles.add(empleadoRol);
                        break;
                    default:
                        throw new BadRequestException("Error: El Rol " + role + " no es válido.");
                }
            });
        }
        usuario.setRoles(roles);

        // Asignación de Sucursal (opcional)
        if (registerRequest.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(registerRequest.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + registerRequest.getSucursalId() + " no existe."));
            usuario.setSucursal(sucursal);
        }

        return usuarioRepository.save(usuario);
    }
}
