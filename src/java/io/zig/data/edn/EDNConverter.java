package io.zig.data.edn;

import static us.bpsm.edn.parser.Parsers.newParseable;
import io.zig.convert.IText;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.printer.Printer;
import us.bpsm.edn.printer.Printers;

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
		Parser parser = Parsers.newParser(Handlers.javaDefaultParser);
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
		Printer ew = Printers.newPrinter(Handlers.javaDefaultPrinter,
				sw);
		ew.printValue(o);
		return sw.toString();
	}

}