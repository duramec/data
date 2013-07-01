package io.zig.data.edn;

import static us.bpsm.edn.parser.Parsers.newParseable;
import io.zig.convert.IText;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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

	/**
	 * Method turns a CharSequence into a composite Object representing EDN data
	 * structures.
	 * 
	 * If the input representation is unenclosed in an EDN Map, Set, List,
	 * Vector, etc., place all of the sequence items in a special Collection
	 * type which is very low in the Class hierarchy and should play nice with
	 * Printers and Parsers.
	 */
	@Override
	public Object toObject(CharSequence s) throws Exception {
		Parseable edn = newParseable(s);
		Parser parser = Parsers.newParser(config);
		Object next = parser.nextValue(edn);
		/**
		 * Size set to 1 because most EDN will be be wrapped and will never need
		 * a resize. Do not create a bunch of intermediate garbage in memory
		 * just for the special case of an unenclosed sequence.
		 */
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

	static final Printer.Fn<Pattern> regexPrintFn = new Printer.Fn<Pattern>() {
		@Override
		public void eval(Pattern self, Printer writer) {
			writer.append("#regex\"");
			writer.append(self.pattern());
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
				.put(Pattern.class, regexPrintFn).put(URI.class, uriPrintFn)
				.build();
	}

	static final Protocol<Fn<?>> protocol = createPrinterProtocol();

	static final Tag uriTag = Tag.newTag("uri");

	static final Tag regexTag = Tag.newTag("regex");

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

	static final TagHandler regexTagHandler = new TagHandler() {
		public Object transform(Tag tag, Object value) {
			return Pattern.compile((String) value);
		}
	};

	static final Parser.Config config = Parsers.newParserConfigBuilder()
			.setVectorFactory(createVectorFactory())
			.putTagHandler(uriTag, uriTagHandler)
			.putTagHandler(regexTag, regexTagHandler).build();

}