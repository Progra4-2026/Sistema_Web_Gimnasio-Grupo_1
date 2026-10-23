import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import { CursosProvider } from './context/CursosContext';
import Layout from './components/layout/Layout';
import InicioPage from './pages/InicioPage';
import ServiciosPage from './pages/ServiciosPage';
import ContactoPage from './pages/ContactoPage';
import BlogPage from './pages/BlogPage';

export default function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <CursosProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route path="/" element={<InicioPage />} />
                <Route path="/servicios" element={<ServiciosPage />} />
                <Route path="/contacto" element={<ContactoPage />} />
                <Route path="/blog" element={<BlogPage />} />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </CursosProvider>
      </NotificationProvider>
    </AuthProvider>
  );
}
