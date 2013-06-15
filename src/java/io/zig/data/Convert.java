package io.zig.data;

import java.io.IOException;

public final class Convert {
	
	public final static String toEDN(byte[] bytes) throws IOException {
		return FressianToEdn.convert(bytes);
	}
	
	public final static byte[] toBytes(String edn) throws IOException {
		return EdnToFressian.convert(edn);
	}
	
}