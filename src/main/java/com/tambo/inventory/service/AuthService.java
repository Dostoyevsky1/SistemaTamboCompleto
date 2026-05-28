package com.tambo.inventory.service;

import com.tambo.inventory.dto.AuthResponse;
import com.tambo.inventory.dto.LoginDTO;
import com.tambo.inventory.dto.UsuarioDTO;
import com.tambo.inventory.entity.Rol;
import com.tambo.inventory.entity.RolNombre;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.exception.BadRequestException;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.RolRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginDTO loginRequest) {
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

        return new AuthResponse(
                jwt,
                userPrincipal.getUsername(),
                userPrincipal.getNombre(),
                roles,
                1L // Sucessfully authenticated, send a mock sucursalId to not break the frontend expectation
        );
    }

    @Transactional
    public Usuario registrar(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByUsername(usuarioDTO.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya está en uso.");
        }

        if (usuarioRepository.existsByCorreo(usuarioDTO.getCorreo())) {
            throw new BadRequestException("El correo electrónico ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setActivo(true);

        Set<Rol> roles = new HashSet<>();
        if (usuarioDTO.getRoles() == null || usuarioDTO.getRoles().isEmpty()) {
            Rol userRol = rolRepository.findByNombre(RolNombre.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: El Rol por defecto ROLE_USER no fue encontrado."));
            roles.add(userRol);
        } else {
            usuarioDTO.getRoles().forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                    case "ROLE_ADMIN":
                        Rol adminRol = rolRepository.findByNombre(RolNombre.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: El Rol ROLE_ADMIN no fue encontrado."));
                        roles.add(adminRol);
                        break;
                    case "USER":
                    case "ROLE_USER":
                        Rol userRol = rolRepository.findByNombre(RolNombre.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Error: El Rol ROLE_USER no fue encontrado."));
                        roles.add(userRol);
                        break;
                    default:
                        throw new BadRequestException("Error: El Rol " + role + " no es válido.");
                }
            });
        }
        usuario.setRoles(roles);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioDTO(
                        u.getId(),
                        u.getUsername(),
                        null,
                        u.getNombre(),
                        u.getCorreo(),
                        u.getRoles().stream().map(r -> r.getNombre().name()).collect(Collectors.toSet())
                )).collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setCorreo(usuarioDTO.getCorreo());
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        if (usuarioDTO.getRoles() != null && !usuarioDTO.getRoles().isEmpty()) {
            Set<Rol> roles = new HashSet<>();
            usuarioDTO.getRoles().forEach(role -> {
                Rol r = rolRepository.findByNombre(RolNombre.valueOf(role))
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + role));
                roles.add(r);
            });
            usuario.setRoles(roles);
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return new UsuarioDTO(
                guardado.getId(),
                guardado.getUsername(),
                null,
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getRoles().stream().map(r -> r.getNombre().name()).collect(Collectors.toSet())
        );
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
