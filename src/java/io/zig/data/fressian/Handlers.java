package io.zig.data.fressian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fressian.Reader;
import org.fressian.Writer;
import org.fressian.handlers.ILookup;
import org.fressian.handlers.ReadHandler;
import org.fressian.handlers.WriteHandler;

import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;

@SuppressWarnings("rawtypes")
public final class Handlers {

	private static final Class<?> UnenclosedSequenceClass = Collections
			.unmodifiableCollection(new ArrayList<Object>()).getClass();

	public static final Map<Object, ReadHandler> javaDefaultReadHandlers() {

		Map<Object, ReadHandler> handlers = new HashMap<Object, ReadHandler>();

		handlers.put("key", new ReadHandler() {
			public Object read(Reader r, Object tag, int componentCount)
					throws IOException {
				assert (componentCount == 2);
				return Keyword.newKeyword((String) r.readObject(),
						(String) r.readObject());
			}
		});

		handlers.put("sym", new ReadHandler() {
			public Object read(Reader r, Object tag, int componentCount)
					throws IOException {
				assert (componentCount == 2);
				return Symbol.newSymbol((String) r.readObject(),
						(String) r.readObject());
			}
		});

		handlers.put("char", new ReadHandler() {
			public Object read(Reader r, Object tag, int componentCount)
					throws IOException {
				assert (componentCount == 1);
				Integer codePoint = ((Long) (r.readInt())).intValue();
				return (Character) Character.toChars(codePoint)[0];
			}
		});

		return Collections.unmodifiableMap(handlers);
	}

	public static Map<Class, Map<String, WriteHandler>> javaDefaultWriteHandlers() {
		Map<Class, Map<String, WriteHandler>> handlers = new HashMap<Class, Map<String, WriteHandler>>();

		handlers.put(Keyword.class, map("key", new WriteHandler() {
			public void write(Writer w, Object instance) throws IOException {
				w.writeTag("key", 2);
				Keyword keyWord = (Keyword) instance;
				w.writeString(keyWord.getPrefix());
				w.writeString(keyWord.getName());
			}
		}));

		handlers.put(Symbol.class, map("sym", new WriteHandler() {
			public void write(Writer w, Object instance) throws IOException {
				w.writeTag("sym", 2);
				Symbol symbol = (Symbol) instance;
				w.writeString(symbol.getPrefix());
				w.writeString(symbol.getName());
			}
		}));

		handlers.put(Character.class, map("char", new WriteHandler() {
			public void write(Writer w, Object instance) throws IOException {
				w.writeTag("char", 1);
				char c = ((Character) instance).charValue();
				w.writeInt(c);
			}
		}));

		handlers.put(UnenclosedSequenceClass, map("seq", new WriteHandler() {
			public void write(Writer w, Object instance) throws IOException {
				@SuppressWarnings("unchecked")
				Collection<Object> col = (Collection) instance;
				for (Object o : col) {
					w.writeObject(o);
				}
			}
		}));

		return Collections.unmodifiableMap(handlers);
	};

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

	static final ILookup<Class, Map<String, WriteHandler>> writeLookup = new ILookup<Class, Map<String, WriteHandler>>() {
		final Map<Class, Map<String, WriteHandler>> handlers = Handlers
				.javaDefaultWriteHandlers();

		public Map<String, WriteHandler> valAt(Class key) {
			return handlers.get(key);
		}
	};

	static final ILookup<Object, ReadHandler> readLookup = new ILookup<Object, ReadHandler>() {
		final Map<Object, ReadHandler> handlers = Handlers
				.javaDefaultReadHandlers();

		public ReadHandler valAt(Object key) {
			return handlers.get(key);
		}
	};

}