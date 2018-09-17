package com.javaspring.petclinic.web;

import com.javaspring.petclinic.model.Owner;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
public class PetClinicRestControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp(){
        restTemplate=restTemplate.withBasicAuth("user2","secret");
    }
    @Test
    public void testGetOwnerById(){
        ResponseEntity<Owner> response = restTemplate.getForEntity("http://localhost:8085/rest/owner/1", Owner.class);

        MatcherAssert.assertThat(response.getStatusCodeValue(), Matchers.equalTo(200));//donen cevap status kodu kontrolu

        MatcherAssert.assertThat(response.getBody().getFirstName(),Matchers.equalTo("Burak"));

        MatcherAssert.assertThat(response.getBody().getLastName(),Matchers.equalTo("Gungor"));


    }
    @Test
    public void testGetOwnersByLastName(){
        ResponseEntity<List> response = restTemplate.getForEntity("http://localhost:8080/rest/owner?ln=Gungor", List.class);

        MatcherAssert.assertThat(response.getStatusCodeValue(),Matchers.equalTo(200));

        List<Map<String,String>> body= response.getBody();

        List<String> firstNames = body.stream().map(o -> o.get("firstName")).collect(Collectors.toList());

        MatcherAssert.assertThat(firstNames,Matchers.containsInAnyOrder("Burak","Gultekin","Emine"));
    }
    @Test
    public void testGetOwners(){
        ResponseEntity<List> response = restTemplate.getForEntity("http://localhost:8080/rest/owners", List.class);

        MatcherAssert.assertThat(response.getStatusCodeValue(),Matchers.equalTo(200));

        List<Map<String,String>> body = response.getBody();

        List<String> firstNames = body.stream().map(o -> o.get("firstName")).collect(Collectors.toList());

        MatcherAssert.assertThat(firstNames,Matchers.containsInAnyOrder("Burak","Handenur","Gultekin","Emine","Yusuf","Mehmet","Hakan","Utku"));
    }
    @Test
    public void testCreateOwner(){
        Owner owner=new Owner();
        owner.setFirstName("Utku");
        owner.setLastName("Polat");

        URI location = restTemplate.postForLocation("http://localhost:8080/rest/owner", owner);

        Owner owner2=restTemplate.getForObject(location,Owner.class);

        MatcherAssert.assertThat(owner2.getFirstName(),Matchers.equalTo(owner.getFirstName()));
        MatcherAssert.assertThat(owner2.getLastName(),Matchers.equalTo(owner.getLastName()));

    }
    @Test
    public void testUpdateOwner(){
        Owner owner=restTemplate.getForObject("http://localhost:8080/rest/owner/4",Owner.class);

        MatcherAssert.assertThat(owner.getFirstName(),Matchers.equalTo("Emine"));

        owner.setFirstName("Emineseni");

        restTemplate.put("http://localhost:8080/rest/owner/4",owner);

        owner=restTemplate.getForObject("http://localhost:8080/rest/owner/4",Owner.class);

        MatcherAssert.assertThat(owner.getFirstName(),Matchers.equalTo("Emineseni"));

    }
    @Test
    public void testDeleteOwner(){
        restTemplate.delete("http://localhost:8080/rest/owner/1");

        try {
            restTemplate.getForEntity("http://localhost:8080/rest/owner/1",Owner.class);

            Assert.fail("should haven't returned owner !");
        }catch (HttpClientErrorException ex){
            MatcherAssert.assertThat(ex.getStatusCode().value(),Matchers.equalTo(404));
        }

    }
}
