package io.zig.data;

import java.util.List;
import java.io.IOException;

public final class Convert {

	public final static String fressianToEdn(byte[] bytes) throws IOException {
		return Deserialize.toEdnFromFressian(bytes);
	}

	public final static Object fressianToObjects(byte[] bytes)
			throws IOException {
		return Deserialize.toObjectsFromFressian(bytes);
	}

	public final static String objectsToEdn(List<Object> objects)
			throws IOException {
		return Deserialize.toEdnFromObjects(objects);
	}

	public final static byte[] ednToFressian(String edn) throws IOException {
		return Serialize.toFressianFromEdn(edn);
	}

	public final static List<Object> ednToObjects(String edn)
			throws IOException {
		return Serialize.toObjectsFromEdn(edn);
	}

	public final static byte[] objectsToFressian(List<Object> objects) throws IOException {
		return Serialize.toFressianFromObjects(objects);
	}
}