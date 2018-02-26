package com.googleSearch.test_googleSearch;

import java.net.URL;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.jayway.jsonpath.JsonPath;

/**
 * source:https://stackoverflow.com/questions/40181330/google-search-using-java-programatically
 */
public class GoogleSearchService {

	public static void main(String[] args) {
		try {
			GoogleSearchService googleSearchService = new GoogleSearchService();
			googleSearchService.searchGoogle(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
    public void searchGoogle(List<String> keywords){
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpTransport httpTransport = new NetHttpTransport();
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