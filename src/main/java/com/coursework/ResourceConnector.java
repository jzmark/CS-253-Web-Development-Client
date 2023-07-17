package com.coursework;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;

import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Resource connector used to connect to the server and retrieve date
 * @author Marek Jezinski - Based on Neal Harman's lab 14 Resource Connector
 */
public class ResourceConnector {

    private final Client client = Client.create();
    private final WebResource baseURI = client.resource("http://localhost:8080/webapi/webresource");

    /**
     * Adding a city
     * @param newCity city object
     */
    public void addCity(City newCity) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ClientResponse response = baseURI.path("add").type(MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class, mapper.writeValueAsString(newCity));
            System.out.printf("Adding: %s was founded in %d "
                            + "and is in %s, which is in %s -  Response code: %d%n",
                    newCity.getCity(), newCity.getFoundingDate(),
                    newCity.getState(), newCity.getCountry(), response.getStatus());
        } catch (IOException e) {
            System.out.println("Conversion to JSON failed");
        }
    }

    /**
     * Get request from the server to get cities only satisfying relevant filters
     * @param country country name filter
     * @param state state name filter
     * @param founded foundation date filter
     * @return array list of City objects satisfying requirements set
     */
    public ArrayList<City> get(String country, String state, String founded) {
        //MultivaluedMap is necessary for queryParams method implementation
        MultivaluedMap<String, String> queryParamMap = new MultivaluedMapImpl();
        //adding relevant parameters if they are set
        if (country != null) {
            queryParamMap.add("country", country);
        }
        if (state != null) {
            queryParamMap.add("state", state);
        }
        if (founded != null) {
            queryParamMap.add("founded", founded);
        }
        //retrieving cities from server with relevant filters
        ClientResponse response = baseURI.path("get").queryParams(queryParamMap).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            System.out.println("GET Failed with code: " + response.getStatus());
            return null;
        }
        //converting retrieved cities to arraylist of City objects
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<City> cities = null;
        try {
            cities = mapper.readValue(response.getEntity(String.class),
                    new TypeReference<ArrayList<City>>() {
                    });
        } catch (IOException e) {
            System.out.println("City not found");
        }
        return cities;
    }

    /**
     * Deleting City set with parameters
     * @param country country name
     * @param state state name
     * @param city city name
     */
    public void delete(String country, String state, String city) {
        MultivaluedMap<String, String> queryParamMap = new MultivaluedMapImpl();
        //all parameters must be set otherwise server will not return anything
        queryParamMap.add("country", country);
        queryParamMap.add("state", state);
        queryParamMap.add("city", city);

        WebResource webTarget = baseURI.path("delete").queryParams(queryParamMap);
        ClientResponse response = webTarget.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        System.out.println(response.getEntity(String.class) + " -  Response code: " + response.getStatus());
    }
}

