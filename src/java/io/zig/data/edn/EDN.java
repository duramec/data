package io.zig.data.edn;

public final class EDN {

	static final EDNConverter converter = new EDNConverter();

	public static final Object toObject(String s) throws Exception {
		return converter.toObject(s);
	}

	public static final String toFormat(Object o) {
		return converter.toFormat(o);
	}
}
