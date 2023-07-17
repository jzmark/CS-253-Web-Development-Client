package com.coursework;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ResourceConnector connector = new ResourceConnector();
        System.out.println("*** Add cities ***");
        connector.addCity(new City("Italy", "Marche", "Ancona", 387));
        connector.addCity(new City("Italy", "Lazio", "Rome", -753));
        connector.addCity(new City("Greece",  "Attica", "Athens", -3000));
        connector.addCity(new City("Greece", "Attica", "Pireaus", -2600));
        connector.addCity(new City("Greece",  "Messinia", "Messini", 1867));
        connector.addCity(new City("Greece", "Messinia", "Pylos", 1833));

        System.out.println("\n*** Add Rome again - should return 409 ***");
        connector.addCity(new City("Italy", "Lazio", "Rome", -753));

        System.out.println("\n*** Output all current cities ***");
        //all arguments set to null as no filter equals get all cities
        get(connector, null, null, null);

        System.out.println("\n*** Delete Ancona and then print the cities again ***");
        connector.delete("Italy", "Marche", "Ancona");
        get(connector, null, null, null);

        System.out.println("\n*** Try to delete Ancona again - should return 404 ***");
        connector.delete("Italy", "Marche", "Ancona");

        System.out.println("\n*** Try to delete a made up city called Neverland in a "
                + "\nreal state and country (Lazio, Italy) - should return 404 ***");
        connector.delete("Italy", "Lazio", "Neverland");

        System.out.println("\n**** Get all cities founded before 1700 ***");
        get(connector, null, null, 1700);

        System.out.println("\n*** Get all cities in Italy - should only be Rome as Ancona was deleted ***");
        get(connector, "Italy", null, null);

        System.out.println("\n*** Get all cities in Greece - should be Athens, Pireaus, Messini and Pylos ***");
        get(connector, "Greece", null, null);

        System.out.println("\n*** Get all cities in Messinia in Greece - should be Pylos and Messini ***");
        get(connector, "Greece", "Messinia", null);

        System.out.println("\n*** Create and add a (made up) city called Athens founded year 1 "
                + "\nin a province called Attica in Italy - should return 200 ***");
        connector.addCity(new City("Italy", "Attica", "Athens", 1));

        System.out.println("\n*** Get all cities in Italy having added new fake one ***");
        get(connector, "Italy", null, null);

        System.out.println("\n*** Return all cities older than 0 in Italy - NOT explicitly "
                + "\nasked for, should only be Rome ...***");
        get(connector, "Italy", null, 0);
    }

    /**
     * Get method moved to main to show that retrieved
     * Cities get correctly converted back into java objects
     * from passed JSON
     * @param connector connector used to connect to the service
     * @param country country name
     * @param state state name
     * @param founded date of foundation
     */
    public static void get(ResourceConnector connector, String country, String state, Integer founded) {
        String foundedString = null;
        if (founded != null) {
            //converting from Integer to String to keep data persistent
            foundedString = Integer.toString(founded);
        }
        ArrayList<City> cities = connector.get(country, state, foundedString);
        if (cities != null) {
            //outputting all retrieved City objects
            for (City value : cities) {
                System.out.printf("%s was founded in %d and is in %s, which is in %s%n",
                        value.getCity(), value.getFoundingDate(), value.getState(), value.getCountry());
            }
        }
    }
}
