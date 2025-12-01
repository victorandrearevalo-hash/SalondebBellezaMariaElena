package fakeDAO;

import clases.Usuario;
import interfaces.IRead;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class FakeRead implements IRead<Usuario> {

    private ArrayList<Usuario> lista = new ArrayList<>();

    public void setDatos(ArrayList<Usuario> datos) {
        this.lista = datos; // Para cargar datos de prueba
    }

    @Override
    public ArrayList<Usuario> listar() {
        return lista;
    }

    @Override
    public ArrayList<Usuario> buscar(String criterio) {
        ArrayList<Usuario> resultado = new ArrayList<>();
        for (Usuario u : lista) {
            if (u.getNombre().contains(criterio)) {
                resultado.add(u);
            }
        }
        return resultado;
    }
}
