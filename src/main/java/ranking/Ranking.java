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

    /**
     * Ranking principal ordenado por puntos totales en orden descendente.
     * En caso de empate, usa el nombre de usuario en orden alfabético ascendente.
     */
    private TreeSet<Perfil> rankingPuntos;

    /**
     * Clasificación de jugadores por cantidad total de partidas jugadas (descendente).
     * Utiliza el nombre de usuario como desempate para perfiles con misma cantidad de partidas.
     */
    private TreeSet<Perfil> rankingPartidasJugadas;

    /**
     * Ranking de victorias ordenado de mayor a menor número de partidas ganadas.
     * El criterio secundario de ordenación es el nombre de usuario en orden ascendente.
     */
    private TreeSet<Perfil> rankingVictorias;

    /**
     * Clasificación de derrotas organizada por mayor número de partidas perdidas primero.
     * Los empates en número de derrotas se resuelven alfabéticamente por nombre de usuario.
     */
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
        rankingPartidasJugadas = new TreeSet<Perfil>(Comparator.comparingInt(Perfil::getPartidasJugadas).reversed().thenComparing(Perfil::getUsername));
        rankingVictorias = new TreeSet<Perfil>(Comparator.comparingInt(Perfil::getPartidasGanadas).reversed().thenComparing(Perfil::getUsername));
        rankingDerrotas = new TreeSet<Perfil>(Comparator.comparingInt(Perfil::getPartidasPerdidas).reversed().thenComparing(Perfil::getUsername));
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
