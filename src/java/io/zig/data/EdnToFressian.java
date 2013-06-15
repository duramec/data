package io.zig.data;

import org.fressian.handlers.ILookup;
import org.fressian.handlers.WriteHandler;
import org.fressian.FressianWriter;
import org.fressian.Writer;

import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.CollectionBuilder;
import static us.bpsm.edn.parser.Parsers.newParseable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@SuppressWarnings("rawtypes")
public final class EdnToFressian {

	static final Parser.Config config = Parsers.newParserConfigBuilder()
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

	static final Map<String, WriteHandler> map(Object... keyvals) {
		if (keyvals == null) {
			return new HashMap<String, WriteHandler>();
		} else if (keyvals.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Map must have an even number of elements");
		} else {
			Map<String, WriteHandler> m = new HashMap<String, WriteHandler>(
					keyvals.length / 2);
			for (int i = 0; i < keyvals.length; i += 2) {
				m.put((String) keyvals[i], (WriteHandler) keyvals[i + 1]);
			}
			return Collections.unmodifiableMap(m);
		}
	}

	static final WriteHandler keywordHandler = new WriteHandler() {
		public void write(Writer w, Object instance) throws IOException {
			w.writeTag("key", 2);
			Keyword keyWord = (Keyword) instance;
			w.writeString(keyWord.getPrefix());
			w.writeString(keyWord.getName());
		}
	};

	static final WriteHandler symbolHandler = new WriteHandler() {
		public void write(Writer w, Object instance) throws IOException {
			w.writeTag("sym", 2);
			Symbol symbol = (Symbol) instance;
			w.writeString(symbol.getPrefix());
			w.writeString(symbol.getName());
		}
	};

	static final Map<Class, Map<String, WriteHandler>> createHandlers() {
		Map<Class, Map<String, WriteHandler>> handlers = new HashMap<Class, Map<String, WriteHandler>>();
		final String keywordTag = "us.bspm.edn.Keyword";
		final String symbolTag = "us.bpsm.edn.Symbol";
		handlers.put(Keyword.class, map(keywordTag, keywordHandler));
		handlers.put(Symbol.class, map(symbolTag, symbolHandler));
		return Collections.unmodifiableMap(handlers);
	}

	static final ILookup<Class, Map<String, WriteHandler>> createLookup() {

		final Map<Class, Map<String, WriteHandler>> handlers = createHandlers();

		return new ILookup<Class, Map<String, WriteHandler>>() {
			public Map<String, WriteHandler> valAt(Class key) {
				return handlers.get(key);
			}
		};
	}

	static final ILookup<Class, Map<String, WriteHandler>> lookup = createLookup();

	public static final byte[] convert(String text) throws IOException {
		Parseable edn = newParseable(text);
		Parser parser = Parsers.newParser(config);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer fw = new FressianWriter(baos, lookup);
		Object next = parser.nextValue(edn);
		while (next != Parser.END_OF_INPUT) {
			fw.writeObject(next);
			next = parser.nextValue(edn);
		}
		return baos.toByteArray();
	}
}