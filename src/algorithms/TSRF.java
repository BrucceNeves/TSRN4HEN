package algorithms;

import java.util.HashMap;
import network.Node;
import network.Network;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brucce
 */
public class TSRF {

    private final Network network;
    private final LLGC alpha, beta;
    private final String target;


    public TSRF(Network rede, JSONObject json) {
        this.network = rede;
        this.alpha = new LLGC(rede, json);
        this.beta = new LLGC(rede, json, json.getDouble("miBeta"));
        this.target = json.getString("target_layer");
    }

    public void classificar() throws Exception {
        alpha.classifier();
        entropy();
        beta.classifier();
    }

    private void entropy() {
        HashMap<String, Double> entropyLayers = new HashMap<>();
        double totalNetworkEntropy = 0;
        for (String layer : network.layers.keySet()) {
            if (layer.compareTo(target) == 0) {
                continue;
            }
            double entropy_layer = 0;
            for (Node node : network.layers.get(layer).values()) {
                double total_node = 0, entropyNode = 0;
                for (double value : node.F) {
                    total_node += value;
                }
                for (double value : node.F) {
                    if (value != 0) {
                        double p_entropy = value / total_node;
                        entropyNode += -p_entropy * log2(p_entropy);
                    }
                }
                entropy_layer += entropyNode;
            }
            entropy_layer /= network.layers.get(layer).values().size();
            entropyLayers.put(layer, entropy_layer);
            totalNetworkEntropy += entropy_layer;
        }
        System.out.print("Entropy of each Layer: ");
        System.out.println(entropyLayers.toString());
        System.out.println("Total network entropy: " + totalNetworkEntropy);
        if (totalNetworkEntropy == 0) {
            return;
        }
        double entropyNormalized = 0;
        for (String layer : entropyLayers.keySet()) {
            double entropy = entropyLayers.get(layer);
            entropy = 1 - entropy / totalNetworkEntropy;
            entropyLayers.put(layer, entropy);
            entropyNormalized += entropy;
        }
        totalNetworkEntropy = entropyNormalized;
        for (String layer : entropyLayers.keySet()) {
            double entropy = entropyLayers.get(layer);
            entropy = entropy / totalNetworkEntropy;
            entropyLayers.put(layer, entropy);
        }
        System.out.print("Importance of Layers: ");
        System.out.println(entropyLayers.toString());
        for (HashMap<String, Node> layer : network.layers.values()) {
            for (Node node1 : layer.values()) {
                for (HashMap<Node, Double> adjs : node1.adjacent.values()) {
                    for (Node node2 : adjs.keySet()) {
                        String temp[] = node2.toString().split(":");
                        String node2_layer = temp[temp.length - 1];
                        Double entropyRelationNode2 = entropyLayers.get(node2_layer); // Takes the entropy of the adjacent group
                        if (entropyRelationNode2 == null) {// true for target
                            break;
                        }
                        // for each edge leaving node1 for the adjoining group the edge weight must be multiplied by the entropy of the adjacent group
                        double newWeight = adjs.get(node2) * entropyRelationNode2;
                        adjs.put(node2, newWeight);
                    }
                }
            }
        }
        for (Node node : network.layers.get(target).values()) {
            for (String relation : node.adjacent.keySet()) {
                HashMap<Node, Double> adjs = node.adjacent.get(relation);
                for (Node node2 : adjs.keySet()) {
                    String temp[] = node2.toString().split(":");
                    String node2_layer = temp[temp.length - 1];
                    Double entropyRelationNode2 = entropyLayers.get(node2_layer); // Takes the entropy of the adjacent group
                    if (entropyRelationNode2 == null) {
                        System.out.println("null layer " + node2_layer);
                        break;
                    }
                    // for each edge leaving node1 for the adjacent group the edge weight must be multiplied by the entropy of the adjacent group
                    double newWeight = adjs.get(node2) * entropyRelationNode2;
                    adjs.put(node2, newWeight);
                    node2.adjacent.get(relation).put(node, newWeight);
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
        JSONObject json = new JSONObject(tools.Useful.readInline(args[0]));
        JSONArray ja = json.getJSONArray("relations");
        String[] layers = new String[ja.length()];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = ja.getString(i);
        }
        Network network = new Network(layers, json.getString("labels"));

        TSRF alg = new TSRF(network, json);
        alg.classificar();
        network.saveNetwork(json.getString("output_file"));
    }
}
