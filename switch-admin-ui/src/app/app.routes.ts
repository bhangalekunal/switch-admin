import { Routes } from '@angular/router';

export const routes: Routes = [
    { 
        path: 'auth',
        loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
    },
    {
        path: '404',
        loadComponent: () => import('./core/components/not-found/not-found.component').then(m => m.NotFoundComponent),
        title: 'Page Not Found'
    },
    { path: '**', redirectTo: '404' }
];
