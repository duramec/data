package io.zig.data.edn;

import static us.bpsm.edn.parser.Parsers.newParseable;
import io.zig.convert.IText;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

import us.bpsm.edn.Tag;
import us.bpsm.edn.parser.CollectionBuilder;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.TagHandler;
import us.bpsm.edn.printer.Printer;
import us.bpsm.edn.printer.Printers;
import us.bpsm.edn.printer.Printer.Fn;
import us.bpsm.edn.protocols.Protocol;

final class EDNConverter implements IText {

	@Override
	public Object toObject(CharSequence s) throws Exception {
		Parseable edn = newParseable(s);
		Parser parser = Parsers.newParser(config);
		Object next = parser.nextValue(edn);
		// size set to 1 because most things will not need a resize
		ArrayList<Object> objects = new ArrayList<Object>(1); 
		while (next != Parser.END_OF_INPUT) {
			objects.add(next);
			next = parser.nextValue(edn);
		}
		if (objects.size() == 1) {
			return objects.get(0);
		} else {
			objects.trimToSize();
			return Collections.unmodifiableCollection(objects);
		}
	}

	@Override
	public String toFormat(Object o) {
		StringWriter sw = new StringWriter();
		Printer ew = Printers.newPrinter(protocol, sw);
		ew.printValue(o);
		return sw.toString();
	}

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
			writer.append("#uri\"");
			writer.append(self.toString());
			writer.append("\"");
		}
	};

	static final Printer.Fn<Collection<?>> collectionPrintFn = new Printer.Fn<Collection<?>>() {
		@Override
		public void eval(Collection<?> self, Printer writer) {
			for (Object o : self) {
				writer.printValue(o);
			}
		}
	};

	static Protocol<Fn<?>> createPrinterProtocol() {
		return Printers.defaultProtocolBuilder()
				.put(Object[].class, vectorPrintFn)
				.put(List.class, listPrintFn)
				.put(Collection.class, collectionPrintFn)
				.put(URI.class, uriPrintFn).build();
	}

	static final Protocol<Fn<?>> protocol = createPrinterProtocol();

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

}