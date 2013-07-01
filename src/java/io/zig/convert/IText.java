package io.zig.convert;

public interface IText {
	public String toFormat(Object o) throws Exception;

	public Object toObject(CharSequence s) throws Exception;
}