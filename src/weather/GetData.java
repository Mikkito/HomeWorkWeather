package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetData {
	double[] coordinate = new double[2];
	int lim;
	String temperatures;
	String access_key = "ac5fb246-5348-45f7-ad62-a51286d00827";
	
	public void temperature() throws ParseException{
		setCoordinate();
		getValues(coordinate);
	}
	
	private void getValues(double[] coordinate) {
		String lat = "lat=" + coordinate[0];
		String lot = "lot=" + coordinate[1];
		String limit = "limit=" + lim;
		String path = "https://api.weather.yandex.ru/v2/forecast?"+lat+"&"+lot+"&"+limit;
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(path))
				.header("X-Yandex-Weather-Key", access_key)
				.GET().build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println(path);
			System.out.println("Response code: " + response.statusCode());
			temperatures = response.body();
			System.out.println("Response: " + response);
			System.out.println("Температура в указанной точке = " + parseResponse(response.body()));
			System.out.println("Средняя температура = " + parseResponseAvgTemp(response.body()));
		}
		catch (Exception e) {
			System.out.println("Error making Http request: " + e.getMessage());
		}
	}
	
	private void setCoordinate(){
		try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Введите координаты местности через пробел:");
		coordinate = Arrays.stream(reader.readLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
		System.out.println("Введите количество дней для подсчета средней температуры");
		lim = Integer.parseInt(reader.readLine());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private String parseResponse(String response) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject data = (JSONObject) parser.parse(response);
		JSONObject fact = (JSONObject) data.get("fact");
		String actualTemp = fact.get("temp").toString();
		return actualTemp;
	}
	
	private double parseResponseAvgTemp(String response) throws ParseException {
		ArrayList<String> tempAvgDay = new ArrayList<String>();
		int i = 0;
		JSONParser parser = new JSONParser();
		JSONObject data = (JSONObject) parser.parse(response);
		JSONArray forecasts = (JSONArray) data.get("forecasts");
		for (Object it: forecasts) {
			JSONObject forecastObj = (JSONObject) it;
			JSONObject parts = (JSONObject) forecastObj.get("parts");
			JSONObject day = (JSONObject) parts.get("day");
			tempAvgDay.add(day.get("temp_avg").toString());
			i++;
		}
		return tempAvg(tempAvgDay);
	}
	
	private double tempAvg(ArrayList<String> avgArray) {
		double tempAvg;
		tempAvg = Double.parseDouble(avgArray.get(0));
		for (int i = 1; i < avgArray.size(); i++) {
			tempAvg += Double.parseDouble(avgArray.get(i));
		}
		tempAvg = tempAvg / avgArray.size();
		return tempAvg;
	}
}
