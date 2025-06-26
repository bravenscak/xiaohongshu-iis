package hr.algebra.xiaohongshuiis.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DhmzWeatherService {

    private static final String DHMZ_URL = "https://vrijeme.hr/hrvatska_n.xml";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<WeatherData> getTemperatureByCity(String cityName) {
        List<WeatherData> results = new ArrayList<>();

        try {
            String xmlData = restTemplate.getForObject(DHMZ_URL, String.class);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));

            NodeList gradovi = document.getElementsByTagName("Grad");

            for (int i = 0; i < gradovi.getLength(); i++) {
                Element grad = (Element) gradovi.item(i);
                String gradIme = grad.getElementsByTagName("GradIme").item(0).getTextContent();

                if (gradIme.toLowerCase().contains(cityName.toLowerCase())) {
                    WeatherData weather = new WeatherData();
                    weather.setCityName(gradIme);

                    if (grad.getElementsByTagName("Temp").getLength() > 0) {
                        String temp = grad.getElementsByTagName("Temp").item(0).getTextContent();
                        weather.setTemperature(temp);
                    }

                    if (grad.getElementsByTagName("Vlaga").getLength() > 0) {
                        String vlaga = grad.getElementsByTagName("Vlaga").item(0).getTextContent();
                        weather.setHumidity(vlaga);
                    }

                    if (grad.getElementsByTagName("Tlak").getLength() > 0) {
                        String tlak = grad.getElementsByTagName("Tlak").item(0).getTextContent();
                        weather.setPressure(tlak);
                    }

                    results.add(weather);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather data: " + e.getMessage());
        }

        return results;
    }

    public static class WeatherData {
        private String cityName;
        private String temperature;
        private String humidity;
        private String pressure;

        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }

        public String getTemperature() { return temperature; }
        public void setTemperature(String temperature) { this.temperature = temperature; }

        public String getHumidity() { return humidity; }
        public void setHumidity(String humidity) { this.humidity = humidity; }

        public String getPressure() { return pressure; }
        public void setPressure(String pressure) { this.pressure = pressure; }
    }
}