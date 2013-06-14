package io.zig.data;

import org.fressian.FressianReader;
import org.fressian.Reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.io.StringWriter;
import us.bpsm.edn.printer.Printer;
import us.bpsm.edn.printer.Printer.Fn;
import us.bpsm.edn.printer.Printers;
import us.bpsm.edn.protocols.Protocol;

import java.util.AbstractList;
import java.util.List;
import java.util.Arrays;

final class DelegatingList<E> extends AbstractList<E> {
	final List<E> delegate;

	DelegatingList(List<E> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public E get(int index) {
		return delegate.get(index);
	}

}

public final class FressianToEdn {

	private final static Protocol<Fn<?>> protocol = Printers
			.defaultProtocolBuilder()
			.put(Object[].class, new Printer.Fn<Object[]>() {
				@Override
				public void eval(Object[] self, Printer writer) {
					writer.append('[');
					for (Object o : self) {
						writer.printValue(o);
					}
					writer.append(']');
				}
			}).put(List.class, new Printer.Fn<List<?>>() {
				@Override
				public void eval(List<?> self, Printer writer) {
					writer.append('(');
					for (Object o : self) {
						writer.printValue(o);
					}
					writer.append(')');
				}
			}).build();

	public static final String convert(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Reader reader = new FressianReader(bais);
		Object readObject = reader.readObject();
		StringWriter sw = new StringWriter();
		Printer ew = Printers.newPrinter(protocol, sw);
		ew.printValue(readObject);
		String res = sw.toString();
		return res;
	}

}