package io.zig.data;

import org.fressian.FressianWriter;
import org.fressian.Tagged;
import org.fressian.Writer;

import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.CollectionBuilder;
import static us.bpsm.edn.parser.Parsers.newParseable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public final class EdnToFressian {

	private static final Parser.Config config = Parsers
			.newParserConfigBuilder()
			.setVectorFactory(new CollectionBuilder.Factory() {
				public CollectionBuilder builder() {
					return new CollectionBuilder() {
						ArrayList<Object> s = new ArrayList<Object>();

						public void add(Object o) {
							s.add(o);
						}

						public Object build() {
							return s.toArray();
						}

					};
				}
			}).build();

	static boolean isVector(Object o) {
		return false;
	}

	public static final byte[] convert(String text) throws IOException {
		Parseable edn = newParseable(text);
		Parser parser = Parsers.newParser(config);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer fw = new FressianWriter(baos);
		Object next = parser.nextValue(edn);
		while (next != Parser.END_OF_INPUT) {
			fw.writeObject(next);
			next = parser.nextValue(edn);
		}
		return baos.toByteArray();
	}
}