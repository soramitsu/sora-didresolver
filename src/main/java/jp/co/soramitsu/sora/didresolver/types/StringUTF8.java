package jp.co.soramitsu.sora.didresolver.types;

import java.io.Serializable;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Comparator;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class StringUTF8 implements CharSequence, Comparator<StringUTF8>, Serializable {
    private String _s;

    public StringUTF8(String s){
        byte[] b;
        try{
            b = s.getBytes(UTF_8); // throws
            _s = new String(b, UTF_8);
        } catch(UnsupportedCharsetException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public StringUTF8(byte[] b){
        _s = new String(b, UTF_8);
    }

    @Override
    public int length() {
        return _s.length();
    }

    @Override
    public char charAt(int index) {
        return _s.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return _s.subSequence(start, end);
    }

    @Override
    public int compare(StringUTF8 o1, StringUTF8 o2) {
        return o1._s.compareTo(o2._s);
    }
}
