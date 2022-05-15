package com.example.first;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import com.docusign.esign.client.auth.OAuth.UserInfo;



@RestController
public class Controller {
	
	@Autowired
	private DsAuthService dsAuthService;
	
	@GetMapping("/ds/auth/oauth")
	public URI generateOauthURI() {
		return dsAuthService.generateOauthURI();
	}
	
	@GetMapping("/ds/auth/jwt")
	public String generateJwtToken(@RequestParam String email) throws ApiException, IOException, Exception {
		return dsAuthService.generateJwtToken(email);
	}
	
	@GetMapping("/code")
	public OAuthToken generateToken(@RequestParam String code) throws ApiException, IOException {
		return dsAuthService.generateToken(code);
	}
	
	@GetMapping("/getUserInfo")
	public UserInfo getUserInfo(@RequestParam String token) throws IllegalArgumentException, ApiException{
		return dsAuthService.getUserInfo(token);
	}
}
