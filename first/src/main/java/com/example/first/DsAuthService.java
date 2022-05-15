package com.example.first;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import com.docusign.esign.client.auth.OAuth.UserInfo;

@Service
public class DsAuthService {

	String oAuthBasePath = "account-d.docusign.com";
	String authName = "docusignAccessCode";
	String clientId = "xxx";
	String secret = "axxx";
	String redirectURl = "http://localhost:8080/code";
	List<String> scope = Arrays.asList("signature");
	String AdminUserId = "dxxx7";
	private String clientApiAccountId="xxx";

	ApiClient dsClient = new ApiClient(oAuthBasePath, authName, clientId, secret);

	public final static String DEMO_REST_BASEPATH = "https://demo.docusign.net/restapi";

	private FileSystemResource rsaPrivateKey;
	private String privateKeyPath = "src/main/resources/private.key";

	

	public URI generateOauthURI() {
		return dsClient.getAuthorizationUri(clientId, scope, redirectURl, "code");
	}

	public OAuthToken generateToken(String code) throws ApiException, IOException {
		return dsClient.generateAccessToken(clientId, secret, code);
	}

	public UserInfo getUserInfo(String token) throws IllegalArgumentException, ApiException {
		return dsClient.getUserInfo(token);
	}

	public byte[] getRsaBytes() throws IOException {
		try {
			if (rsaPrivateKey == null) {
				rsaPrivateKey = new FileSystemResource(privateKeyPath);
			}
			return FileCopyUtils.copyToByteArray(rsaPrivateKey.getInputStream());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public String generateJwtToken(String userEmail) throws ApiException, IOException, Exception {
		dsClient = new ApiClient(oAuthBasePath, authName);
		byte[] bytearray = this.getRsaBytes();

		try {
			OAuth.OAuthToken adminAccess = dsClient.requestJWTUserToken(clientId, AdminUserId, scope, bytearray, 3600);
			return dsClient.requestJWTUserToken(clientId, this.fetchUserId(adminAccess.getAccessToken(), userEmail),
					scope, bytearray, 3600).getAccessToken();
		} catch (ApiException e) {
			System.out.println(e.getMessage());
		}
		return ("[USER CONSENT REQUIRED] Give Consent using this URL : "
				+ dsClient.getJWTUri(clientId, redirectURl, oAuthBasePath));
	}

	public String fetchUserId(String adminAccessToken, String userEmail) throws IOException {
		JSONObject responseJson = new JSONObject(
				this.getUserDetailsFromDocusign(adminAccessToken, userEmail).toString());
		JSONObject usersJson = new JSONObject(responseJson.getJSONArray("users").get(0).toString());
		return usersJson.getString("userId");
	}

	public StringBuffer getUserDetailsFromDocusign(String adminAccessToken, String userEmail) throws IOException {
		URL url = new URL(DEMO_REST_BASEPATH + "/v2.1/accounts/" + clientApiAccountId + "/users?email=" + userEmail);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization", "Bearer " + adminAccessToken);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();
		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();
		return response;
	}
}
