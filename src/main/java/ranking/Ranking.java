package ranking;

import gestordeperfil.Perfil;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Clase que gestiona y mantiene diferentes clasificaciones de jugadores basadas en sus estadísticas.
 * Mantiene cuatro rankings independientes ordenados por:
 * <ul>
 *   <li>Puntos totales</li>
 *   <li>Partidas jugadas</li>
 *   <li>Victorias obtenidas</li>
 *   <li>Derrotas acumuladas</li>
 * </ul>
 *
 * <p>Los rankings se actualizan automáticamente al añadir o eliminar perfiles y mantienen
 * un orden descendente. En caso de empate, se usa el nombre de usuario como criterio secundario.
 *
 * @author Marc Ribas Acon
 */
public class Ranking {
    private TreeSet<Perfil> rankingPuntos;
    private TreeSet<Perfil> rankingPartidasJugadas;
    private TreeSet<Perfil> rankingVictorias;
    private TreeSet<Perfil> rankingDerrotas;

    /**
     * Construye un nuevo sistema de rankings con comparadores específicos:
     * <ul>
     *   <li>Orden principal: Estadística correspondiente (descendente)</li>
     *   <li>Orden secundario: Nombre de usuario (ascendente)</li>
     * </ul>
     */
    public Ranking()
    {
        rankingPuntos = new TreeSet<Perfil>(Comparator.comparingInt(Perfil::getPuntos).reversed().thenComparing(Perfil::getUsername));
        rankingPartidasJugadas = new TreeSet<>(Comparator.comparingInt(Perfil::getPartidasJugadas).reversed().thenComparing(Perfil::getUsername));
        rankingVictorias = new TreeSet<>(Comparator.comparingInt(Perfil::getPartidasGanadas).reversed().thenComparing(Perfil::getUsername));
        rankingDerrotas = new TreeSet<>(Comparator.comparingInt(Perfil::getPartidasPerdidas).reversed().thenComparing(Perfil::getUsername));
    }

    /**
     * Añade un perfil a todos los rankings automáticamente.
     *
     * @param perfil Perfil a registrar en las clasificaciones
     */
    public void addToRankings(Perfil perfil)
    {
        rankingPuntos.add(perfil);
        rankingPartidasJugadas.add(perfil);
        rankingVictorias.add(perfil);
        rankingDerrotas.add(perfil);
    }

    /**
     * Elimina un perfil de todos los rankings.
     *
     * @param perfil Perfil a eliminar de las clasificaciones
     */
    public void deleteFromRankings(Perfil perfil)
    {
        rankingPuntos.remove(perfil);
        rankingPartidasJugadas.remove(perfil);
        rankingVictorias.remove(perfil);
        rankingDerrotas.remove(perfil);
    }

    /**
     * Obtiene el ranking por puntos totales.
     *
     * @return TreeSet ordenado descendente por puntos
     */
    public TreeSet<Perfil> getRankingPuntos() { return rankingPuntos; }

    /**
     * Obtiene el ranking por partidas jugadas.
     *
     * @return TreeSet ordenado descendente por partidas jugadas
     */
    public TreeSet<Perfil> getRankingPartidasJugadas() { return rankingPartidasJugadas; }

    /**
     * Obtiene el ranking por victorias.
     *
     * @return TreeSet ordenado descendente por victorias
     */
    public TreeSet<Perfil> getRankingVictorias() { return  rankingVictorias; }

    /**
     * Obtiene el ranking por derrotas.
     *
     * @return TreeSet ordenado descendente por derrotas
     */
    public TreeSet<Perfil> getRankingDerrotas() { return rankingDerrotas; }

    /**
     * Verifica si todos los rankings están vacíos.
     *
     * @return true si no hay perfiles en ningún ranking, false en caso contrario
     */
    public boolean rankingsVacios() { return rankingPuntos.isEmpty(); }
}
