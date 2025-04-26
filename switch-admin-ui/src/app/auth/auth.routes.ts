import { Routes } from '@angular/router';

export const AUTH_ROUTES: Routes = [
    { 
        path: 'login',
        loadComponent: () => import('./login/login.component').then(m => m.LoginComponent),
        title: 'Login'
    },
    { 
        path: 'forgot-password',
        loadComponent: () => import('./forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent),
        title: 'Password Recovery'
    },
    { 
        path: 'reset-password',
        loadComponent: () => import('./reset-password/reset-password.component').then(m => m.ResetPasswordComponent),
        title: 'Reset Password'
    },
    { path: '', redirectTo: 'login', pathMatch: 'full' }
];