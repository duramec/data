package io.zig.data;

import org.fressian.FressianWriter;
import org.fressian.Writer;

import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.Parseable;
import static us.bpsm.edn.parser.Parsers.defaultConfiguration;
import static us.bpsm.edn.parser.Parsers.newParseable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class EdnToFressian {

	private static final Parser parser = Parsers
			.newParser(defaultConfiguration());

	public static final byte[] convert(CharSequence text) throws IOException {
		Parseable edn = newParseable(text);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new FressianWriter(baos);
		Object next = parser.nextValue(edn);
		while (next != Parser.END_OF_INPUT) {
			writer.writeObject(next);
			next = parser.nextValue(edn);
		}
		return baos.toByteArray();
	}

}