import MaquinaCard from '../cards/MaquinaCard';

const MAQUINAS = [
  { nombre: 'Caminadora eléctrica', imagen: '/assets/caminadora_electrica.jpg' },
  { nombre: 'Bicicleta estática', imagen: '/assets/bici_estatica.jpg' },
  { nombre: 'Prensa de piernas', imagen: '/assets/leg_press.jpg' },
  { nombre: 'Máquina de remo', imagen: '/assets/maquina_remo.jpg' },
  { nombre: 'Estación multifuerza', imagen: '/assets/estacion_multi_fuerza.jpg' },
  { nombre: 'Elípticas', imagen: '/assets/eliptica.jpg' },
];

export default function MaquinasList() {
  return (
    <div className="rejilla rejilla--estrecha">
      {MAQUINAS.map((maquina) => (
        <MaquinaCard key={maquina.nombre} maquina={maquina} />
      ))}
    </div>
  );
}
