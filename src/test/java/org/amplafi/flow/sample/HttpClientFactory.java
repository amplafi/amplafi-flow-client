package org.amplafi.flow.sample ;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jul 8, 2010  
 */
public class HttpClientFactory {
  static public int MAX_HTTP_CONNECTION = 3000 ;
	static private DefaultHttpClient httpclient ;
	static HashSet<String> IGNORE_COMPRESS_SITES = new HashSet<String>() ;
  static {
  	IGNORE_COMPRESS_SITES.add("muaban.net") ;
  }
	
  synchronized static public DefaultHttpClient getInstance() {
    if(httpclient == null) {
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
      //Determines the timeout in milliseconds until a connection is established. 
      //A timeout value of zero is interpreted as an infinite timeout.
      //Please note this parameter can only be applied to connections that are bound to a particular local address.
      HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
      //Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or, 
      //put differently, a maximum period inactivity between two consecutive data packets). 
      //A timeout value of zero is interpreted as an infinite timeout.
      HttpConnectionParams.setSoTimeout(params, 60 * 1000);
      //Determines whether stale connection check is to be used. The stale connection check can cause up to 
      //30 millisecond overhead per request and should be used only when appropriate. For performance critical 
      //operations this check should be disabled.
      HttpConnectionParams.setStaleCheckingEnabled(params, false);
      //Determines whether Nagle's algorithm is to be used. The Nagle's algorithm tries to conserve 
      //bandwidth by minimizing the frequency of segments that are sent. When applications wish to decrease 
      //network latency and increase performance, they can disable Nagle's algorithm 
      //(that is enable TCP_NODELAY). Data will be sent earlier, at the cost of an increase 
      //in bandwidth consumption.
      HttpConnectionParams.setTcpNoDelay(params, true) ;

      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      HttpProtocolParams.setContentCharset(params, "UTF-8");
      //HttpProtocolParams.setUserAgent(params, "moom.vn");
      HttpProtocolParams.setUserAgent(params, "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
      // Create and initialize scheme registry 
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
      schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
      
      ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
      cm.setMaxTotal(MAX_HTTP_CONNECTION) ;
      cm.setDefaultMaxPerRoute(4) ;
      
      httpclient  = new DefaultHttpClient(cm, params);
      httpclient.getParams().setParameter("http.protocol.allow-circular-redirects", true);
      httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
      HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
          if (executionCount > 2) return false;
          if (exception instanceof NoHttpResponseException) return true;
          if (exception instanceof SSLHandshakeException) return false;
          HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
          boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
          if (idempotent) return true;
          return false;
        }
      };

      httpclient.setHttpRequestRetryHandler(myRetryHandler);
      HttpRequestInterceptor requestInterceptor = new HttpRequestInterceptor() {
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
          if (!request.containsHeader("Accept-Encoding")) {
          	String site = (String) context.getAttribute("crawler.site") ;
          	if(!IGNORE_COMPRESS_SITES.contains(site)) {
          		request.addHeader("Accept-Encoding", "gzip,deflate");
          	}
          }
        }
      };
      HttpResponseInterceptor responseInterceptor = new HttpResponseInterceptor() {
        public void process(HttpResponse response, final HttpContext context) throws HttpException, IOException {
          HttpEntity entity = response.getEntity();
          Header ceheader = entity.getContentEncoding();
          if (ceheader != null) {
            HeaderElement[] codecs = ceheader.getElements();
            for (int i = 0; i < codecs.length; i++) {
              String codecname = codecs[i].getName();
              if ("gzip".equalsIgnoreCase(codecname)) {
                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                return;
              } else if("deflate".equals(codecname)) {
                response.setEntity(new MyDeflateDecompressingEntity(response.getEntity()));
                return;
              }
            }
          }
        }
      } ;
      httpclient.addRequestInterceptor(requestInterceptor);
      httpclient.addResponseInterceptor(responseInterceptor);
    }
    return httpclient ;
  }
  
  static class GzipDecompressingEntity extends HttpEntityWrapper {
    public GzipDecompressingEntity(HttpEntity entity) { super(entity); }

    public InputStream getContent() throws IOException, IllegalStateException {
      if(wrappedEntity instanceof GzipDecompressingEntity) {
        return wrappedEntity.getContent() ;
      }
      InputStream wrappedin = wrappedEntity.getContent();
      return new GZIPInputStream(wrappedin);
    }

    public long getContentLength() { return -1; }
  }
  
  static class MyDeflateDecompressingEntity extends HttpEntityWrapper {
    public MyDeflateDecompressingEntity(final HttpEntity entity) { super(entity); }

    public InputStream getContent() throws IOException, IllegalStateException {
      // the wrapped entity's getContent() decides about repeatability
      if(wrappedEntity instanceof MyDeflateDecompressingEntity) {
        return wrappedEntity.getContent() ;
      }
      InputStream wrappedin = wrappedEntity.getContent();
      return new InflaterInputStream(wrappedin, new Inflater(true));
    }

    /**length of ungzipped content is not known*/
    public long getContentLength() { return -1; }
  }
}
