package com.business.support;


import org.junit.Test;

import java.util.Base64;

public class Test2 {

    @Test
    public void test1() {
        String oo00ox0o00x00xo0ox0xx2 = oo00ox0o00x00xo0ox0xx("OyssaSsGARM5MCQsaBcAFj00KSgoGksuPSgkNy4MCwMVJS8mIQYXPyA=", "XDAGFcez", "##Bl*#mP");
        String oo00ox0o00x00xo0ox0xx3 = oo00ox0o00x00xo0ox0xx("Jy0PKgouF0AyJgc9FS8cAD9tJgsMKicLKiYbMAopCiMnLQo/ADU=", "FCkXeGsn", "qhoOvipC");
        String oo00ox0o00x00xo0ox0xx4 = oo00ox0o00x00xo0ox0xx("RTsHKwYOAGZUMBE0ABQXIUs7TQssJiAXdB0sFyw4NxxlASY=", "$UcYigdH", "nrpemPaw");
        System.out.println("1=" + oo00ox0o00x00xo0ox0xx2 + ",2=" + oo00ox0o00x00xo0ox0xx3 + ",3=" + oo00ox0o00x00xo0ox0xx4);
    }



    public static String oo00ox0o00x00xo0ox0xx(String str, String str2, String str3) {
        return xxo0o0ox0oxxoo(str, str2, str3);
    }

    private static String xxo0o0ox0oxxoo(String str, String str2, String str3) {
        byte[] bytes = str2.getBytes();
        byte[] decode = Base64.getDecoder().decode(str);
        if (decode == null) {
            return oo00ox0o00x00xo0ox0xx("", "ad*#U&SI", "aZhHfHo$");
        }
        byte[] bArr = new byte[decode.length];
        for (int i = 0; i < decode.length; i++) {
            bArr[i] = (byte) (decode[i] ^ bytes[i % bytes.length]);
        }
        return new String(bArr);
    }
}
