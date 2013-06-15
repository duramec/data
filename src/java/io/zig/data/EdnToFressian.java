package io.zig.data;

import org.fressian.handlers.ILookup;
import org.fressian.handlers.WriteHandler;
import org.fressian.FressianWriter;
import org.fressian.Writer;

import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.TagHandler;
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

import us.bpsm.edn.Tag;
import java.net.URI;

@SuppressWarnings("rawtypes")
final class EdnToFressian {

	static final Tag uriTag = Tag.newTag("uri");

	static final CollectionBuilder.Factory createVectorFactory() {
		return new CollectionBuilder.Factory() {
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
		};
	}

	static final TagHandler uriTagHandler = new TagHandler() {
		public Object transform(Tag tag, Object value) {
			return URI.create((String) value);
		}
	};

	static final Parser.Config config = Parsers.newParserConfigBuilder()
			.setVectorFactory(createVectorFactory())
			.putTagHandler(uriTag, uriTagHandler).build();

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

	static Map<Class, Map<String, WriteHandler>> createHandlers() {
		Map<Class, Map<String, WriteHandler>> handlers = new HashMap<Class, Map<String, WriteHandler>>();

		handlers.put(Keyword.class,
				map("us.bspm.edn.Keyword", new WriteHandler() {
					public void write(Writer w, Object instance)
							throws IOException {
						w.writeTag("key", 2);
						Keyword keyWord = (Keyword) instance;
						w.writeString(keyWord.getPrefix());
						w.writeString(keyWord.getName());
					}
				}));

		handlers.put(Symbol.class,
				map("us.bpsm.edn.Symbol", new WriteHandler() {
					public void write(Writer w, Object instance)
							throws IOException {
						w.writeTag("sym", 2);
						Symbol symbol = (Symbol) instance;
						w.writeString(symbol.getPrefix());
						w.writeString(symbol.getName());
					}
				}));

		handlers.put(Character.class,
				map("java.lang.Character", new WriteHandler() {
					public void write(Writer w, Object instance)
							throws IOException {
						w.writeTag("char", 1);
						char c = ((Character) instance).charValue();
						w.writeInt(c);
					}
				}));

		return Collections.unmodifiableMap(handlers);
	};

	static final ILookup<Class, Map<String, WriteHandler>> lookup = new ILookup<Class, Map<String, WriteHandler>>() {
		final Map<Class, Map<String, WriteHandler>> handlers = createHandlers();

		public Map<String, WriteHandler> valAt(Class key) {
			return handlers.get(key);
		}
	};

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