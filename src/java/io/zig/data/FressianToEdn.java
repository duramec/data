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

public final class FressianToEdn {
	
	private static Protocol<Fn<?>> fns = Printers.defaultProtocolBuilder().build();
	
	public static final String convert(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Reader reader = new FressianReader(bais);
        Object readObject = reader.readObject();
        StringWriter writer = new StringWriter();
        Printer printer = Printers.newPrinter(fns, writer);
        printer.printValue(readObject);
        return writer.toString();
	}
	
}