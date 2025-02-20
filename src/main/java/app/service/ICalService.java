package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ICalService {

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String getICalContent(String iCalLink) {
        return restTemplate.getForObject(iCalLink, String.class);
    }
}
