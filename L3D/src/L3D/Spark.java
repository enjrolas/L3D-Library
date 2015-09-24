package L3D;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;

import org.json.JSONObject;
import org.json.JSONArray;
import org.joda.time.DateTime;
import org.joda.time.format.*;

import java.util.*;

import org.apache.commons.codec.binary.Base64;

public class Spark {

	// public static List<String> availableCores;
	public static JSONObject availableCores;
	public static JSONArray allCores;
	private static String accessToken;
	public static String name;

	public static String readAll(Reader rd) {
		StringBuilder sb = new StringBuilder();
		try {
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
		} catch (Exception e) {
			System.out.println("ran into an error while reading data");
			System.out.println(e);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) {
		try {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONObject json = new JSONObject(jsonText);
				return json;
			} catch (Exception e) {
				System.out.println("trouble parsing JSON");
				System.out.println(e);
				return new JSONObject();
			} finally {

				is.close();
			}
		} catch (Exception e) {
			System.out.println("trouble opening the URL: " + url);
			System.out.println(e);
			return new JSONObject();
		}
	}

	public static JSONArray readJsonArrayFromUrl(String url) {
		try {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONArray json = new JSONArray(jsonText);
				return json;
			} catch (Exception e) {
				System.out.println("trouble parsing JSON");
				System.out.println(e);
				return new JSONArray();
			} finally {

				is.close();
			}
		} catch (Exception e) {
			System.out.println("trouble opening the URL: " + url);
			System.out.println(e);
			return new JSONArray();
		}
	}
	
	public static JSONArray readJsonArrayFromUrl(String dataURL, String username, String password)
	{
		JSONArray array=null;
	    try {
	        String webPage = dataURL;

	        String authString = username + ":" + password;
	        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
	        String authStringEnc = new String(authEncBytes);

	        URL url = new URL(webPage);
	        URLConnection urlConnection = url.openConnection();
	        urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
	        InputStream is = urlConnection.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);

	        int numCharsRead;
	        char[] charArray = new char[1024];
	        StringBuffer sb = new StringBuffer();
	        while ((numCharsRead = isr.read(charArray)) > 0) {
	          sb.append(charArray, 0, numCharsRead);
	        }
	        String result = sb.toString();
	        array=new JSONArray(result);

	      } catch (Exception e) {
	        e.printStackTrace();
	      } 
    return array;
	}


	public Spark(String token) {
		accessToken = token;
		try {
			loadCores();
		} catch (Exception e) {
			System.out.println("cannot load the cores");
		}
	}

	public Spark(String username, String password) {
		try {
			accessToken = getAccessToken(username, password);
			loadCores();
		} catch (Exception e) {
			System.out.println("cannot load the cores");
		}
	}
	
	String getAccessToken(String username, String password)
	{
		System.out.println("Getting access token from Spark's API...");
		String token=null;
		String expiration=null, client=null;
		String url = "https://api.particle.io/v1/access_tokens";
		DateTimeFormatter parser2 = ISODateTimeFormat.dateTime();
		JSONArray allTokens = readJsonArrayFromUrl(url, username, password);
		for (int i = 0; i < allTokens.length(); i++) {
			try{
			JSONObject currentToken = allTokens.getJSONObject(i);
			expiration=currentToken.getString("expires_at");
			client=currentToken.getString("client");
			DateTime now = new DateTime();			
		    DateTime expirationDate=parser2.parseDateTime(expiration);
		    if(expirationDate.isAfter(now))
				token=currentToken.getString("token");
			}
			catch(Exception e)
			{
				System.out.println("error parsing JSON at getAccessToken");
				System.out.println(e);
			}			
		}
		System.out.println("Got access token:  "+token);
		return token;
	}
	
	void loadCores() {
		String url;
		try {
			url = "https://api.particle.io/v1/devices/?access_token="
					+ accessToken;
			allCores = readJsonArrayFromUrl(url);
			//System.out.println(allCores);
			System.out.println("Finding available cubes");
			System.out.println("checking URL: " + url);
			// availableCores = new ArrayList<String>();
			availableCores = new JSONObject();
			for (int i = 0; i < allCores.length(); i++) {
				JSONObject core = allCores.getJSONObject(i);
				if (core.getBoolean("connected")) {
					try{
					String deviceID = core.getString("id");
					String coreName = core.getString("name");
					JSONObject currentCore = new JSONObject();
					currentCore.put("name", coreName);
					currentCore.put("deviceID", deviceID);
					String deviceURL = "https://api.particle.io/v1/devices/"
							+ deviceID + "/?access_token=" + accessToken;
					System.out.println("getting variables for core");
					System.out.println("checking URL: " + deviceURL);
					JSONObject thisCore = readJsonFromUrl(deviceURL);
					JSONObject activeVariables = thisCore
							.getJSONObject("variables");
					System.out.println(thisCore.toString());

					Iterator<?> keys = activeVariables.keys();

					while (keys.hasNext()) {
						String variableName = keys.next().toString();
						String variableURL = "https://api.particle.io/v1/devices/"
								+ deviceID + "/" + variableName
								+ "/?access_token=" + accessToken;
						System.out.println("getting value of variable "+variableName);
						System.out.println(variableURL);
						JSONObject variable = readJsonFromUrl(variableURL);

						System.out.println(variable);
						
						if (activeVariables.getString(variableName).equals(
								"string")) {
							String value = variable.getString("result");
							System.out.println("value:  "+value);
							currentCore.put(variableName, value);
						} else if (activeVariables.getString(variableName)
								.equals("int32")) {
							int value = variable.getInt("result");
							currentCore.put(variableName, value);
						} else if (activeVariables.getString(variableName)
								.equals("float")) {
							float value = (float) variable.getDouble("result");
							currentCore.put(variableName, value);
						}
					}

					// add the current core to the list of available cores
					// TODO -- check to make sure the variables aren't all null
					// before putting it in the list
					availableCores.put(coreName, currentCore);

					System.out.println(core.getString("name"));
				}
				catch(Exception e)
				{
					System.out.println("error getting data from core.  Maybe it's not connected and the system hadn't updated, yet.");
				}
				}
			}
		} catch (Exception e) {
			System.out.println("error loading core info from the spark cloud");
			System.out.println(e);
		}

		System.out.println("available cores:");
		Iterator<?> keys = availableCores.keys();
		while (keys.hasNext()) {
			String key = keys.next().toString();
			System.out.println(key);
		}
	}

	private static String getVar(String coreName, String varName) {
		try {
			JSONObject core = availableCores.getJSONObject(coreName);
			return core.getString(varName);
		} catch (Exception e) {
			System.out.println("that wasn't a JSON object");
			System.out.println(e);
			return null;
		}
	}

	public static String getAddress(String _name) {
		return getVar(_name, "IPAddress");
	}

}
