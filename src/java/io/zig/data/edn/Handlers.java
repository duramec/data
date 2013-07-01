package io.zig.data.edn;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import us.bpsm.edn.Tag;
import us.bpsm.edn.parser.CollectionBuilder;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.TagHandler;
import us.bpsm.edn.printer.Printer;
import us.bpsm.edn.printer.Printers;
import us.bpsm.edn.printer.Printer.Fn;
import us.bpsm.edn.protocols.Protocol;

public final class Handlers {

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

	static Protocol.Builder<Fn<?>> defaultPrinterBuilder() {
		return Printers.defaultProtocolBuilder()
				.put(Object[].class, vectorPrintFn)
				.put(List.class, listPrintFn)
				.put(Collection.class, collectionPrintFn)
				.put(URI.class, uriPrintFn).put(Pattern.class, regexPrintFn);
	}

	static final Protocol<Fn<?>> javaDefaultPrinter = defaultPrinterBuilder()
			.build();

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

	static Parser.Config.Builder javaDefaultParserBuilder() {
		return Parsers.newParserConfigBuilder()
				.setVectorFactory(createVectorFactory())
				.putTagHandler(uriTag, uriTagHandler)
				.putTagHandler(regexTag, regexTagHandler);
	}

	static final Parser.Config javaDefaultParser = javaDefaultParserBuilder()
			.build();
}