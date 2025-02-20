package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpClientService {

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String getJson(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}
