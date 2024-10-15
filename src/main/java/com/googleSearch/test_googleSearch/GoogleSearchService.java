package com.googleSearch.test_googleSearch;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;



/**
 * source:https://stackoverflow.com/questions/40181330/google-search-using-java-programatically
 */
public class GoogleSearchService {

//	private final static String PROXY_HOST = "172.18.100.15"; 
//	private final static Integer PROXY_PORT = new Integer(18717);
	private final static String PROXY_HOST = null; 
	private final static Integer PROXY_PORT = null;
	
	// Saltar el certificado del proxy
	static{
		if (PROXY_HOST!=null && PROXY_PORT!=null) {
			try{
				TrustManager[] trustAllCerts = { new X509TrustManager() {
	
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {}
	
					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)throws CertificateException {}
	
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
				} };
				SSLContext sc = SSLContext.getInstance("SSL");
				
				HostnameVerifier hv = new HostnameVerifier() {
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				};
				sc.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		try {
			GoogleSearchService googleSearchService = new GoogleSearchService();
			googleSearchService.searchGoogle(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    private void searchGoogle(List<String> keywords){
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            HttpTransport httpTransport =  null;
            if (PROXY_HOST!=null && PROXY_PORT!=null) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP,  new InetSocketAddress(PROXY_HOST,PROXY_PORT.intValue()));
                httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
            } else {
            	httpTransport = new NetHttpTransport();
            }
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
            url.put("query", "Shadow of the colossus");
            url.put("limit", "2");
            url.put("indent", "true");
            url.put("key", "AIzaSyC6zUw7hJmErTjs7F3pWh6a_jF6zX6erKM");
            
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            String responseString = httpResponse.parseAsString();
   
//            JSONParser parser = new JSONParser();
//            JSONObject response = (JSONObject) parser.parse(responseString);
//            JSONArray elements = (JSONArray) response.get("itemListElement");
//            for (Object element : elements) {
//              System.out.println(JsonPath.read(element, "$.result.name").toString());
//            }

//            System.out.println(responseString);
            JsonNode node = mapper.readTree(responseString).get("itemListElement").get(0).get("result");
            System.out.println("-----\n" + node + "\n----\n");
            System.out.println(node.get("name"));
            System.out.println(node.get("@type"));
            System.out.println(node.get("detailedDescription").get("articleBody"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}