package de.hpi.companies.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class UnclosableObjectOutputStreamWrapper extends ObjectOutputStream {

	private ObjectOutputStream real;

	public UnclosableObjectOutputStreamWrapper(ObjectOutputStream real) throws IOException {
		super();
		this.real=real;
	}

	public int hashCode() {
		return real.hashCode();
	}

	public boolean equals(Object obj) {
		return real.equals(obj);
	}

	public String toString() {
		return real.toString();
	}

	public void useProtocolVersion(int version) throws IOException {
		real.useProtocolVersion(version);
	}

	public void writeUnshared(Object obj) throws IOException {
		real.writeUnshared(obj);
	}

	public void defaultWriteObject() throws IOException {
		real.defaultWriteObject();
	}

	public PutField putFields() throws IOException {
		return real.putFields();
	}

	public void writeFields() throws IOException {
		real.writeFields();
	}

	public void reset() throws IOException {
		real.reset();
	}

	public void write(int val) throws IOException {
		real.write(val);
	}

	public void write(byte[] buf) throws IOException {
		real.write(buf);
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		real.write(buf, off, len);
	}

	public void flush() throws IOException {
		real.flush();
	}

	public void close() throws IOException {}

	public void writeBoolean(boolean val) throws IOException {
		real.writeBoolean(val);
	}

	public void writeByte(int val) throws IOException {
		real.writeByte(val);
	}

	public void writeShort(int val) throws IOException {
		real.writeShort(val);
	}

	public void writeChar(int val) throws IOException {
		real.writeChar(val);
	}

	public void writeInt(int val) throws IOException {
		real.writeInt(val);
	}

	public void writeLong(long val) throws IOException {
		real.writeLong(val);
	}

	public void writeFloat(float val) throws IOException {
		real.writeFloat(val);
	}

	public void writeDouble(double val) throws IOException {
		real.writeDouble(val);
	}

	public void writeBytes(String str) throws IOException {
		real.writeBytes(str);
	}

	public void writeChars(String str) throws IOException {
		real.writeChars(str);
	}

	public void writeUTF(String str) throws IOException {
		real.writeUTF(str);
	}
	
	

}
