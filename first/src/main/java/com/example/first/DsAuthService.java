package com.example.first;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import com.docusign.esign.client.auth.OAuth.UserInfo;

@Service
public class DsAuthService {
	
	String oAuthBasePath = "account-d.docusign.com"; 
	String authName= "docusignAccessCode";
	String clientId= "";
	String secret= "";
	String redirectURl="http://localhost:8080/code";
	List<String> scope = Arrays.asList("signature");
	ApiClient dsClient;
	
	public void init() {
		 dsClient = new ApiClient(oAuthBasePath,authName,clientId,secret);
	}
	
	public URI generateOauthURI() {
		if(dsClient==null) {
			this.init();
		}
		return dsClient.getAuthorizationUri(clientId, scope, redirectURl, "code");
	}

	public OAuthToken generateToken(String code) throws ApiException, IOException {
		
		return dsClient.generateAccessToken(clientId, secret, code);
	}

	public UserInfo getUserInfo(String token) throws IllegalArgumentException, ApiException {
		
		return dsClient.getUserInfo(token);
	}
	
}
