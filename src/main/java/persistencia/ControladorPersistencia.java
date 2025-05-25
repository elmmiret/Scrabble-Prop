package persistencia;

import gestordepartida.Partida;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;
import ranking.Ranking;

import java.util.Map;

/**
 * Controlador central para todas las operaciones de persistencia.
 * Coordina los DAO específicos para cada entidad y proporciona una interfaz unificada
 * para las operaciones de guardado y carga.
 *
 * @author Albert Aulet Niubó
 */
public class ControladorPersistencia {
    /** DAO para operaciones de persistencia de partidas ({@link Partida}), vinculado al {@link GestorDePerfil} */
    private final PartidaDAO partidaDAO;
    /** DAO para operaciones CRUD de perfiles de usuario ({@link Perfil}) */
    private final PerfilDAO perfilDAO;

    /**
     * Constructor que inicializa los distintos DAOs.
     */
    public ControladorPersistencia(GestorDePerfil gestorDePerfil) {
        this.partidaDAO = new PartidaDAO(gestorDePerfil); // Pasar el GestorDePerfil al DAO
        this.perfilDAO = new PerfilDAO();
    }

    /**
     * Guarda las partidas en el sistema de persistencia.
     *
     * @param partidas Mapa de partidas a guardar
     */
    public void guardarPartidas(Map<Integer, Partida> partidas) {
        partidaDAO.guardar(partidas);
    }

    /**
     * Carga las partidas desde el sistema de persistencia.
     *
     * @return Mapa de partidas cargadas
     */
    public Map<Integer, Partida> cargarPartidas() {
        return partidaDAO.cargar();
    }

    /**
     * Guarda los perfiles en el sistema de persistencia.
     *
     * @param perfiles Mapa de perfiles a guardar
     */
    public void guardarPerfiles(Map<String, Perfil> perfiles) {
        perfilDAO.guardar(perfiles);
    }

    /**
     * Carga los perfiles desde el sistema de persistencia.
     *
     * @return Mapa de perfiles cargados
     */
    public Map<String, Perfil> cargarPerfiles() {
        return perfilDAO.cargar();
    }


}