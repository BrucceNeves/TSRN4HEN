package algoritmos;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import rede.No;
import rede.Rede;

/**
 * @author Brucce
 */
public class LPHN extends ClassificadorTransdutivo {

    private String relacao = "";

    public LPHN(Rede rede, JSONObject json) {
        super(rede, json);
    }

    @Override
    protected void propagacaoRotulos() {
        for (HashMap<String, No> objetos : rede.camadas.values()) {
            for (No Oi : objetos.values()) {
                if (Oi.Y != null) {
                    continue;
                }
                double f[] = rede.criarVetorClasse();
                for (String Oi_adjRelacao : Oi.adjacentes.keySet()) {
                    this.relacao = Oi_adjRelacao;
                    HashMap<No, Double> adjRelacao = Oi.adjacentes.get(Oi_adjRelacao);
                    for (No Oj : adjRelacao.keySet()) {
                        double peso = adjRelacao.get(Oj);
                        peso = getGrauRelacao(Oi, Oj, peso);
                        /*if (Double.isNaN(peso)) {
                            System.out.println("Erro: " + Oi + "\t" + Oj + "\t" + peso);
                            System.exit(1);
                        }*/
                        for (int i = 0; i < f.length; i++) {
                            f[i] += Oj.lastF[i] * (peso);
                            /*if (Double.isNaN(f[i])) {
                                System.out.println("Erro: " + Oi + "\toj: " + Oj + "\t" + Oj_f[i] + "\tpos: " + i + "\tpeso: " + peso);
                                System.exit(1);
                            }*/
                        }
                    }
                }
                double w = getGrau(Oi);
                if (Double.isNaN(w)) {
                    System.out.println("Erro: " + Oi + "\t" + w);
                    System.exit(1);
                }
                for (int i = 0; i < f.length; i++) {
                    f[i] /= w;
                    if (Double.isNaN(f[i])) {
                        System.out.println("Erro: " + Oi + "\t" + f[i] + "\t" + w);
                        System.exit(1);
                    }
                }
                Oi.F = f;
            }
        }
    }

    protected double getGrauRelacao(No i, No j, double aresta) {
        double aux = getGrau2(i) * getGrau2(j);
        double w = aresta / aux;
        if (aresta > aux) {
            System.out.println("Erro-fraco: grauRelacao(" + i + ", " + j + ", " + aresta + ") grau(i)*grau(j)= " + aux);
        }
        w += 1;
        return w;
    }

    protected double getGrau2(No obj) {
        Double w = 0.0;
        HashMap<No, Double> adjs = obj.adjacentes.get(relacao);
        for (Double peso : adjs.values()) {
            w += peso;
        }
        w = Math.sqrt(w);
        return w;
    }

    @Override
    protected double getGrau(No obj) {
        Double w = 0.0;
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

        LPHN alg = new LPHN(rede, json);
        alg.classificar();
        rede.salvarRede(json.getString("output_file"));
    }
}
