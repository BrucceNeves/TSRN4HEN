package algoritmos;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import rede.No;
import rede.Rede;

/**
 * @author Brucce
 */
public final class GNetMine extends ClassificadorTransdutivo {

    public final HashMap<String, Double> cacheRelacao = new HashMap<>();
    private final double mi;
    private String relacao;
    private final HashMap<String, Double> importanciaRelacao;

    public GNetMine(Rede rede, JSONObject json, HashMap<String, Double> importanciaRelacao) {
        super(rede, json);
        mi = json.getDouble("mi");
        this.importanciaRelacao = importanciaRelacao;
        normalizarImportanciaRelacao();
    }

    public GNetMine(Rede rede, JSONObject json) {
        super(rede, json);
        mi = json.getDouble("mi");
        this.importanciaRelacao = new HashMap<>();
        int total = rede.relations_name.size();
        for (String r : rede.relations_name) {
            importanciaRelacao.put(r, (1.0 / total));
        }
    }

    private void normalizarImportanciaRelacao() {
        double soma = 0;
        for (double v : importanciaRelacao.values()) {
            soma += v;
        }
        for (String r : importanciaRelacao.keySet()) {
            double v = importanciaRelacao.get(r);
            importanciaRelacao.put(r, v / soma);
        }
    }

    @Override
    protected void propagacaoRotulos() {
        for (HashMap<String, No> nos : rede.camadas.values()) {
            for (No Oi : nos.values()) {
                double f[] = rede.criarVetorClasse();
                if (mi == 1.0 && Oi.Y != null) {
                    continue;
                }
                for (String Oi_adjRelacao : Oi.adjacentes.keySet()) {
                    relacao = Oi_adjRelacao;
                    HashMap<No, Double> adjs = Oi.adjacentes.get(relacao);
                    for (No Oj : adjs.keySet()) {
                        if (importanciaRelacao.get(relacao) == null) {
                            System.out.println(relacao);
                        }
                        double peso = getPesoRelacao(Oi, Oj, adjs.get(Oj)) * importanciaRelacao.get(relacao);
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
    }

    protected double getPesoRelacao(No i, No j, double aresta) {
        return aresta / (getGrau(i) * getGrau(j));
    }

    @Override
    protected double getGrau(No obj) {
        double w = 0.0;
        HashMap<No, Double> adjs = obj.adjacentes.get(relacao);
        for (Double peso : adjs.values()) {
            w += peso;
        }
        w = Math.sqrt(w);
        return w;
    }

    @Override
    public String toString() {
        return "GNetMine-" + importanciaRelacao.toString() + "-mi_" + mi;
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
        HashMap<String, Double> importanciaRelacao = new HashMap<>();
        JSONObject json_importancia = json.getJSONObject("weight_relations");
        for (String key : json_importancia.keySet()) {
            importanciaRelacao.put(key, json_importancia.getDouble(key));
        }
        Rede.setRelacoesName(importanciaRelacao.keySet().toArray());
        Rede rede = new Rede(layers, json.getString("labels"));

        GNetMine alg = importanciaRelacao.size() > 0 ? new GNetMine(rede, json, importanciaRelacao) : new GNetMine(rede, json);
        alg.classificar();
        rede.salvarRede(json.getString("output_file"));
    }
}
