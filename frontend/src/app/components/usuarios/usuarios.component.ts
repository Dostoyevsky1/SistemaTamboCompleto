// Comunicación: Inyecta UsuarioService para ejecutar el mantenimiento CRUD sobre la lista de usuarios del sistema mediante llamadas REST a /usuarios y ChangeDetectorRef para forzar la actualización de la UI.
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsuarioService, Usuario } from '../../services/usuario.service';

@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.css'],
  standalone: false
})
export class UsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  showUserModal: boolean = false;
  selectedUsuario: Usuario | null = null;
  usuarioForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
    this.inicializarFormulario();
  }

  cargarUsuarios(): void {
    this.usuarioService.listarUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  inicializarFormulario(): void {
    this.usuarioForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      correo: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: [''],
      roles: ['ROLE_USER', [Validators.required]]
    });
  }

  abrirModalUsuario(usuario: Usuario | null = null): void {
    this.selectedUsuario = usuario;
    this.showUserModal = true;

    if (usuario) {
      this.usuarioForm.patchValue({
        nombre: usuario.nombre,
        correo: usuario.correo,
        username: usuario.username,
        password: '',
        roles: usuario.roles.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_USER'
      });
      this.usuarioForm.get('password')?.setValidators([]);
    } else {
      this.usuarioForm.reset({
        nombre: '',
        correo: '',
        username: '',
        password: '',
        roles: 'ROLE_USER'
      });
      this.usuarioForm.get('password')?.setValidators([Validators.required, Validators.minLength(4)]);
    }
    this.usuarioForm.get('password')?.updateValueAndValidity();
  }

  cerrarModalUsuario(): void {
    this.showUserModal = false;
    this.selectedUsuario = null;
    this.usuarioForm.reset();
  }

  guardarUsuario(): void {
    if (this.usuarioForm.invalid) {
      this.usuarioForm.markAllAsTouched();
      return;
    }

    const val = this.usuarioForm.value;
    const data: Usuario = {
      nombre: val.nombre,
      correo: val.correo,
      username: val.username,
      roles: [val.roles],
      ...(val.password ? { password: val.password } : {})
    };

    if (this.selectedUsuario && this.selectedUsuario.id) {
      this.usuarioService.actualizarUsuario(this.selectedUsuario.id, data).subscribe({
        next: () => {
          this.cerrarModalUsuario();
          this.cargarUsuarios();
        },
        error: (err) => alert('Error al actualizar usuario: ' + (err.error?.message || err.message))
      });
    } else {
      this.usuarioService.crearUsuario(data).subscribe({
        next: () => {
          this.cerrarModalUsuario();
          this.cargarUsuarios();
        },
        error: (err) => alert('Error al registrar usuario: ' + (err.error?.message || err.message))
      });
    }
  }

  eliminarUsuario(id: number): void {
    if (confirm('¿Está seguro de eliminar este usuario?')) {
      this.usuarioService.eliminarUsuario(id).subscribe({
        next: () => {
          this.cargarUsuarios();
        },
        error: (err) => alert('Error al eliminar usuario')
      });
    }
  }
}
