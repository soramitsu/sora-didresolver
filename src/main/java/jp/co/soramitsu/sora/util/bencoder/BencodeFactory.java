package jp.co.soramitsu.sora.util.bencoder;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

class BencodeFactory extends JsonFactory {

  public static final Charset DEFAULT_CHARSET = UTF_8;

  public BencodeFactory() {
    super(null);
  }

  @Override
  public BencodeGenerator createGenerator(OutputStream out) {
    throw new UnsupportedOperationException("bencoder: unsupported createGenerator(OutputStream)");
  }

  @Override
  public BencodeGenerator createGenerator(File f, JsonEncoding enc) throws FileNotFoundException {
    throw new UnsupportedOperationException("bencoder: unsupported createGenerator(file)");
  }

  @Override
  public BencodeGenerator createGenerator(OutputStream out, JsonEncoding enc) {
    throw new UnsupportedOperationException(
        "bencoder: unsupported createGenerator(OutputStream, JsonEncoding)");
  }

  @Override
  public BencodeGenerator createGenerator(Writer w) {
    return new BencodeGenerator(w, DEFAULT_CHARSET);
  }

  @Override
  public BencodeGenerator createGenerator(DataOutput out) {
    throw new UnsupportedOperationException("bencoder: can not create generator from DataOutput");
  }


}
