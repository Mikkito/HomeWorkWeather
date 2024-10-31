package weather;

import java.util.Map;

import org.json.simple.parser.ParseException;

import weather.GetData;

public class Main {
	public static void main(String[] args) throws ParseException {
		GetData getting = new GetData();
		getting.temperature();
	}
}
