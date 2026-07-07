// Comunicación: Protege el acceso a rutas administrativas en app-routing.module.ts consultando a AuthService si el usuario posee el rol de administrador (ROLE_ADMIN), redirigiendo a la vista de productos si no lo es.
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.isAdmin()) {
    return true;
  } else {
    router.navigate(['/dashboard/productos']);
    return false;
  }
};
