package algoritmos;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import rede.Rede;
import rede.No;

/**
 * @author Brucce
 */
public abstract class ClassificadorTransdutivo {

    private int interacoes;
    private double countDivergencia, convAntigo;
    private final double limiarConvergencia;
    private final ArrayList<Double> cacheLimiarConv = new ArrayList<>();
    protected Rede rede;

    public ClassificadorTransdutivo() {
        this.limiarConvergencia = 0;
    }

    public ClassificadorTransdutivo(Rede rede, JSONObject config) {
        this(rede, config.getInt("iteracoes"), config.getDouble("limiarConvergencia"));
    }

    public ClassificadorTransdutivo(Rede rede, int interacoes, double limiarConvergencia) {
        this.rede = rede;
        this.interacoes = interacoes;
        this.limiarConvergencia = limiarConvergencia;
    }

    public void classificar() throws Exception {
        int count;
        convAntigo = 0;
        countDivergencia = 0;
        cacheLimiarConv.clear();
        for (count = 0; count < interacoes; count++) {
            propagacaoRotulos();
            if (convergiu() && count > 5) {
                break;
            }
        }
    }

    public void setInteracoes(int interacoes) {
        this.interacoes = interacoes;
    }

    protected abstract double getGrau(No obj);

    protected abstract void propagacaoRotulos() throws Exception;

    protected boolean convergiu() {
        double convNovo = 0.0;
        for (HashMap<String, No> camada : rede.camadas.values()) {
            for (No no : camada.values()) {
                for (int i = 0; i < no.F.length; i++) {
                    convNovo += Math.abs(no.lastF[i] - no.F[i]);
                }
                no.lastF = no.F;
            }
        }
        if (Double.isNaN(convNovo) || Double.isInfinite(convNovo)) {
            System.out.println("########## convNovo(" + convNovo + ")");
            return true;
        } else if (convNovo > convAntigo) {
            System.out.println("########## convNovo > convAntigo " + convNovo + " > " + convAntigo);
            countDivergencia++;
        } else {
            countDivergencia = 0;
        }
        cacheLimiarConv.add(convNovo);
        System.out.println(convNovo + " < " + limiarConvergencia);
        if (convNovo < limiarConvergencia) {
            return true;
        }

        if (countDivergencia >= 4) {
            System.out.println("###### Finished by divergence!");
            return true;
        }
        if (cacheLimiarConv.size() > 10) {
            cacheLimiarConv.remove(0);
        }
        if (cacheLimiarConv.size() >= 10) {
            if (cacheLimiarConv.get(0) < cacheLimiarConv.get(9) && cacheLimiarConv.get(1) < cacheLimiarConv.get(8)) {
                System.out.println("###### Finished by divergence! (zig-zag)");
                return true;
            }
            for (int i = 0; i < cacheLimiarConv.size(); i++) {
                if (convNovo != cacheLimiarConv.get(i)) {
                    convAntigo = convNovo;
                    return false;
                }
            }
            System.out.println("Finished by cache");
            return true;
        }
        convAntigo = convNovo;
        return false;
    }

}
