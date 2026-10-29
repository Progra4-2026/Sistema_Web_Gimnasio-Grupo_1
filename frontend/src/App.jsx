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
import MantenimientoInstructoresPage from './pages/mantenimiento/MantenimientoInstructoresPage';
import MantenimientoAdministradoresPage from './pages/mantenimiento/MantenimientoAdministradoresPage';
import MantenimientoParametrosPage from './pages/mantenimiento/MantenimientoParametrosPage';
import InstructorLayout from './pages/instructor/InstructorLayout';
import InstructorRutinasPage from './pages/instructor/InstructorRutinasPage';
import InstructorMaquinasPage from './pages/instructor/InstructorMaquinasPage';
import { ROLES } from './models/Usuario';

/**
 * Rutas de la SPA (React Router). Navegar entre ellas no recarga la página.
 *
 *   /                          Inicio
 *   /contacto                  Contactos
 *   /blog                      Blog
 *   /servicios/inscripcion     Inscripción      (también /inscripcion)
 *   /servicios/cursos          Cursos           (también /cursos)
 *   /servicios/rutinas         Rutinas          (también /rutinas) — el cliente ve solo las suyas
 *   /servicios/maquinas        Máquinas
 *   /servicios/instructores    Instructores     (también /instructores)
 *   /servicios/desinscribirse  Desinscribirse
 *   /login                     Iniciar sesión
 *
 * Rutas privadas (RutaPrivada = AuthGuard por rol; el backend también verifica con @PreAuthorize):
 *   /instructor/rutinas            Panel del instructor  — rol instructor
 *   /instructor/maquinas           Panel del instructor  — rol instructor
 *   /mantenimiento/cursos          Mantenimiento         — rol administrador
 *   /mantenimiento/clientes        Mantenimiento         — rol administrador
 *   /mantenimiento/instructores    Mantenimiento         — rol administrador
 *   /mantenimiento/administradores Mantenimiento         — rol administrador
 *   /mantenimiento/parametros      Mantenimiento         — rol administrador
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

                {/* Rutas privadas por rol (AuthGuard) */}
                <Route element={<RutaPrivada roles={[ROLES.INSTRUCTOR]} />}>
                  <Route path="/instructor" element={<InstructorLayout />}>
                    <Route index element={<Navigate to="rutinas" replace />} />
                    <Route path="rutinas" element={<InstructorRutinasPage />} />
                    <Route path="maquinas" element={<InstructorMaquinasPage />} />
                  </Route>
                </Route>

                <Route element={<RutaPrivada roles={[ROLES.ADMINISTRADOR]} />}>
                  <Route path="/mantenimiento" element={<MantenimientoLayout />}>
                    <Route index element={<Navigate to="cursos" replace />} />
                    <Route path="cursos" element={<MantenimientoCursosPage />} />
                    <Route path="clientes" element={<MantenimientoClientesPage />} />
                    <Route path="instructores" element={<MantenimientoInstructoresPage />} />
                    <Route path="administradores" element={<MantenimientoAdministradoresPage />} />
                    <Route path="parametros" element={<MantenimientoParametrosPage />} />
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
