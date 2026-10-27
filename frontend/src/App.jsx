import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import Layout from './components/layout/Layout';
import ProtectedRoute from './components/routing/ProtectedRoute';
import InicioPage from './pages/InicioPage';
import ServiciosPage from './pages/ServiciosPage';
import ContactoPage from './pages/ContactoPage';
import BlogPage from './pages/BlogPage';
import LoginPage from './pages/LoginPage';
import InstructorPage from './pages/InstructorPage';
import AdminPage from './pages/AdminPage';

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
              <Route element={<ProtectedRoute roles={['instructor']} />}>
                <Route path="/instructor" element={<InstructorPage />} />
              </Route>
              <Route element={<ProtectedRoute roles={['administrador']} />}>
                <Route path="/admin" element={<AdminPage />} />
              </Route>
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </NotificationProvider>
    </AuthProvider>
  );
}
