package rede;

import ferramentas.Util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Brucce
 */
public final class Rede implements Serializable {

    private int F_size = 0;
    public static final ArrayList<String> relations_name = new ArrayList<>();
    public final HashMap<String, HashMap<String, No>> camadas = new HashMap<>();

    public static void setRelacoesName(Object[] relacoes) {
        for (Object o : relacoes) {
            relations_name.add(o.toString());
        }
    }

    public Rede(String fileRelacoes, String fileLabels) throws Exception {
        this(new String[]{fileRelacoes}, fileLabels);
    }

    public Rede(String[] filesRelacoes, String fileLabels) throws Exception {
        HashMap<String, String> labels = read_F_file(fileLabels);
        int count = 0;
        System.out.println("Starting reading network");
        for (String layer : filesRelacoes) {
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
                No No_a = (No) array[0];
                String camada_a = (String) array[1];
                array = createNode(aux[1], labels.get(aux[1]));
                No No_b = (No) array[0];
                String camada_b = (String) array[1];

                double peso = Double.parseDouble(aux[2]);
                String relacao;
                if (relations_name.contains(camada_a + "_" + camada_b)) {
                    relacao = camada_a + "_" + camada_b;
                } else if (relations_name.contains(camada_b + "_" + camada_a)) {
                    relacao = camada_b + "_" + camada_a;
                } else {
                    relacao = camada_a + "_" + camada_b;
                    relations_name.add(relacao);
                }
                No_a.add(relacao, No_b, peso);
                No_b.add(relacao, No_a, peso);
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

    private Object[] createNode(String node, String label_y) {
        String temp[] = node.split(":");
        if (temp.length > 2) {
            System.out.println("Warning: Badly formatted node. " + node);
        }
        String camada = temp[temp.length - 1];
        No no = null;
        if (camadas.get(camada) != null) {
            no = camadas.get(camada).get(node);
        } else {
            camadas.put(camada, new HashMap<>());
        }
        if (no == null) {
            no = new No(node);
            no.F = new double[F_size];
            no.lastF = new double[F_size];
            if (label_y != null) {
                String v[] = label_y.split(",");
                no.Y = new double[F_size];
                for (int i = 0; i < v.length; i++) {
                    double d = Double.parseDouble(v[i]);
                    no.F[i] = d;
                    no.lastF[i] = d;
                    no.Y[i] = d;
                }
            }
            camadas.get(camada).put(no.toString(), no);
        }
        return new Object[]{no, camada};
    }

    public double[] criarVetorClasse() {
        return new double[F_size];
    }

    public void salvarRede(String fileoutput) throws Exception {
        Util.output(fileoutput, "");
        for (HashMap<String, No> camada : camadas.values()) {
            for (No no : camada.values()) {
                String t = java.util.Arrays.toString(no.F).replace(" ", "");
                Util.addToFile(fileoutput, no + "\t" + t.substring(1, t.length() - 1) + "\n");
            }
        }
    }

}
