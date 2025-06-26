package hr.algebra.xiaohongshuiis.config;

import hr.algebra.xiaohongshuiis.service.DhmzWeatherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.HttpRequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class WeatherXmlRpcHandler implements HttpRequestHandler {

    private final DhmzWeatherService weatherService;

    public WeatherXmlRpcHandler(DhmzWeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if ("POST".equals(request.getMethod())) {
            handleXmlRpcRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().write("Only POST method allowed");
        }
    }

    private void handleXmlRpcRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String xmlRequest = requestBody.toString();
        String cityName = extractCityName(xmlRequest);

        try {
            List<DhmzWeatherService.WeatherData> weatherData = weatherService.getTemperatureByCity(cityName);
            String xmlResponse = createXmlRpcResponse(weatherData);

            response.setContentType("text/xml; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            try (PrintWriter writer = response.getWriter()) {
                writer.write(xmlResponse);
            }

        } catch (Exception e) {
            String errorResponse = createErrorResponse(e.getMessage());
            response.setContentType("text/xml; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorResponse);
            }
        }
    }

    private String extractCityName(String xmlRequest) {
        int start = xmlRequest.indexOf("<string>");
        int end = xmlRequest.indexOf("</string>");
        if (start != -1 && end != -1) {
            return xmlRequest.substring(start + 8, end);
        }
        return "";
    }

    private String createXmlRpcResponse(List<DhmzWeatherService.WeatherData> weatherData) {
        StringBuilder response = new StringBuilder();
        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        response.append("<methodResponse>\n");
        response.append("  <params>\n");
        response.append("    <param>\n");
        response.append("      <value>\n");
        response.append("        <array>\n");
        response.append("          <data>\n");

        for (DhmzWeatherService.WeatherData weather : weatherData) {
            response.append("            <value>\n");
            response.append("              <struct>\n");
            response.append("                <member>\n");
            response.append("                  <name>city</name>\n");
            response.append("                  <value><string>").append(escapeXml(weather.getCityName())).append("</string></value>\n");
            response.append("                </member>\n");
            response.append("                <member>\n");
            response.append("                  <name>temperature</name>\n");
            response.append("                  <value><string>").append(escapeXml(weather.getTemperature())).append("</string></value>\n");
            response.append("                </member>\n");
            response.append("                <member>\n");
            response.append("                  <name>humidity</name>\n");
            response.append("                  <value><string>").append(escapeXml(weather.getHumidity())).append("</string></value>\n");
            response.append("                </member>\n");
            response.append("                <member>\n");
            response.append("                  <name>pressure</name>\n");
            response.append("                  <value><string>").append(escapeXml(weather.getPressure())).append("</string></value>\n");
            response.append("                </member>\n");
            response.append("              </struct>\n");
            response.append("            </value>\n");
        }

        response.append("          </data>\n");
        response.append("        </array>\n");
        response.append("      </value>\n");
        response.append("    </param>\n");
        response.append("  </params>\n");
        response.append("</methodResponse>");

        return response.toString();
    }

    private String createErrorResponse(String errorMessage) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<methodResponse>\n" +
                "  <fault>\n" +
                "    <value>\n" +
                "      <struct>\n" +
                "        <member>\n" +
                "          <name>faultCode</name>\n" +
                "          <value><int>1</int></value>\n" +
                "        </member>\n" +
                "        <member>\n" +
                "          <name>faultString</name>\n" +
                "          <value><string>" + escapeXml(errorMessage) + "</string></value>\n" +
                "        </member>\n" +
                "      </struct>\n" +
                "    </value>\n" +
                "  </fault>\n" +
                "</methodResponse>";
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}