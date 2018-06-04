package jp.co.soramitsu.sora.util.bencoder;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.ObjectCodec;
import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

class BencodeFactory extends JsonFactory {

  public static final Charset DEFAULT_CHARSET = UTF_8;

  public BencodeFactory() {
    super(null);
  }

  public BencodeFactory(ObjectCodec oc) {
    super(oc);
  }

  public BencodeFactory(BencodeFactory src, ObjectCodec codec) {
    super(src, codec);
  }

  @Override
  public BencodeGenerator createGenerator(OutputStream out) {
    Writer w = new OutputStreamWriter(out, DEFAULT_CHARSET);
    return new BencodeGenerator(w, DEFAULT_CHARSET);
  }

  @Override
  public BencodeGenerator createGenerator(File f, JsonEncoding enc) throws FileNotFoundException {
    OutputStream out = new FileOutputStream(f);
    Charset charset = Charset.forName(enc.getJavaName());
    Writer w = new OutputStreamWriter(out, charset);
    return new BencodeGenerator(w, DEFAULT_CHARSET);
  }

  @Override
  public BencodeGenerator createGenerator(OutputStream out, JsonEncoding enc) {
    Charset charset = Charset.forName(enc.getJavaName());
    Writer w = new OutputStreamWriter(out, charset);
    return new BencodeGenerator(w, DEFAULT_CHARSET);
  }

  @Override
  public BencodeGenerator createGenerator(Writer w) {
    return new BencodeGenerator(w, DEFAULT_CHARSET);
  }

  @Override
  public BencodeGenerator createGenerator(DataOutput out) {
    throw new UnsupportedOperationException("can not create generator from DataOutput");
  }


}
