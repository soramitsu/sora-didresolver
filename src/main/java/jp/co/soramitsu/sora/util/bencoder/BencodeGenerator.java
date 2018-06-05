package jp.co.soramitsu.sora.util.bencoder;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import javax.validation.constraints.NotNull;

class BencodeGenerator extends JsonGenerator {

  private Writer writer;
  private ObjectCodec codec;
  private Charset charset;

  public BencodeGenerator(@NotNull Writer w, @NotNull Charset charset) {
    this.writer = w;
    this.charset = charset;
  }

  @Override
  public JsonGenerator setCodec(ObjectCodec oc) {
    codec = oc;
    return this;
  }

  @Override
  public ObjectCodec getCodec() {
    return codec;
  }

  @Override
  public Version version() {
    throw new UnsupportedOperationException("can not call version()");
  }

  @Override
  public JsonGenerator enable(Feature f) {
    throw new UnsupportedOperationException("can not enable feature f: " + f.toString());

  }

  @Override
  public JsonGenerator disable(Feature f) {
    throw new UnsupportedOperationException("can not disable feature f: " + f.toString());
  }

  @Override
  public boolean isEnabled(Feature f) {
    throw new UnsupportedOperationException("can not execute isEnabled()");

  }

  @Override
  public int getFeatureMask() {
    throw new UnsupportedOperationException("can not execute getFeatureMask()");

  }

  @Override
  public JsonGenerator setFeatureMask(int values) {
    throw new UnsupportedOperationException(
        "can not execute setFeatureMask(" + values + ")");

  }

  @Override
  public JsonGenerator useDefaultPrettyPrinter() {
    throw new UnsupportedOperationException("can not execute useDefaultPrettyPrinter()");
  }

  @Override
  public void writeStartArray() throws IOException {
    writer.write('l');
  }

  @Override
  public void writeEndArray() throws IOException {
    writer.write('e');
  }

  @Override
  public void writeStartObject() throws IOException {
    writer.write('d');
  }

  @Override
  public void writeEndObject() throws IOException {
    writer.write('e');
  }

  @Override
  public void writeFieldName(String name) throws IOException {
    writeString(name);
  }

  @Override
  public void writeFieldName(SerializableString name) throws IOException {
    write(name.getValue());
  }

  @Override
  public void writeString(String text) throws IOException {
    write(text);
  }

  @Override
  public void writeString(char[] text, int offset, int len) throws IOException {
    writeRawValue(text, offset, len);
  }

  @Override
  public void writeString(SerializableString text) throws IOException {
    write(text.getValue());
  }

  @Override
  public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
    byte[] substring = Arrays.copyOfRange(text, offset, length);
    write(substring);
  }

  @Override
  public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
    byte[] substring = Arrays.copyOfRange(text, offset, length);
    write(substring);
  }

  @Override
  public void writeRaw(String text) throws IOException {
    write(text);
  }

  @Override
  public void writeRaw(String text, int offset, int len) throws IOException {
    writeRawValue(text, offset, len);
  }

  @Override
  public void writeRaw(char[] text, int offset, int len) throws IOException {
    writeRawValue(text, offset, len);
  }

  @Override
  public void writeRaw(char c) throws IOException {
    write(c);
  }

  @Override
  public void writeRawValue(String text) throws IOException {
    write(text);
  }

  @Override
  public void writeRawValue(String text, int offset, int len) throws IOException {
    String substring = text.substring(offset, len);
    write(substring);
  }

  @Override
  public void writeRawValue(char[] text, int offset, int len) throws IOException {
    char[] substring = Arrays.copyOfRange(text, offset, len);
    write(substring);
  }

  @Override
  public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
    writeRaw(bv.encode(data));
  }

  @Override
  public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
    throw new UnsupportedOperationException("can not execute writeBinary()");
  }

  @Override
  public void writeNumber(int v) throws IOException {
    write(v);
  }

  @Override
  public void writeNumber(long v) throws IOException {
    write(v);
  }

  @Override
  public void writeNumber(BigInteger v) throws IOException {
    write(String.valueOf(v));
  }

  @Override
  public void writeNumber(double v) throws IOException {
    write(v);
  }

  @Override
  public void writeNumber(float v) throws IOException {
    write(v);
  }

  @Override
  public void writeNumber(BigDecimal v) throws IOException {
    write(String.valueOf(v));
  }

  @Override
  public void writeNumber(String encodedValue) throws IOException {
    write(encodedValue);
  }

  @Override
  public void writeBoolean(boolean state) throws IOException {
    writer.write(state ? 'B' : 'b');
  }

  @Override
  public void writeNull() throws IOException {
    writer.write('N');
  }

  @Override
  public void writeObject(Object pojo) throws IOException {
    throw new UnsupportedOperationException("can not write Object");
  }

  @Override
  public void writeTree(TreeNode rootNode) throws IOException {
    throw new UnsupportedOperationException("can not write TreeNode");
  }

  @Override
  public JsonStreamContext getOutputContext() {
    // should be null, otherwise json processor tries to disable some feature and throws exception
    return null;
  }

  @Override
  public void flush() throws IOException {
    writer.flush();
  }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }

  private void write(final Number number) throws IOException {
    writer.write('i');
    writer.write(String.valueOf(number));
    writer.write('e');
  }

  private void write(final char chr) throws IOException {
    writer.write('1');
    writer.write(':');
    writer.write(chr);
  }

  private void write(final String s) throws IOException {
    write(s.getBytes(charset));
  }

  private void write(final char[] c) throws IOException {
    byte[] bytes = charset.encode(CharBuffer.wrap(c)).array();
    write(bytes);
  }

  private void write(final byte[] b) throws IOException {
    writer.write(String.valueOf(b.length));
    writer.write(':');
    writer.write(new String(b, charset));
  }
}
