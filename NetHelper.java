package watchbotpinger.util;

import java.security.cert.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;
import watchbotpinger.util.*;
import javax.net.ssl.*;
import java.security.cert.*;

public class NetHelper {

    /**
     * DO NOT USE IN PRODUCTION!!!!
     *
     * This class will simply trust everything that comes along.
     *
     * @author frank
     *
     */
    public static class TrustAllX509TrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                String authType) {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                String authType) {
        }

    }

    private static String myhost = null;

    public static void init(String hostnamehack) throws Exception {
        if (hostnamehack != null) {
            myhost = hostnamehack;
        } else {
            Vector<String> ret = new Vector<String>();
            Util.exec(new String[]{"hostname"}, ret);
            myhost = ret.get(0);

            if (myhost == null || myhost.length() <= 0) {
                throw new Exception("Unknown host!");
            }
        }
        System.setProperty("http.keepAlive", "false");
        //disableSSLVerify();
    }

    public static String getHostName() {
        return myhost;
    }

    private static void disableSSLVerify() throws Exception {
        {
            Log.log("DISABLESSLVERIFY");
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            });
        }
        /*
         *  fix for
         *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
         *       sun.security.validator.ValidatorException:
         *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
         *               unable to find valid certification path to requested target
         */
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            ;
        }

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        /*
         * end of the fix
         */
    }
}
