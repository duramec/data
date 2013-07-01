package io.zig.data.fressian;

public final class Fressian {

	static final FressianConverter converter = new FressianConverter();

	public static final byte[] toBytes(Object o) throws Exception {
		return converter.toBytes(o);
	}

	public static final Object toObject(byte[] bytes) throws Exception {
		return converter.toObject(bytes);
	}
}