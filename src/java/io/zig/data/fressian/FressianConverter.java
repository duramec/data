package io.zig.data.fressian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

import org.fressian.handlers.ILookup;
import org.fressian.handlers.ReadHandler;
import org.fressian.handlers.WriteHandler;
import org.fressian.FressianReader;
import org.fressian.FressianWriter;
import org.fressian.Reader;
import org.fressian.Writer;

import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;

import io.zig.convert.IBinary;

@SuppressWarnings("rawtypes")
final class FressianConverter implements IBinary {

	private static final Class UnenclosedSequenceClass = Collections
			.unmodifiableCollection(new ArrayList<Object>()).getClass();

	@Override
	public byte[] toBytes(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer fw = new FressianWriter(baos, writeLookup);
		fw.writeObject(o);
		fw.writeFooter();
		return baos.toByteArray();
	}

	/**
	 * Method turns a byte array of Fressian objects into EDN.
	 * 
	 * If the input is an unenclosed sequence, return it in an unmodifiable
	 * Collection which is very low in the Class hierarchy and should play nice
	 * with Printers and Parsers.
	 */
	@Override
	public Object toObject(byte[] bytes) throws IOException {
		/**
		 * Size set to 1 because most objects will be be wrapped and will never
		 * need a resize. Do not create a bunch of intermediate garbage in
		 * memory just for the special case of an unenclosed sequence.
		 */
		ArrayList<Object> objects = new ArrayList<Object>(1);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		PushbackInputStream pb = new PushbackInputStream(bais);
		Reader reader = new FressianReader(pb, readLookup);
		int token = 0;
		boolean done = false;
		while (!done) {
			objects.add(reader.readObject());
			token = pb.read();
			if (token == FooterToken || token == -1) {
				done = true;
			} else {
				pb.unread(token);
			}
		}
		if (objects.size() == 1) {
			return objects.get(0);
		} else {
			objects.trimToSize();
			return Collections.unmodifiableCollection(objects);
		}
	}

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

	static Map<Class, Map<String, WriteHandler>> createWriteHandlers() {
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

	static final ILookup<Class, Map<String, WriteHandler>> writeLookup = new ILookup<Class, Map<String, WriteHandler>>() {
		final Map<Class, Map<String, WriteHandler>> handlers = createWriteHandlers();

		public Map<String, WriteHandler> valAt(Class key) {
			return handlers.get(key);
		}
	};

	static final int FooterToken = 0xCF;

	static Map<Object, ReadHandler> createReadHandlers() {

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

	static final ILookup<Object, ReadHandler> readLookup = new ILookup<Object, ReadHandler>() {
		final Map<Object, ReadHandler> handlers = createReadHandlers();

		public ReadHandler valAt(Object key) {
			return handlers.get(key);
		}
	};

}