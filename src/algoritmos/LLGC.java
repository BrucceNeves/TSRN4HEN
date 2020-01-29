package algoritmos;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import rede.No;
import rede.Rede;

/**
 * @author Brucce
 */
public class LLGC extends ClassificadorTransdutivo {

    private final ArrayList<No> listaNos = new ArrayList<>();
    private final double mi;

    public LLGC(Rede rede, JSONObject json) {
        this(rede, json, json.getDouble("mi"));
    }
    public LLGC(Rede rede, JSONObject json, double mi) {
        super(rede, json);
        this.mi = mi;
        for (HashMap<String, No> nos : rede.camadas.values()) {
            for (No no : nos.values()) {
                listaNos.add(no);
            }
        }
    }

    @Override
    protected void propagacaoRotulos() {
        for (No Oi : listaNos) {
            if (mi == 1.0 && Oi.Y != null) {
                continue;
            }
            double f[] = rede.criarVetorClasse();
            for (HashMap<No, Double> adjs : Oi.adjacentes.values()) {
                for (No Oj : adjs.keySet()) {
                    double peso = getPesoRelacao(Oi, Oj, adjs.get(Oj));
                    for (int k = 0; k < f.length; k++) {
                        f[k] += Oj.lastF[k] * peso;
                    }
                }
            }
            if (Oi.Y != null) {
                for (int k = 0; k < f.length; k++) {
                    f[k] += mi * Oi.Y[k];
                }
            }
            Oi.F = f;
        }
    }

    protected double getPesoRelacao(No i, No j, double aresta) {
        return aresta / (getGrau(i) * getGrau(j));
    }

    @Override
    protected double getGrau(No obj) {
        double w = 0.0;
        for (HashMap<No, Double> adjs : obj.adjacentes.values()) {
            for (Double peso : adjs.values()) {
                w += peso;
            }
        }
        w = Math.sqrt(w);
        return w;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new Exception("Necessary to inform [JSON file]!");
        }
        JSONObject json = new JSONObject(ferramentas.Util.readInline(args[0]));
        JSONArray ja = json.getJSONArray("relations");
        String[] layers = new String[ja.length()];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = ja.getString(i);
        }
        Rede rede = new Rede(layers, json.getString("labels"));

        LLGC alg = new LLGC(rede, json);
        alg.classificar();
        rede.salvarRede(json.getString("output_file"));
    }
}
