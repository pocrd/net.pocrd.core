package net.pocrd.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class CDataString {
    public String desc;
    @XmlJavaTypeAdapter(CDataAdapter.class)
    public String value;

    public CDataString(String str, String des) {
        value = str;
        desc = des;
    }

    public static class CDataAdapter extends XmlAdapter<String, String> {
        @Override
        public String marshal(String str) throws Exception {
            return "<![CDATA[" + str + "]]>";
        }

        @Override
        public String unmarshal(String str) throws Exception {
            return str;
        }
    }
}
