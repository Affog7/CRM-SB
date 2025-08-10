package com.smartcrm.SmartCRM;

import com.smartcrm.SmartCRM.entity.Client;
import com.smartcrm.SmartCRM.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository repo;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/clients";
    }

    @Test
    void createClient_thenGetAllClients() {
        // Vérifie que la base est vide
        repo.deleteAll();
        assertThat(repo.findAll()).isEmpty();

        // Crée un nouveau client
        Client newClient = Client.builder()
                .nom("Alice")
                .email("alice@example.com")
                .telephone("0600000000")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Client> request = new HttpEntity<>(newClient, headers);

        ResponseEntity<Client> postResponse =
                restTemplate.postForEntity(getBaseUrl(), request, Client.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().getId()).isNotNull();

        // Récupère tous les clients
        ResponseEntity<Client[]> getResponse =
                restTemplate.getForEntity(getBaseUrl(), Client[].class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).hasSize(1);
        assertThat(getResponse.getBody()[0].getNom()).isEqualTo("Alice");
    }
}