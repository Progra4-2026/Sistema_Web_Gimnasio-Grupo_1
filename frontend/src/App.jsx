import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import Layout from './components/layout/Layout';
import InicioPage from './pages/InicioPage';
import ServiciosPage from './pages/ServiciosPage';
import ContactoPage from './pages/ContactoPage';
import BlogPage from './pages/BlogPage';
import LoginPage from './pages/LoginPage';
import MantenimientoPage from './pages/MantenimientoPage';
import RutaPrivada from './components/auth/RutaPrivada';

export default function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<Layout />}>
              <Route path="/" element={<InicioPage />} />
              <Route path="/servicios" element={<ServiciosPage />} />
              <Route path="/contacto" element={<ContactoPage />} />
              <Route path="/blog" element={<BlogPage />} />
              <Route path="/login" element={<LoginPage />} />

              {/* Rutas privadas: solo con sesión iniciada (AuthGuard) */}
              <Route element={<RutaPrivada />}>
                <Route path="/mantenimiento" element={<MantenimientoPage />} />
              </Route>

              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </NotificationProvider>
    </AuthProvider>
  );
}
