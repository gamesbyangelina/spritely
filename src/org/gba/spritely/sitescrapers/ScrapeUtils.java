package org.gba.spritely.sitescrapers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class ScrapeUtils {
	
	/**
	 * Establishes a connection to a given URL while ignoring SSL errors and following redirects
	 * @param url
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static InputStream readURLLax(URL url) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		
		SSLBypass.bypassSSL(clientBuilder);
		
		HttpClient client = clientBuilder.build();

		HttpGet getRequest = new HttpGet(url.toURI());
		HttpResponse response = client.execute(getRequest);   
		
		return response.getEntity().getContent();
		
	}
}
