package io.zig.data;

import org.fressian.handlers.ILookup;
import org.fressian.FressianReader;
import org.fressian.Reader;
import org.fressian.handlers.ReadHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.io.StringWriter;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.printer.Printer;
import us.bpsm.edn.printer.Printer.Fn;
import us.bpsm.edn.printer.Printers;
import us.bpsm.edn.protocols.Protocol;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.net.URI;

public final class FressianToEdn {

	static final Printer.Fn<Object[]> vectorPrintFn = new Printer.Fn<Object[]>() {
		@Override
		public void eval(Object[] self, Printer writer) {
			writer.append('[');
			for (Object o : self) {
				writer.printValue(o);
			}
			writer.append(']');
		}
	};

	static final Printer.Fn<List<?>> listPrintFn = new Printer.Fn<List<?>>() {
		@Override
		public void eval(List<?> self, Printer writer) {
			writer.append('(');
			for (Object o : self) {
				writer.printValue(o);
			}
			writer.append(')');
		}
	};

	static final Printer.Fn<URI> uriPrintFn = new Printer.Fn<URI>() {
		@Override
		public void eval(URI self, Printer writer) {
			writer.append("#uri \"");
			writer.append(self.toString());
			writer.append("\"");
		}
	};

	static final Protocol<Fn<?>> protocol = Printers.defaultProtocolBuilder()
			.put(Object[].class, vectorPrintFn)
			.put(List.class, listPrintFn)
			.put(URI.class, uriPrintFn).build();

	static final ReadHandler keywordHandler = new ReadHandler() {
		public Object read(Reader r, Object tag, int componentCount)
				throws IOException {
			assert (componentCount == 2);
			return Keyword.newKeyword((String) r.readObject(),
					(String) r.readObject());
		}
	};

	static final ReadHandler symbolHandler = new ReadHandler() {
		public Object read(Reader r, Object tag, int componentCount)
				throws IOException {
			assert (componentCount == 2);
			return Symbol.newSymbol((String) r.readObject(),
					(String) r.readObject());
		}
	};

	static final ReadHandler characterHandler = new ReadHandler() {
		public Object read(Reader r, Object tag, int componentCount)
				throws IOException {
			assert (componentCount == 1);
			Integer codePoint = ((Long) (r.readInt())).intValue();
			return (Character) Character.toChars(codePoint)[0];
		}
	};

	static final Map<Object, ReadHandler> createHandlers() {
		Map<Object, ReadHandler> handlers = new HashMap<Object, ReadHandler>();
		final String keywordTag = "key";
		final String symbolTag = "sym";
		final String characterTag = "char";
		handlers.put(keywordTag, keywordHandler);
		handlers.put(symbolTag, symbolHandler);
		handlers.put(characterTag, characterHandler);
		return Collections.unmodifiableMap(handlers);
	}

	static final ILookup<Object, ReadHandler> lookup = new ILookup<Object, ReadHandler>() {
		final Map<Object, ReadHandler> handlers = createHandlers();

		public ReadHandler valAt(Object key) {
			return handlers.get(key);
		}
	};

	public static final String convert(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Reader reader = new FressianReader(bais, lookup);
		Object readObject = reader.readObject();
		StringWriter sw = new StringWriter();
		Printer ew = Printers.newPrinter(protocol, sw);
		ew.printValue(readObject);
		return sw.toString();
	}

}