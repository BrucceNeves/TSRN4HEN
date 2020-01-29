package ferramentas;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Brucce
 */
public class Util {

    public static ArrayList<String> read(String file) throws Exception {
        return read(new File(file));
    }

    public static ArrayList<String> read(File file) throws Exception {
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<String> bufOur = new ArrayList<>();
        String Line;
        while ((Line = br.readLine()) != null) {
            bufOur.add(Line.trim());
        }
        br.close();
        return bufOur;
    }
    
    public static <T extends Serializable> T readSerializable(String file) throws IOException {
        return readSerializable(new File(file));
    }

    public static <T extends Serializable> T readSerializable(File file) throws IOException {
        ObjectInputStream objectinputstream = null;
        try {
            objectinputstream = new ObjectInputStream(new FileInputStream(file));
            T mySerializable = (T) objectinputstream.readObject();
            objectinputstream.close();
            return mySerializable;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectinputstream != null) {
                objectinputstream.close();
            }
        }
        return null;
    }
    
    public static <T extends Externalizable> T readExternalizable(String file) throws IOException {
        return readSerializable(new File(file));
    }

    public static <T extends Externalizable> T readExternalizable(File file) throws IOException {
        ObjectInputStream objectinputstream = null;
        try {
            objectinputstream = new ObjectInputStream(new FileInputStream(file));
            T mySerializable = (T) objectinputstream.readObject();
            objectinputstream.close();
            return mySerializable;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectinputstream != null) {
                objectinputstream.close();
            }
        }
        return null;
    }

    public static String readInline(String file) throws Exception {
        return readInline(new File(file));
    }

    public static String readInline(File file) throws Exception {
        if (!file.exists()) {
            return null;
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer bufOut = new StringBuffer();
        String Line;
        while ((Line = br.readLine()) != null) {
            bufOut.append(Line.trim() + "\n");
        }
        br.close();
        return bufOut.toString();
    }

    public static void output(String file, String content) throws Exception {
        output(new File(file), content);
    }

    public static void output(File file, String content) throws Exception {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content.getBytes());
        fos.close();
    }

    public static void output(String file, Serializable object) throws Exception {
        output(new File(file), object);
    }

    public static void output(File file, Serializable object) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static void addToFile(String file, String content) throws Exception {
        addToFile(new File(file), content);
    }

    public static void addToFile(File file, String content) throws Exception {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(content.getBytes());
        fos.close();
    }
}
