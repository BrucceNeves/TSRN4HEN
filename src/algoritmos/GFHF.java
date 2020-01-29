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
public class GFHF extends ClassificadorTransdutivo {

    private final ArrayList<No> listaNos = new ArrayList<>();

    public GFHF() {
    }

    public GFHF(Rede rede, JSONObject json) {
        super(rede, json);
        for (HashMap<String, No> nos : rede.camadas.values()) {
            for (No no : nos.values()) {
                listaNos.add(no);
            }
        }
    }

    @Override
    protected void propagacaoRotulos() {
        for (No Oi : listaNos) {
            if (Oi.Y != null) {
                continue;
            }
            double f[] = rede.criarVetorClasse();
            for (HashMap<No, Double> adjs : Oi.adjacentes.values()) {
                for (No Oj : adjs.keySet()) {
                    double peso = adjs.get(Oj);
                    for (int i = 0; i < f.length; i++) {
                        f[i] += Oj.lastF[i] * peso;
                    }
                }
            }
            double w = getGrau(Oi);
            for (int i = 0; i < f.length; i++) {
                f[i] /= w;
            }
            Oi.F = f;
        }
    }

    @Override
    protected double getGrau(No obj) {
        double w = 0;
        for (HashMap<No, Double> adjs : obj.adjacentes.values()) {
            for (Double peso : adjs.values()) {
                w += peso;
            }
        }
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

        GFHF alg = new GFHF(rede, json);
        alg.classificar();
        rede.salvarRede(json.getString("output_file"));
    }
}
