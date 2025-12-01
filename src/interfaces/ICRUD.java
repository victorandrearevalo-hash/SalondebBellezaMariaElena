package interfaces;

/**
 *
 * @author Administrador
 */
public interface ICRUD<T> extends
        ICreate<T>,
        IRead<T>,
        IUpdate<T>,
        IDelete<T> {
}
