import { apiRequest } from './client';
import Consulta from '../models/Consulta';

/** POST /api/consultas → { id, mensaje } */
export function crearConsulta(datos) {
  const consulta = datos instanceof Consulta ? datos : new Consulta(datos);
  return apiRequest('/api/consultas', { metodo: 'POST', cuerpo: consulta.aJson() });
}
