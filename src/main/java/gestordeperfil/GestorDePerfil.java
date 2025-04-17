package gestordeperfil;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la creación, modificación y eliminación de perfiles de usuario.
 * Proporciona métodos para verificar credenciales, seguridad de contraseñas y recuperación de cuentas.
 * Utiliza un mapa para almacenar los perfiles y un scanner para entrada de datos.
 *
 * @author Marc Ribas Acon
 */
public class GestorDePerfil {

    private Map<String, Perfil> jugadores;
    Scanner lector;

    /**
     * Construye un nuevo gestor de perfiles inicializando las estructuras de datos necesarias.
     */
    public GestorDePerfil() {
        lector = new Scanner(System.in);
        jugadores = new HashMap<>();
    }

    /**
     * Verifica si la contraseña proporcionada coincide con la del perfil del usuario.
     *
     * @param username el nombre de usuario a verificar
     * @param password la contraseña a comprobar
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public boolean esPasswordCorrecta(String username, String password) {
        return jugadores.get(username).getPassword().equals(password);
    }

    /**
     * Verifica si la frase de recuperación coincide con la del perfil (ignorando mayúsculas/minúsculas).
     *
     * @param username el nombre de usuario a verificar
     * @param fraseRecuperacion la frase de recuperación a comprobar
     * @return true si la frase es correcta, false en caso contrario
     */
    public boolean esFraseRecuperacionCorrecta(String username, String fraseRecuperacion) {
        return jugadores.get(username).getFraseRecuperacion().equalsIgnoreCase(fraseRecuperacion);
    }

    /**
     * Comprueba si una contraseña cumple los requisitos mínimos de seguridad:
     * - Longitud mínima de 8 caracteres
     * - Al menos una letra mayúscula
     * - Al menos un dígito numérico
     *
     * @param password la contraseña a validar
     * @return true si la contraseña es segura, false en caso contrario
     */
    public boolean esPasswordSegura(String password) {
        if (password == null || password.length() < 8) return false;
        boolean tieneMayuscula = false;
        boolean tieneDigito = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) tieneMayuscula = true;
            else if (Character.isDigit(c)) tieneDigito = true;
            if (tieneMayuscula && tieneDigito) return true;
        }
        return false;
    }

    /**
     * Crea un nuevo perfil y lo añade al gestor.
     *
     * @param username nombre único para el nuevo perfil
     * @param password contraseña del perfil
     * @param fraseRecuperacion frase de recuperación para seguridad
     */
    public void crearPerfil(String username, String password, String fraseRecuperacion) {
        jugadores.put(username, new Perfil(username, password, fraseRecuperacion));
    }

    /**
     * Actualiza el nombre de usuario de un perfil y su posición en el mapa.
     *
     * @param username nombre actual del perfil
     * @param newUsername nuevo nombre a establecer
     */
    public void cambiarUsername(String username, String newUsername) {
        Perfil perfil = jugadores.get(username);
        perfil.cambiarUsername(newUsername);
        jugadores.remove(username);
        jugadores.put(newUsername, perfil);
    }

    /**
     * Modifica la contraseña de un perfil existente.
     *
     * @param username nombre del perfil a modificar
     * @param newPassword nueva contraseña a establecer
     */
    public void cambiarPassword(String username, String newPassword) {
        jugadores.get(username).cambiarPassword(newPassword);
    }


    /**
     * Elimina permanentemente un perfil del gestor.
     *
     * @param username nombre del perfil a eliminar
     */
    public void eliminarPerfil(String username) {
        jugadores.remove(username);
    }

    /**
     * Devuelve todos los perfiles almacenados en el gestor.
     *
     * @return mapa con nombres de usuario como clave y perfiles como valores
     */
    public Map<String, Perfil> getJugadores() { return jugadores; }

    /**
     * Devuelve el perfil del jugador con el username indicado.
     *
     * @param username nombre de perfil del jugador a obtener
     * @return el perfil que coincida con el username
     */
    public Perfil getPerfil(String username)
    {
        return jugadores.get(username);
    }

    /**
     * Comprueba la existencia de un perfil con el nombre de usuario especificado.
     *
     * @param username nombre a buscar
     * @return true si existe el perfil, false en caso contrario
     */
    public boolean existeJugador(String username) { return jugadores.containsKey(username); }
}