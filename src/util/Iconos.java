package util;

import javax.swing.ImageIcon;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Administrador
 */
public class Iconos {

    private static final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

    /**
     * Devuelve ImageIcon cargado desde /imagenes/<name>
     * name ejemplo: "icono_guardar.png"
     */
    public static ImageIcon get(String name) {
        return cache.computeIfAbsent(name, n -> {
            String path = "/imagenes/" + n;
            java.net.URL url = Iconos.class.getResource(path);
            if (url == null) {
                System.err.println("Icono no encontrado en classpath: " + path);
                return null;
            }
            return new ImageIcon(url);
        });
    }
}
