package org.amplafi.flow.sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;


public class FacebookClient {
	static String GET_ACCESS_TOKEN = "https://graph.facebook.com/oauth/access_token?client_id=$APP_ID&client_secret=$APP_SECRET&grant_type=client_credentials" ;
	
	private HttpClient httpClient ;
	private String app_id ;
	private String app_secret ;
	private String accessToken ;
	
	public FacebookClient(String app_id, String app_secret) {
		this.httpClient = HttpClientFactory.getInstance();
		this.app_id = app_id ;
		this.app_secret = app_secret ;
	}
	
	public String getAccessToken() { return this.accessToken ; }
	
	public String updateAccessToken() throws Exception {
		String url = GET_ACCESS_TOKEN ;
		url = url.replace("$APP_ID", this.app_id) ;
		url = url.replace("$APP_SECRET", this.app_secret) ;
		HttpGet method = new HttpGet(url);
		BasicHttpContext httpContext = new BasicHttpContext();
		HttpResponse response = httpClient.execute(method, httpContext);
		String content = getContentAsString(response.getEntity().getContent());
		if(content.indexOf("access_token") >= 0) {
			String[] tmp = content.split("=") ;
			//tmp = tmp[1].split("\\|") ;
			this.accessToken = tmp[1].trim() ;
		}
		return this.accessToken ;
	}
	
	public String getUserInfo(String userId, String fields) throws Exception {
		StringBuilder url = new StringBuilder("https://graph.facebook.com/") ;
		url.append(userId) ;
		if(fields != null) {
			url.append("?fields=").append(fields) ;
			url.append("&access_token=").append(URLEncoder.encode(this.accessToken, "UTF-8")) ;
		} else {
		    url.append("?access_token=").append(URLEncoder.encode(this.accessToken, "UTF-8")) ;
		}
		System.out.println(url);
		HttpGet method = new HttpGet(url.toString());
		BasicHttpContext httpContext = new BasicHttpContext();
		HttpResponse response = httpClient.execute(method, httpContext);
		String content = getContentAsString(response.getEntity().getContent());
		return content ;
	}
	
	public void createTestUser(String userId, String fullName) throws Exception {
		System.out.println("Creating test user: " + userId) ;
		System.out.println("----------------------------------------") ;
		StringBuilder urlB = new StringBuilder("https://graph.facebook.com/") ;
		urlB.append(this.app_id).append("/accounts/").append("test-users").append("?").
			 append("installed=true&").
			 append("name=").append(URLEncoder.encode(fullName, "UTF-8")).append("&").
			 append("locale=en_US&").
			 append("permissions=read_stream&").
			 append("method=post&").
			 append("access_token=").append(URLEncoder.encode(this.accessToken, "UTF-8"));
		System.out.println("URL: " + urlB);
		HttpGet method = new HttpGet(urlB.toString());
		BasicHttpContext httpContext = new BasicHttpContext();
		HttpResponse response = httpClient.execute(method, httpContext);
		String content = getContentAsString(response.getEntity().getContent());
		System.out.println(content);
		System.out.println("----------------------------------------") ;
	}
	
	public  void postOnWall(String userId, String message) throws Exception {
		String url = "https://graph.facebook.com/$PROFILE_ID$/feed" ;
		url = url.replace("$PROFILE_ID$", userId);

		HttpClient client = HttpClientFactory.getInstance();
		HttpPost method = new HttpPost(url);
		ArrayList<NameValuePair>  params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", this.accessToken));
		params.add(new BasicNameValuePair("message", message));
		method.setEntity(new UrlEncodedFormEntity(params));

		BasicHttpContext httpContext = new BasicHttpContext();
		HttpResponse response = client.execute(method, httpContext);
		String content = getContentAsString(response.getEntity().getContent());
		System.out.println("User Info: \n" + content);
	}
	
	private String getContentAsString(InputStream is) throws Exception {
		String line = "";
		StringBuilder b = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		// Read response until the end
		while ((line = rd.readLine()) != null) { 
			b.append(line); 
		}
		// Return full string
		return b.toString() ;
	}
	
	public static void main(String args[]) throws Exception {
		String appId = "267973943274175" ;
		String appSecret = "442889abdde73b00560cfa3df179baeb" ;
		FacebookClient fb = new FacebookClient(appId, appSecret) ;
		String accessToken = fb.updateAccessToken() ;
		fb.createTestUser("farreaches.test", "Farreaches Test User") ;
		System.out.println("Access Token = " + accessToken);
		//fb.postOnWall("yourUsername", "A post from facebook app") ;
		String userInfo = fb.getUserInfo("tuan08", "name,username,email,gender,birthday") ;
		System.out.println("User Info: \n" + userInfo);
		
		userInfo = fb.getUserInfo("100004849774316", "name,username,email,gender,birthday") ;
		System.out.println("User Info: \n" + userInfo);
		
		fb.postOnWall("100004849774316", "A post from facebook app") ;
	}
}