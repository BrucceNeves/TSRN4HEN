package algoritmos;

import java.util.HashMap;
import rede.No;
import rede.Rede;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brucce
 */
public class TSRF {

    private final Rede rede;
    private final LLGC alpha, beta;
    private final String target;


    public TSRF(Rede rede, JSONObject json) {
        this.rede = rede;
        this.alpha = new LLGC(rede, json);
        this.beta = new LLGC(rede, json, json.getDouble("miBeta"));
        this.target = json.getString("target_layer");
    }

    public void classificar() throws Exception {
        alpha.classificar();
        entropia();
        beta.classificar();
    }

    private void entropia() {
        HashMap<String, Double> entropiaCamadas = new HashMap<>();
        double entropiaTotalRede = 0;
        for (String layer : rede.camadas.keySet()) {
            if (layer.compareTo(target) == 0) {
                continue;
            }
            double entropia_camada = 0;
            for (No no : rede.camadas.get(layer).values()) {
                double total_no = 0, entropiaNo = 0;
                for (double value : no.F) {
                    total_no += value;
                }
                for (double value : no.F) {
                    if (value != 0) {
                        double p_entropia = value / total_no;
                        entropiaNo += -p_entropia * log2(p_entropia);
                    }
                }
                entropia_camada += entropiaNo;
            }
            entropia_camada /= rede.camadas.get(layer).values().size();
            entropiaCamadas.put(layer, entropia_camada);
            entropiaTotalRede += entropia_camada;
        }
        System.out.print("Entropia de cada Camada: ");
        System.out.println(entropiaCamadas.toString());
        System.out.println("Entropia total da rede: " + entropiaTotalRede);
        if (entropiaTotalRede == 0) {
            return;
        }
        double entropiaNormalized = 0;
        for (String camada : entropiaCamadas.keySet()) {
            double entropia = entropiaCamadas.get(camada);
            entropia = 1 - entropia / entropiaTotalRede;
            entropiaCamadas.put(camada, entropia);
            entropiaNormalized += entropia;
        }
        entropiaTotalRede = entropiaNormalized;
        for (String camada : entropiaCamadas.keySet()) {
            double entropia = entropiaCamadas.get(camada);
            entropia = entropia / entropiaTotalRede;
            entropiaCamadas.put(camada, entropia);
        }
        System.out.print("Importancia das Camadas: ");
        System.out.println(entropiaCamadas.toString());
        for (HashMap<String, No> camada : rede.camadas.values()) {
            for (No no1 : camada.values()) {
                for (HashMap<No, Double> adjs : no1.adjacentes.values()) {
                    for (No no2 : adjs.keySet()) {
                        String temp[] = no2.toString().split(":");
                        String no2_layer = temp[temp.length - 1];
                        Double entropiaRelacaoNo2 = entropiaCamadas.get(no2_layer); // Pega a entropia do grupo adjacente
                        if (entropiaRelacaoNo2 == null) {// verdadeiro para target
                            break;
                        }
                        // para cada aresta saindo do no1 para o grupo adjancente o peso da aresta deve ser multiplicado pela entropia do grupo adjacente
                        double novoPeso = adjs.get(no2) * entropiaRelacaoNo2;
                        adjs.put(no2, novoPeso);
                    }
                }
            }
        }
        for (No no : rede.camadas.get(target).values()) {
            for (String relation : no.adjacentes.keySet()) {
                HashMap<No, Double> adjs = no.adjacentes.get(relation);
                for (No no2 : adjs.keySet()) {
                    String temp[] = no2.toString().split(":");
                    String no2_layer = temp[temp.length - 1];
                    Double entropiaRelacaoNo2 = entropiaCamadas.get(no2_layer); // Pega a entropia do grupo adjacente
                    if (entropiaRelacaoNo2 == null) {
                        System.out.println("camada null " + no2_layer);
                        break;
                    }
                    // para cada aresta saindo do no1 para o grupo adjancente o peso da aresta deve ser multiplicado pela entropia do grupo adjacente
                    double novoPeso = adjs.get(no2) * entropiaRelacaoNo2;
                    adjs.put(no2, novoPeso);
                    no2.adjacentes.get(relation).put(no, novoPeso);
                }
            }
        }
    }

    private double log2(double n) {
        return Math.log10(n) / Math.log10(2);
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

        TSRF alg = new TSRF(rede, json);
        alg.classificar();
        rede.salvarRede(json.getString("output_file"));
    }
}
