package rede;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Brucce
 */
public class No implements Serializable, Comparable<No> {

    private final String rotulo;
    public double F[], lastF[], Y[] = null;
    public HashMap<String, HashMap<No, Double>> adjacentes = new HashMap<>();

    public No(String rotulo) {
        this.rotulo = rotulo;
    }

    public void add(String relacao, No objeto, double peso) {
        HashMap<No, Double> bagRotulos = adjacentes.get(relacao);
        if (bagRotulos == null) {
            bagRotulos = new HashMap<>();
            adjacentes.put(relacao, bagRotulos);
        }
        bagRotulos.put(objeto, peso);
    }

    public void resetarF() {
        for (int i = 0; i < Y.length; i++) {
            lastF[i] = F[i] = Y[i];
        }
    }

    @Override
    public int compareTo(No o) {
        return rotulo.compareTo(o.rotulo);
    }
    
    

    @Override
    public String toString() {
        return rotulo;
    }
}
