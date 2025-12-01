package interfaces;

import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public interface IRead<T> {

    ArrayList<T> listar();

    ArrayList<T> buscar(String criterio);

}
