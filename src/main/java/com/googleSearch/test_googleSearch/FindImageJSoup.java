package com.googleSearch.test_googleSearch;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FindImageJSoup {

	private static Log logger = Log.getInstance().getLogger();

	private String searchName = null;
    
	// Saltar el certificado del proxy
	static{
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
	
	public FindImageJSoup(String searchName) {
		this.searchName = searchName;
	}

	public ArrayList<FindResult> findImage(String question, String questionMods, String ua, int numResults, Integer width, Integer height, 
			Float sizeMargin, Integer widthMargin, Integer heightMargin) {
		return findImage(question, questionMods, ua, numResults, width, height, sizeMargin, widthMargin, heightMargin, null,null);
	}

	public ArrayList<FindResult> findImage(String question, String questionMods, String ua, int numResults, Integer width, Integer height, 
			Float sizeMargin, Integer widthMargin, Integer heightMargin, String proxyHost, Integer proxyPort) {
		
		ArrayList<FindResult> findResults = null;
		String googleUrl=null;

		try {
			String params = new String();
			if ((width!=null && height!=null)&&(sizeMargin==null||sizeMargin.floatValue()==0)&&(widthMargin==null&&heightMargin==null)) {
				params+="&tbs=isz:ex";
				if (width!=null) {
					params+=",iszw:"+width;
				}
				if (height!=null) {
					params+=",iszh:"+height;
				}
			}
			
			Float aspectRatio = null;
			if (width!=null && height!=null) {
				aspectRatio=new Float(width)/new Float(height);
			}
			
						
			if (question!=null) {
				question=question.replaceAll(" ", "+");
			}
			if (questionMods!=null) {
				questionMods=questionMods.replaceAll(" ", "+");
			}
			
			googleUrl="https://www.google.com/search?tbm=isch&q=" + (question + "+" + questionMods).replace(",","") + params;

			
			logger.debug("---------------");
			logger.debug("googleUrl="+googleUrl);

			int counter=0;
			boolean moreResults = true;
			Document doc = null;
			Elements media = null;
			String responseString = null;
			JsonNode node = null;
        	ObjectMapper mapper = null;
        	FindResult findResult = null;
        	int page = 0;
        	String fullUrl = null;
        	findResults = new ArrayList<FindResult>();
        	// Continuar paginando resultados hasta que se alcance el numero de resultados solicitado o no se puedan obtener mas resultados
			while (counter<numResults && moreResults) {
				
				fullUrl = googleUrl;
//				if (page>0) {
//					fullUrl+="&ijn="+page+"&start="+(page*100);
//				}
				
				if (proxyHost!=null && proxyPort!=null) {
					URL url = new URL(fullUrl);
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
					HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
					uc.connect();
					String line = null;
					StringBuffer tmp = new StringBuffer();
					BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
					while ((line = in.readLine()) != null) {
						tmp.append(line);
					}
					doc = Jsoup.parse(String.valueOf(tmp));
				} else {
					doc=Jsoup.connect(fullUrl).userAgent(ua).timeout(100 * 1000).get();
				}
				
			    try {
			        FileWriter myWriter = new FileWriter("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\testGOWJava.html");
			        myWriter.write(doc.toString());
			        myWriter.close();
			        System.out.println("Successfully wrote to the file.");
			      } catch (IOException e) {
			        System.out.println("An error occurred.");
			        e.printStackTrace();
			      }
			    
			    ArrayList<Element> elementsAll = doc.getAllElements();
				int index = 0;
				String jsonData="";
				for (Element element : elementsAll) {
					String str=element.toString();
					if (str.contains("AF_initDataCallback") && str.contains("hash: '2'") && !str.contains("<body")) {
						logger.debug(index+"");
						jsonData = element.data().substring(element.data().indexOf('(')+1,element.data().lastIndexOf(')'));
					}
					index++;
				}
				
			    try {
			        FileWriter myWriter = new FileWriter("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\testGOWJava.json");
//			        jsonData = jsonData.replaceAll("\\\"","\\\\\"");  // '"' -> '\"'
//			        jsonData = jsonData.replaceAll("\\\\\\\\\\\"","\\\\\\\\\\\\\\\"");   // '\\"' -> '\\\"'
//			        jsonData = jsonData.replaceAll("', data:","\", \"data\":\"");
//			        jsonData = jsonData.replaceAll(", sideChannel:","\", \"sideChannel\":");
			        jsonData = jsonData.replaceAll("key: '","\"key\":\"");
			        jsonData = jsonData.replaceAll("', hash: '","\", \"hash\":\"");
			        jsonData = jsonData.replaceAll("', data:","\", \"data\":");
			        jsonData = jsonData.replaceAll(", sideChannel:",", \"sideChannel\":");
			        myWriter.write(jsonData);
			        myWriter.close();
			        System.out.println("Successfully wrote to the file.");
			      } catch (IOException e) {
			        System.out.println("An error occurred.");
			        e.printStackTrace();
			      }
			    
			    JSONParser parser = new JSONParser();
			    //jsonData = "{\"key\": \"ds:1\", \"hash\": \"2\"}";
			    logger.debug("jsonData:" + jsonData);
			    JSONObject jObject =  (JSONObject) parser.parse(jsonData);
			    JSONArray jArray = (JSONArray)jObject.get("data");

			    JSONArray jsonResults = null;
			    boolean found = false;
			    for (Object dataArray: jArray) {
				    //jObject[31][0]   [12][2][0-103]
				    // [31][0]
				    if (dataArray != null && dataArray instanceof JSONArray) {
				    	for (Object jArrayData: (JSONArray)dataArray) {
					    	// [31][0][12][0]
						    if (jArrayData != null && jArrayData instanceof JSONArray) {
						    	for (Object jData: (JSONArray)jArrayData) {
						    		if (jData != null && jData instanceof JSONArray &&
							    	 ((JSONArray)jData).size()>2 && ((JSONArray)jData).get(0)!=null && ((JSONArray)jData).get(0).equals("GRID_STATE0") &&
							    	 ((JSONArray)jData).get(2)!=null && (((JSONArray)jData).get(2) instanceof JSONArray)) {
						    			jsonResults = (JSONArray)((JSONArray)jData).get(2);
						    			found = true;
						    			break;
							    	}
						    	}
						    }
						    if (found) break;
					    }
				    }
				    if (found) break;
			    }

				//			System.out.println("---------------\n"+doc+"\n---------------\n");
//				media=doc.select("div.rg_meta"); // notranslate");
//				
//				mapper = new ObjectMapper();

			    for (Object jsonArray:jsonResults) {
			    	JSONArray item = (JSONArray)((JSONArray)jsonArray).get(1);
			    	if (item==null) {
			    		continue;
			    	}
			    	findResult = new FindResult(item);

//		        for (Element src : media) {
		        	if (counter==numResults) break;
//		            responseString = src.text();
//		        	node = mapper.readTree(responseString);
//					findResult = new FindResult(node);
//
//					// Eliminar las imagenes incrustadas en htmls no descargables
//					if (findResult.getImageURL()!=null && findResult.getImageURL().startsWith("x-raw-image://")) {
//						continue;
//					}

					// Seleccionar segun el margen de relacion de aspecto solicitado
					if (sizeMargin!=null && aspectRatio!=null) {
						float rWidth = (float)findResult.getImageWidth();
						float rHeight = (float)findResult.getImageHeight();
						float rAspectRatio = rWidth/rHeight;
						if (rAspectRatio<(aspectRatio-sizeMargin) || rAspectRatio>(aspectRatio+sizeMargin)) {
							continue;	// Aspect ratio fuera de margen
						}
					} else if (widthMargin!=null || heightMargin!=null) {
						Integer rWidth = findResult.getImageWidth();
						Integer rHeight = findResult.getImageHeight();
						if (widthMargin!=null && width!=null && rWidth!=null && (rWidth>(width.intValue()+widthMargin.intValue()) || (rWidth<(width.intValue()-widthMargin.intValue())))) {
							continue;
						}
						if (heightMargin!=null && height!=null && rHeight!=null && (rHeight>(height.intValue()+heightMargin.intValue()) || (rHeight<(height.intValue()-heightMargin.intValue())))) {
							continue;
						}
					}
					counter++;
					
					findResult.setName(searchName);
					logger.debug(findResult.toString());
					findResults.add(findResult);
		        }
		        
//		        if (media.size()<100) {
//		        	moreResults = false;
//		        }
//		        page++;
		        break;
			}

	        logger.debug("---------------\n");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
		return findResults;
	}

	private static String removeExcludedWords(String str,String[] strsExcluded) {
		
		if(str!=null && !str.isEmpty() && strsExcluded!=null && strsExcluded.length>0) {
			for (String strExcluded:strsExcluded) {
				str = str.replaceAll(strExcluded, "");
			}
		}
		return str;
	}
	
	
//    private static void print(String msg, Object... args) {
//        System.out.println(String.format(msg, args));
//    }
//    
//    private static String trim(String s, int width) {
//        if (s.length() > width)
//            return s.substring(0, width-1) + ".";
//        else
//            return s;
//    }
	
	
//    final static String LISTA_HECHO = "lista_hecho.log";
//    final static String BASE_DIR = "D:\\PS3\\PS2\\Juegos\\_done";
//    final static String OUT_DIR = "D:\\PS3\\PS2\\Juegos\\_done\\pics";
//    final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
//	private final static ExtensionsFilter EXT_FILTER = new ExtensionsFilter(new String[] {".iso"});
//	private final static String[] EXCLUDED_WORDS = new String[] {"-","(english patch)","_","DVD1","DVD2","DVD3","DVD4","CD1","CD2","Disc1","Disc2","Disc3","Disc4"};
//
//	public static void main(String[] args) {
//		
//		FindImageJSoup test = new FindImageJSoup("test");
//		test.test();
//
//	}
//	
//	public void test () {
//		
//		try { 
//
//	        File file = new File(BASE_DIR);
//
//	        // Reading directory contents
//	        File[] files = file.listFiles(EXT_FILTER);
//	        if (files!=null && files.length>0) {
//	        	String fileName = null;
//		        for (int i = 0; i < files.length; i++) {
//		        	fileName = FilenameUtils.getBaseName(files[i].getPath());
//		        	System.out.println("File:" + fileName);
//		        	if (Util.isDoneBefore(LISTA_HECHO,fileName)) {
//		        		System.out.println("Already done");
//	        			continue;
//		        	}
//		        	String question = removeExcludedWords(fileName,EXCLUDED_WORDS);
//		        	System.out.println("question:"+ question);
//		        	ArrayList<FindResult> findResults = findImage(question,"PS2 cover",USER_AGENT,4);
//					
//					if (OUT_DIR!=null && findResults!=null && findResults.size()>0) {
//						for (FindResult findResult:findResults) {
//							if (findResult.getImageURL()!=null) {
//								String resourceStr = findResult.getImageURL();
//								resourceStr=resourceStr.substring(1,resourceStr.length()-1);
//								Util.downloadURL(resourceStr,OUT_DIR,question,USER_AGENT);
//							}
//						}
//						
//						Util.writeFile(LISTA_HECHO,fileName,true);
//					}
//					
//					Thread.sleep(2000);
//		        }
//	        }
//	        
//        	System.out.println("DONE!!!!!");
//	        
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}

