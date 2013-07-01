package io.zig.data.fressian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Collections;
import java.util.ArrayList;
import org.fressian.FressianReader;
import org.fressian.FressianWriter;
import org.fressian.Reader;
import org.fressian.Writer;


import io.zig.convert.IBinary;

final class FressianConverter implements IBinary {

	static final int FooterToken = 0xCF;

	@Override
	public byte[] toBytes(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer fw = new FressianWriter(baos, Handlers.writeLookup);
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
		Reader reader = new FressianReader(pb, Handlers.readLookup);
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

}