package interfaces;

import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public interface interfaz<T> {

    boolean insertar(T t);

    boolean eliminar(T t);

    boolean actualizar(T t);

    T buscar(int id);

    ArrayList<T> listar();
    
}
