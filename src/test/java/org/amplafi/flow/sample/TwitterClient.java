package org.amplafi.flow.sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


/**
 * A simple example that uses HttpClient to execute an HTTP request against
 * a target site that requires user authentication.
 */
public class TwitterClient {
	private String consumerKey ;
	private String consumetSecret ;
	private String accessToken ;
	private String tokenSecret ;
	 
	private DefaultHttpClient httpclient ;
    
	
	public TwitterClient(String consumerKey, String consumerSecret, String accessToken, String tokenSecret) {
		this.consumerKey = consumerKey ;
		this.consumetSecret = consumerSecret ;
		this.accessToken = accessToken ;
		this.tokenSecret = tokenSecret ;
		this.httpclient = HttpClientFactory.getInstance();
	}
	
	public String getStatusesHomeTimeline() throws Exception {
		System.out.println("\ncall getStatusesHomeTimeline()");
		System.out.println("------------------------------------------------------------");
		String url = "https://api.twitter.com/1.1/statuses/home_timeline.json";
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);

		OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumetSecret, serviceProvider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);

		accessor.accessToken = this.accessToken ;
		accessor.tokenSecret = this.tokenSecret ;

		OAuthMessage message = new OAuthMessage("GET", url, new HashMap<String, String>().entrySet());
		message.addRequiredParameters(accessor);
		String authorizationHeader = message.getAuthorizationHeader(null);
		System.out.println("Authorization: " + authorizationHeader);

		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Authorization", authorizationHeader) ;
		System.out.println("executing request: " + httpget.getRequestLine());
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		System.out.println(response.getStatusLine());
		System.out.println("Response content length: " + entity.getContentLength());
		return getContentAsString(entity.getContent()) ;
	}
	
	public String postDirectMessage(String userName, String mesg) throws Exception {
		System.out.println("\ncall postDirectMessage()");
		System.out.println("------------------------------------------------------------");
		
		String url = "https://api.twitter.com/1/direct_messages/new.json";
		String getUrl = url + "?screen_name=" + userName + "&text=" + URLEncoder.encode(mesg, "UTF-8") ;
		
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
		OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumetSecret, serviceProvider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);

		accessor.accessToken = this.accessToken ;
		accessor.tokenSecret = this.tokenSecret ;

		HttpPost httpMethod = new HttpPost(url);
		ArrayList<NameValuePair>  params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("screen_name", userName));
		params.add(new BasicNameValuePair("text", mesg));
		httpMethod.setEntity(new UrlEncodedFormEntity(params));
		
		/*
		HttpPost httpMethod = new HttpPost(url);
		String body = "{ \"screen_name\": \"SCREEN_NAME\", \"text\": \"TEXT\" }" ;
		body = body.replace("SCREEN_NAME", userName) ;
		body = body.replace("TEXT", mesg) ;
		System.out.println(body);
		StringEntity jsonEntity = new StringEntity(body) ;
		httpMethod.setEntity(jsonEntity) ;
		*/
		
		//HttpGet httpMethod = new HttpGet(getUrl);
		
		String method = httpMethod.getMethod() ;
		OAuthMessage message = new OAuthMessage(method, url, new HashMap<String, String>().entrySet());
		message.addRequiredParameters(accessor);
		String authorizationHeader = message.getAuthorizationHeader(null);
		System.out.println("Authorization: " + authorizationHeader);
		System.out.println("executing request: " + httpMethod.getRequestLine());
		
		httpMethod.addHeader("Authorization", authorizationHeader) ;
		
		HttpResponse response = httpclient.execute(httpMethod);
		HttpEntity entity = response.getEntity();

		System.out.println(response.getStatusLine());
		if (entity != null) {
			System.out.println("Response content length: " + entity.getContentLength());
		}
		return getContentAsString(entity.getContent()) ;
	}
	
	public String statusesUpdate(String mesg) throws Exception {
		System.out.println("\ncall statusesUpdate()");
		System.out.println("------------------------------------------------------------");
		
		String url = "https://api.twitter.com/1/statuses/update.json";
		
		OAuthServiceProvider serviceProvider = 
				new OAuthServiceProvider(
						"https://api.twitter.com/oauth/request_token", 
						"https://api.twitter.com/oauth/authorize", 
						"https://api.twitter.com/oauth/access_token");
		OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumetSecret, serviceProvider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);

		accessor.accessToken = this.accessToken ;
		accessor.tokenSecret = this.tokenSecret ;

		HttpPost httpMethod = new HttpPost(url);
		ArrayList<NameValuePair>  params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("trim_user", "true"));
		params.add(new BasicNameValuePair("include_entities", "true"));
		params.add(new BasicNameValuePair("text", URLEncoder.encode(mesg, "UTF-8")));
		httpMethod.setEntity(new UrlEncodedFormEntity(params));
		
		/*
		HttpPost httpMethod = new HttpPost(url);
		String body = "{ \"screen_name\": \"SCREEN_NAME\", \"text\": \"TEXT\" }" ;
		body = body.replace("SCREEN_NAME", userName) ;
		body = body.replace("TEXT", mesg) ;
		System.out.println(body);
		StringEntity jsonEntity = new StringEntity(body) ;
		httpMethod.setEntity(jsonEntity) ;
		*/
		
		//HttpGet httpMethod = new HttpGet(getUrl);
		
		String method = httpMethod.getMethod() ;
		OAuthMessage message = new OAuthMessage(method, url, new HashMap<String, String>().entrySet());
		message.addRequiredParameters(accessor);
		String authorizationHeader = message.getAuthorizationHeader(null);
		System.out.println("Authorization: " + authorizationHeader);
		System.out.println("executing request: " + httpMethod.getRequestLine());
		
		httpMethod.addHeader("Authorization", authorizationHeader) ;
		
		HttpResponse response = httpclient.execute(httpMethod);
		HttpEntity entity = response.getEntity();

		System.out.println(response.getStatusLine());
		if (entity != null) {
			System.out.println("Response content length: " + entity.getContentLength());
		}
		return getContentAsString(entity.getContent()) ;
	}
	
	private String getContentAsString(InputStream is) throws Exception {
		String line = "";
		StringBuilder b = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		while ((line = rd.readLine()) != null) { 
			b.append(line); 
			System.out.println(line);
		}
		// Return full string
		return b.toString() ;
	}
	
	public static void main(String[] args) throws Exception {
		//You will need to create a twitter 
		String consumerKey    = "BRm6uxFwBaNGIvJGMYg" ;
		String consumerSecret = "HAlrFNU1KpbLsGn3FmoZtoZnfttsXZlPneIoKT0ivo";
		String accessToken    = "1006185044-ZPlHm3dyjbOD2dArktK6hZqnoyBJnR0zDcPmUWr";
		String tokenSecret    = "ZtAVj37frtj7zaQSPU7ScitWxFbUc0JsHwtiSHTE";
		
		TwitterClient client = new TwitterClient(consumerKey, consumerSecret, accessToken, tokenSecret) ;
		client.getStatusesHomeTimeline() ;
		client.statusesUpdate("app statuses update............") ;
		client.postDirectMessage("tuan0875", "app direct message") ;
	}
}