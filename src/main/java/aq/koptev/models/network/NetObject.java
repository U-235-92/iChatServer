package aq.koptev.models.network;

import aq.koptev.util.ParameterNetObject;
import aq.koptev.util.TypeNetObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class NetObject implements Serializable {

    private TypeNetObject typeNetObject;
    private Map<ParameterNetObject, Byte[]> dataMap;

    public NetObject(TypeNetObject typeNetObject) {
        this.typeNetObject = typeNetObject;
        dataMap = new HashMap<>();
    }

    public void putData(ParameterNetObject param, byte[] data) {
        Byte[] bytes = new Byte[data.length];
        System.arraycopy(data, 0, bytes, 0, data.length);
        dataMap.put(param, bytes);
    }

    public void dropData() {
        dataMap.clear();
    }

    public byte[] getData(ParameterNetObject param) {
        Byte[] bytes = dataMap.get(param);
        byte[] bytes2 = new byte[bytes.length];
        System.arraycopy(bytes, 0, bytes2, 0, bytes.length);
        return bytes2;
    }

    public TypeNetObject getType() {
        return  typeNetObject;
    }

    public static <T> byte[] getBytes(T obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getObject(byte[] bytes) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
