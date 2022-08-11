package aq.koptev.i.models;

import aq.koptev.i.util.ParameterNetObject;
import aq.koptev.i.util.TypeNetObject;

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
        for(int i = 0; i < data.length; i++) {
            bytes[i] = data[i];
        }
        dataMap.put(param, bytes);
    }

    public void dropData() {
        dataMap.clear();
    }

    public byte[] getData(ParameterNetObject param) {
        Byte[] bytes = dataMap.get(param);
        byte[] bytes2 = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            bytes2[i] = bytes[i];
        }
        return bytes2;
    }

    public TypeNetObject getType() {
        return  typeNetObject;
    }

    public static <T extends Serializable> byte[] getBytes(T obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Serializable> T getObject(byte[] bytes) {
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
