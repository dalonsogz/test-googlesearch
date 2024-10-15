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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
		return findImage(question, questionMods, ua, numResults, width, height, sizeMargin, widthMargin, heightMargin, null,null, null);
	}

	public ArrayList<FindResult> findImage(String question, String questionMods, String ua, int numResults, Integer width, Integer height, 
			Float sizeMargin, Integer widthMargin, Integer heightMargin, String proxyHost, Integer proxyPort, String googleCookieConsent) {
		
		ArrayList<FindResult> findResults = null;
		String googleUrl=null;

		int totalCounter=0;
		int counter=0;
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

			counter=0;
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
					Map<String,String> cookies = new HashMap<String,String>();
					cookies.put("CONSENT",googleCookieConsent);
					int maxBodySize = 2048000;//2MB (default is 1MB) 0 for unlimited size
					doc=Jsoup.connect(fullUrl).userAgent(ua).cookies(cookies).timeout(100 * 1000).maxBodySize(maxBodySize).get();
				}
				
				writeFile("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\testGOWJava.html",doc.toString());
			    
			    ArrayList<Element> elementsAll = doc.getAllElements();
				int index = 0;
				String jsonData="";
				String startString = "google.plm(r);})();(function(){var m=";
				String endString = "var a=m;";
				for (Element element : elementsAll) {
					String str=element.toString();
//					if (str.contains("AF_initDataCallback") && str.contains("hash: '2'") && !str.contains("<body")) {
					if (str.contains(startString)) {
						logger.debug("index="+index);
						jsonData = element.data().substring(element.data().indexOf(startString)+startString.length(),element.data().lastIndexOf(endString)-1);
						break;
					}
					index++;
				}
/*
		        writeFile("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\testGOWJava0.json",jsonData);
//		        jsonData = jsonData.replaceAll("\\\"","\\\\\"");  // '"' -> '\"'
//		        jsonData = jsonData.replaceAll("\\\\\\\\\\\"","\\\\\\\\\\\\\\\"");   // '\\"' -> '\\\"'
//		        jsonData = jsonData.replaceAll("', data:","\", \"data\":\"");
//		        jsonData = jsonData.replaceAll(", sideChannel:","\", \"sideChannel\":");
		        jsonData = jsonData.replaceAll("key: '","\"key\":\"");
		        jsonData = jsonData.replaceAll("', hash: '","\", \"hash\":\"");
		        jsonData = jsonData.replaceAll("', data:","\", \"data\":");
		        jsonData = jsonData.replaceAll(", sideChannel:",", \"sideChannel\":");
		        writeFile("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\testGOWJava.json",jsonData);
			    */
			    //logger.debug("jsonData:" + jsonData);
			    JSONParser parser = new JSONParser();
			    JSONObject jObject =  (JSONObject) parser.parse(jsonData);
			    JSONArray jsonResults = null;

/*
			    JSONArray jArray = (JSONArray)jObject.get("data");
			    boolean found = false;
			    index = 0;
			    for (Object dataArray: jArray) {
			    	System.out.println("index:"+index++);
//			    	if (index<56) continue;
				    //jObject[31][0]   [12][2][0-103]
				    // [31][0]										// [56] [1] [0-103]         
				    if (dataArray != null && dataArray instanceof JSONArray) {
				    	for (Object jArrayData: (JSONArray)dataArray) {
					    	// [31][0][12][0]
						    if (jArrayData != null && jArrayData instanceof JSONArray) {
						    	writeFile("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\jarraydata.json",jArrayData.toString());
						    	for (Object jData: (JSONArray)jArrayData) {
						    		if (jData != null && jData instanceof JSONArray) {
						    			if ( ((JSONArray)jData).get(0) != null && ((JSONArray)jData).get(0) instanceof JSONArray) {
						    				if ( ((JSONArray)((JSONArray)jData).get(0)).get(0) != null && ((JSONArray)((JSONArray)jData).get(0)).get(0) instanceof JSONArray) {
							    				if ( ((JSONArray)((JSONArray)((JSONArray)jData).get(0)).get(0)).get(0) != null && ((JSONArray)((JSONArray)((JSONArray)jData).get(0)).get(0)).get(0) instanceof JSONObject) {
									    			if ( ((JSONArray)((JSONArray)jData).get(0)).get(1) != null && ((JSONArray)((JSONArray)jData).get(0)).get(1) instanceof JSONArray) {
								    					jsonResults = (JSONArray)((JSONArray)((JSONArray)((JSONArray)jData).get(0)).get(1)).get(0);
										    			found = true;
										    			break;
									    			}
							    				}
									    	}
						    			}
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

		    	writeFile("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\jsonResults.json",jsonResults.toJSONString());
*/

			    for (Object key:jObject.keySet()) {
			    	totalCounter++;
//			    	JSONArray item = (JSONArray)((JSONArray)jsonArray).get(1);
			    	JSONArray item = (JSONArray)jObject.get(key.toString());
			    	if (item==null) {
			    		continue;
			    	}
			    	try {
			    		findResult = new FindResult(item);
			    	} catch (Exception e) {
			    		continue;
			    	}

		        	if (counter==numResults) break;

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
					
//					findResult.setName(searchName);
//					logger.debug("findResult="+findResult.toString());
//					logger.debug("imageURL="+findResult.getImageURL());
//					logger.debug("imageURLCoded="+findResult.getImageURLCoded());
//					logger.debug("thumbnailURL="+findResult.getThumbnailURL());
//					logger.debug("thumbnailURL="+findResult.getThumbnailURLCoded());
					findResults.add(findResult);
		        }
		        
//		        if (media.size()<100) {
//		        	moreResults = false;
//		        }
//		        page++;
		        break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
		logger.debug("TotalImagesFiltered:"+totalCounter);
		return findResults;
	}

	private void writeFile (String filepath,String str) {
		FileWriter myWriter = null;
	    try {
	        myWriter = new FileWriter(filepath);
	        myWriter.write(str);
	        myWriter.close();
	        System.out.println("Successfully wrote to the file ("+filepath+").");
	      } catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      } finally {
	    	  try {
	    		  myWriter.close();
	    	  } catch (Exception e) {
	  	        System.out.println("Error closing file (\"+filepath+\").");
		        e.printStackTrace();  
	    	  }
	      }
	}
	
	private static String removeExcludedWords(String str,String[] strsExcluded) {
		
		if(str!=null && !str.isEmpty() && strsExcluded!=null && strsExcluded.length>0) {
			for (String strExcluded:strsExcluded) {
				str = str.replaceAll(strExcluded, "");
			}
		}
		return str;
	}
	
	
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
    
    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}

