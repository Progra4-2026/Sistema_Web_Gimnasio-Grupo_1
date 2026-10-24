import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import { CursosProvider } from './context/CursosContext';
import Layout from './components/layout/Layout';
import RutaPrivada from './components/auth/RutaPrivada';
import InicioPage from './pages/InicioPage';
import ContactoPage from './pages/ContactoPage';
import BlogPage from './pages/BlogPage';
import LoginPage from './pages/LoginPage';
import ServiciosLayout from './pages/servicios/ServiciosLayout';
import InscripcionPage from './pages/servicios/InscripcionPage';
import CursosPage from './pages/servicios/CursosPage';
import RutinasPage from './pages/servicios/RutinasPage';
import MaquinasPage from './pages/servicios/MaquinasPage';
import InstructoresPage from './pages/servicios/InstructoresPage';
import DesinscripcionPage from './pages/servicios/DesinscripcionPage';
import MantenimientoLayout from './pages/mantenimiento/MantenimientoLayout';
import MantenimientoCursosPage from './pages/mantenimiento/MantenimientoCursosPage';
import MantenimientoClientesPage from './pages/mantenimiento/MantenimientoClientesPage';

/**
 * Rutas de la SPA (React Router). Navegar entre ellas no recarga la página.
 *
 *   /                          Inicio
 *   /contacto                  Contactos
 *   /blog                      Blog
 *   /servicios/inscripcion     Inscripción      (también /inscripcion)
 *   /servicios/cursos          Cursos           (también /cursos)
 *   /servicios/rutinas         Rutinas          (también /rutinas)
 *   /servicios/maquinas        Máquinas
 *   /servicios/instructores    Instructores     (también /instructores)
 *   /servicios/desinscribirse  Desinscribirse
 *   /login                     Iniciar sesión
 *   /mantenimiento/cursos      Mantenimiento    — privada (RutaPrivada = AuthGuard)
 *   /mantenimiento/clientes    Mantenimiento    — privada
 */
export default function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <CursosProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route path="/" element={<InicioPage />} />
                <Route path="/contacto" element={<ContactoPage />} />
                <Route path="/blog" element={<BlogPage />} />
                <Route path="/login" element={<LoginPage />} />

                <Route path="/servicios" element={<ServiciosLayout />}>
                  <Route index element={<Navigate to="inscripcion" replace />} />
                  <Route path="inscripcion" element={<InscripcionPage />} />
                  <Route path="cursos" element={<CursosPage />} />
                  <Route path="rutinas" element={<RutinasPage />} />
                  <Route path="maquinas" element={<MaquinasPage />} />
                  <Route path="instructores" element={<InstructoresPage />} />
                  <Route path="desinscribirse" element={<DesinscripcionPage />} />
                </Route>

                {/* Accesos directos */}
                <Route path="/inscripcion" element={<Navigate to="/servicios/inscripcion" replace />} />
                <Route path="/cursos" element={<Navigate to="/servicios/cursos" replace />} />
                <Route path="/rutinas" element={<Navigate to="/servicios/rutinas" replace />} />
                <Route path="/instructores" element={<Navigate to="/servicios/instructores" replace />} />

                {/* Rutas privadas: solo con sesión iniciada (AuthGuard) */}
                <Route element={<RutaPrivada />}>
                  <Route path="/mantenimiento" element={<MantenimientoLayout />}>
                    <Route index element={<Navigate to="cursos" replace />} />
                    <Route path="cursos" element={<MantenimientoCursosPage />} />
                    <Route path="clientes" element={<MantenimientoClientesPage />} />
                  </Route>
                </Route>

                <Route path="*" element={<Navigate to="/" replace />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </CursosProvider>
      </NotificationProvider>
    </AuthProvider>
  );
}
