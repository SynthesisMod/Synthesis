package com.luna.synthesis.utils;

import java.util.*;

// import javax.net.ssl.*;

// import java.io.InputStream;
// import java.net.URL;
// import java.security.KeyStore;

public class Pronouns {
    public static Dictionary<String, String> dict = new Hashtable<String, String>();
    public static boolean dictionaryFilled = false;

    // public InputStream sslConnector(String urlParam) throws Exception {
    //     KeyStore ks = KeyStore.getInstance("jks");
    //     ks.load(getClass().getResourceAsStream("/yourkeystore.jks"), "yourkeystorepassword".toCharArray());
    //     SSLContext ctx = SSLContext.getInstance("TLS");
    //     KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    //     TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    //     kmf.init(ks, null);
    //     tmf.init(ks);
    //     ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    //     URL url = new URL(urlParam);
    //     HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    //     con.setDoOutput(true);
    //     con.setRequestProperty("User-Agent", "Synthesis-NONCANON");
    //     con.setRequestProperty("Accept", "application/json");
    //     con.setRequestProperty("Method", "GET");
    //     con.setSSLSocketFactory(ctx.getSocketFactory());
    //     return con.getInputStream();
    // }
    
    public static void doTheThingWithPronouns() {
        if (dictionaryFilled) {
            return;
        } else {
            dict.put("hh", "he/him");
            dict.put("hi", "he/it");
            dict.put("hs", "he/she");
            dict.put("ht", "he/they");
            dict.put("ih", "it/him");
            dict.put("ii", "it/its");
            dict.put("is", "it/she");
            dict.put("it", "it/they");
            dict.put("shh", "she/him");
            dict.put("sh", "she/her");
            dict.put("si", "she/it");
            dict.put("st", "she/they");
            dict.put("th", "they/he");
            dict.put("ti", "they/it");
            dict.put("ts", "they/she");
            dict.put("tt", "they/them");
            dict.put("any", "any");
            dict.put("other", "other");
            dict.put("ask", "ask");
            dict.put("avoid", "avoid");
            dict.put("unspecified", "unspecified");
            dictionaryFilled = true;
        }
    }
}
