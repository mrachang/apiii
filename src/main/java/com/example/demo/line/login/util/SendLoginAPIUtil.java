package com.example.demo.line.login.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.keys.LineKeys;
import com.example.demo.line.login.entity.AccessToken;
import com.example.demo.line.login.entity.LineUser;
import com.example.demo.line.login.entity.LineUserDetail;
import com.example.demo.line.util.JsonParserUtil;

@Component
public class SendLoginAPIUtil implements LineKeys {
	
	@Autowired
	JsonParserUtil jsonParserUtil;

	private static final Logger LOG = LoggerFactory.getLogger(SendLoginAPIUtil.class);
	// show spring init components and other tags at starting server
	{
		LOG.info("init :\t" + this.getClass().getSimpleName());
	}

	public AccessToken getUserAccessToken(String code) {
		AccessToken accessToken = new AccessToken();
		int respCode;
		try {
			// System.out.println(message);
			// 回傳的json格式訊息
			String urlParams = "grant_type=" + grant_type + "&code=" + code + "&redirect_uri=" + redirect_uri
					+ "&client_id=" + client_id + "&client_secret=" + client_secret;
			//System.out.println(urlParams);
			byte[] bodyData = urlParams.getBytes(StandardCharsets.UTF_8);
			URL myurl = new URL(URL_TOKEN); // 回傳的網址
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection(); // 使用HttpsURLConnection建立https連線
			con.setRequestMethod("POST");// 設定post方法
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;"); // 設定Content-Type為json
			con.setRequestProperty("charset", "utf-8");
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream()); // 開啟HttpsURLConnection的連線
			output.write(bodyData); // 回傳訊息編碼為utf-8
			output.close();
			respCode = con.getResponseCode();
			System.out.println("Resp Code:" + con.getResponseCode() + "; Resp Message:" + con.getResponseMessage()); // 顯示回傳的結果，若code為200代表回傳成功
			if (respCode != 200) {
				LOG.warn(this.getClass().getSimpleName() + " - getUserAccessToken : went wrong ,response code = "
						+ respCode);
			} else {
				accessToken = (AccessToken) jsonParserUtil.stringToJson(getReturn(con), AccessToken.class);
				
			}
		} catch (MalformedURLException e) {
			System.out.println("1Message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		}
		return accessToken;

	}

	public LineUserDetail getLineUserDetail(String idToken) {
		LineUserDetail lineUserDetail = new LineUserDetail();
		int respCode;
		try {
			// System.out.println(message);
			// 回傳的json格式訊息
			String urlParams = "id_token=" + idToken + "&client_id=" + client_id;
			//System.out.println(urlParams);
			byte[] bodyData = urlParams.getBytes(StandardCharsets.UTF_8);
			URL myurl = new URL("https://api.line.me/oauth2/v2.1/verify"); // 回傳的網址
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection(); // 使用HttpsURLConnection建立https連線
			con.setRequestMethod("POST");// 設定post方法
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;"); // 設定Content-Type為json
			con.setRequestProperty("charset", "utf-8");
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream()); // 開啟HttpsURLConnection的連線
			output.write(bodyData); // 回傳訊息編碼為utf-8
			output.close();
			respCode = con.getResponseCode();
			System.out.println("Resp Code:" + con.getResponseCode() + "; Resp Message:" + con.getResponseMessage()); // 顯示回傳的結果，若code為200代表回傳成功
			if (respCode != 200) {
				LOG.warn(this.getClass().getSimpleName() + " - getLineUserDetail : went wrong, response code = "
						+ respCode);
			} else {
				lineUserDetail = (LineUserDetail) jsonParserUtil.stringToJson(getReturn(con), LineUserDetail.class);
				// System.out.println("User name : "+lineUser.getName());
			}
		} catch (MalformedURLException e) {
			System.out.println("1Message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		}
		return lineUserDetail;
	}

	public LineUser getUser(String accessToken) {
		int respCode;
		LineUser lineUser = new LineUser();
		try {
			
			// 回傳的json格式訊息
			URL myurl = new URL("https://api.line.me/v2/profile"); // 回傳的網址
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection(); // 使用HttpsURLConnection建立https連線
			con.setRequestMethod("GET");// 設定get方法
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8"); // 設定Content-Type為json
			con.setRequestProperty("Authorization", "Bearer " + accessToken); // 設定Authorization
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream()); // 開啟HttpsURLConnection的連線
			output.flush();
			output.close();
			respCode = con.getResponseCode();
			System.out.println("Resp Code:" + con.getResponseCode() + "; Resp Message:" + con.getResponseMessage()); // 顯示回傳的結果，若code為200代表回傳成功

			if (respCode == 200) {
				lineUser = (LineUser) jsonParserUtil.stringToJson(getReturn(con), LineUser.class);
			}
			else {
				System.out.println("error accurs");
				
			}
		} catch (MalformedURLException e) {
			System.out.println("1Message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		}

		return lineUser;
	}
	
	public static String getReturn(HttpsURLConnection connection) throws IOException {
		StringBuilder buffer = new StringBuilder();
		// 將返回的輸入流轉換成字符串
		try (InputStream inputStream = connection.getInputStream();
			 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		}
	}
}