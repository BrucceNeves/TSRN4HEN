package network;

import tools.Useful;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Brucce
 */
public final class Network implements Serializable {

    private int F_size = 0;
    public static final ArrayList<String> relations_name = new ArrayList<>();
    public final HashMap<String, HashMap<String, Node>> layers = new HashMap<>();

    public static void setRelationsName(Object[] relations) {
        for (Object o : relations) {
            relations_name.add(o.toString());
        }
    }

    public Network(String fileRelations, String fileLabels) throws Exception {
        this(new String[]{fileRelations}, fileLabels);
    }

    public Network(String[] fileRelations, String fileLabels) throws Exception {
        HashMap<String, String> labels = read_F_file(fileLabels);
        int count = 0;
        System.out.println("Starting reading network");
        for (String layer : fileRelations) {
            System.out.println("\t" + layer);
            BufferedReader br = new BufferedReader(new FileReader(layer));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                String aux[] = line.split("\t");
                if (aux.length != 3) {
                    System.out.println(java.util.Arrays.toString(aux));
                    continue;
                }
                Object array[] = createNode(aux[0], labels.get(aux[0]));
                Node node_a = (Node) array[0];
                String layer_a = (String) array[1];
                array = createNode(aux[1], labels.get(aux[1]));
                Node node_b = (Node) array[0];
                String layer_b = (String) array[1];

                double weight = Double.parseDouble(aux[2]);
                String relations;
                if (relations_name.contains(layer_a + "_" + layer_b)) {
                    relations = layer_a + "_" + layer_b;
                } else if (relations_name.contains(layer_b + "_" + layer_a)) {
                    relations = layer_b + "_" + layer_a;
                } else {
                    relations = layer_a + "_" + layer_b;
                    relations_name.add(relations);
                }
                node_a.add(relations, node_b, weight);
                node_b.add(relations, node_a, weight);
                count++;
                if (count % 100000 == 0) {
                    System.out.println("\t---------- " + count + " lines read");
                }
            }
        }
        System.out.println("Network creation completed!");
    }

    private HashMap<String, String> read_F_file(String fileLabels) throws Exception {
        System.out.println("Starting reading labeled file");
        HashMap<String, String> labels = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(fileLabels));
        String line, vetF = null;
        while ((line = br.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            String v[] = line.trim().split("\t");
            vetF = v[1];
            labels.put(v[0], vetF);
        }
        br.close();
        F_size = vetF.split(",").length;
        System.out.println("Size of the array F: " + F_size);
        return labels;
    }
    
    private Object[] createNode(String nodeID, String label_y) {
        String temp[] = nodeID.split(":");
        if (temp.length > 2) {
            System.out.println("Warning: Badly formatted node. " + nodeID);
        }
        String camada = temp[temp.length - 1];
        Node node = null;
        if (layers.get(camada) != null) {
            node = layers.get(camada).get(nodeID);
        } else {
            layers.put(camada, new HashMap<>());
        }
        if (node == null) {
            node = new Node(nodeID);
            node.F = new double[F_size];
            node.lastF = new double[F_size];
            if (label_y != null) {
                String v[] = label_y.split(",");
                node.Y = new double[F_size];
                for (int i = 0; i < v.length; i++) {
                    double d = Double.parseDouble(v[i]);
                    node.F[i] = d;
                    node.lastF[i] = d;
                    node.Y[i] = d;
                }
            }
            layers.get(camada).put(node.toString(), node);
        }
        return new Object[]{node, camada};
    }

    public double[] createClassVector() {
        return new double[F_size];
    }

    public void saveNetwork(String fileoutput) throws Exception {
        Useful.output(fileoutput, "");
        for (HashMap<String, Node> layer : layers.values()) {
            for (Node node : layer.values()) {
                String t = java.util.Arrays.toString(node.F).replace(" ", "");
                Useful.addToFile(fileoutput, node + "\t" + t.substring(1, t.length() - 1) + "\n");
            }
        }
    }

}
