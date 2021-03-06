package de.stroehle.hendrik.steaminvestor;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;




public class DataGrabber {

    public static Bitmap  GetitemImageByUrl(String img_url){
        System.out.println("img url recieved: " + img_url);
        Bitmap out = downloadImage(img_url);
        return out;

    }

	public static String[][] GetItemnamesBySearching(String search_string,int anzahl_ergebnisse){
		String[] array;
		String[] arraytwo;
		String[] arraythree;

        String[] arrayurl;
        String[] arrayurltwo;

		String data = "";
		
		String[][] out_big = new String[anzahl_ergebnisse][2];
		int errorcount = 0;
		try {
			search_string = URLEncoder.encode(search_string, "UTF-8");
			data = GetListFromSearch(search_string, anzahl_ergebnisse);
		}
		catch(IOException e){

		}

		data = data.replace("\\", ""); // alle backslashes entfernen weil komisches url format in json
		array = data.split("steamcommunity.com/market/listings/730/", anzahl_ergebnisse + 1);
        //get img url
        arrayurl = data.split("62fx62fdpx2x", anzahl_ergebnisse + 1);
        //\get img url

		for(int i = 1; i <= anzahl_ergebnisse; i++){
			try{
				arraytwo = array[i].split(" id=", 2);
				
				arraythree = arraytwo[0].split("filter",2);
				arraytwo[0] = arraythree[0];
				
				arraytwo[0] = arraytwo[0].substring(0,arraytwo[0].length()-1); // bei ? kann nicht gesplittet werden also am ende abschneiden
				out_big[i-1][0] = arraytwo[0];


                arrayurltwo = arrayurl[i-1].split("62fx62f 1x, ",2);
                out_big[i-1][1] = arrayurltwo[1] + "200fx200f";

			}



			catch(ArrayIndexOutOfBoundsException e){
					System.err.println("ArrayIndexOutOfBoundsException: " + e.getMessage());
					errorcount += 1;
			}


		}
		
		
		if(errorcount > 0){
			String[][] out = new String[anzahl_ergebnisse - errorcount][2];
			for(int i = 0; i <= anzahl_ergebnisse - errorcount -1; i++) {
                out[i][0] = out_big[i][0];
                out[i][1] = out_big[i][1];
            }

			
			return out;
		}
		else{
			return out_big;
		}
		
	}
	

	
	public static double GetCurrentPriceFromItemName(String itemname) throws IOException{
		int out = 0;
		double price;
		String price_unformated = "33234234"; //test
		String[] array;
		
		String url_string = "http://steamcommunity.com/market/priceoverview/?country=DE&currency=3&appid=730&market_hash_name=" + itemname;
		String json = readJsonFromUrl(url_string);
		json = json.replace("\"", "");
		json = json.replace("\\", "\\\\");

		array = json.split("lowest_price:", 2);
		array = array[1].split("\\\\", 2);
		price_unformated = array[0];

		price_unformated = price_unformated.replaceAll(",", "");
		price_unformated = price_unformated.replace("\\\\u20ac}", "");
		price_unformated = price_unformated.replaceAll("-", "0");
		
		out = Integer.parseInt(price_unformated);

		return (double)((int)Math.round(out))/100;
	}


	
	private static String GetListFromSearch(String search, int anzahl_ergebnisse) throws IOException{
		String out = "";
		
		String url_string =
			"http://steamcommunity.com/market/search/render/?category_730_ItemSet%5B%5D=any&category_730_ProPlayer%5B%5D=any&category_730_TournamentTeam%5B%5D=any&appid=730&start=0&query="
			+ search
			+ "&count="
			+ Integer.toString(anzahl_ergebnisse);
		
		String json = readJsonFromUrl(url_string);
		return json;
	}
	
	
	
	
	

	

	
	
	private static double GetValueFromList(String list, String question){
		String[] array;
		String out_str;
		double out = 0;

		array = list.split(question, 3);
		array = array[1].split(":",2);
		array = array[1].split(",",2);
		out_str = array[0];
		
		out = Integer.parseInt(out_str);
		return out/100;
	}
	
	
	
	
	
	
	
	
	
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
	while ((cp = rd.read()) != -1) {
		sb.append((char) cp);
	}
	return sb.toString();
	}

	private static String readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			return jsonText;
		} finally {
			is.close();
		}
	}






	public static Bitmap downloadImage(String url) {
		Bitmap bitmap = null;
		InputStream stream = null;
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;

		try {
			stream = getHttpConnection(url);
			bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return bitmap;
	}

	// Makes HttpURLConnection and returns InputStream
	private static InputStream getHttpConnection(String urlString)
			throws IOException {
		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stream;
	}
}