package app.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static app.service.ScheduleService.baseURL;
import static app.service.ScheduleService.objectMapper;

@Service
public class ICalService {

    @Autowired
    private HttpClientService httpClientService;

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String getICalContent(String iCalLink) {
        return restTemplate.getForObject(iCalLink, String.class);
    }

    public String getICalLink(String criteria) throws Exception {

        String url = baseURL + "?limit=15&match=" + criteria;

        String json = httpClientService.getJson(url);

        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode dataNode = rootNode.path("data");
        String iCalLink = "";
        if (dataNode.isArray() && !dataNode.isEmpty()) {
            iCalLink = dataNode.get(0).path("iCalLink").asText();
        } else {
            return null;
        }
        return iCalLink;
    }
}
