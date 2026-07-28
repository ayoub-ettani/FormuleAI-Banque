import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {AuthService} from '../services/auth-service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredRole = route.data['role'] as string;

  if (authService.isLoggedIn() && authService.hasRole(requiredRole)) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
