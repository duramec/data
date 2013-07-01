package io.zig.convert;

public interface IBinary {
	public byte[] toBytes(Object o) throws Exception;

	public Object toObject(byte[] bytes) throws Exception;
}