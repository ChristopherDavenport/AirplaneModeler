package com.davenportsolutions;
/**
 * Made By Christopher Davenport
 * 2/25/2016
 *
 * Helping a friend with homework as a demonstration. First ever java application.
 *
 * Purpose of the Problem is to model airports where every airport is traversed.
 * Prices are random for each flight.
 */

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        // Initial Computer Generated Variables
        final int totalAirports = airportGenerator(5000);
        final int[] prices = {19, 29, 39, 49, 59, 69};
        final List<FlightCost> flights = generateFlights(totalAirports, prices);

        // Initial User Generated Variables
        final int startingCity = getUserIntBetweenZeroAndMax(
                "Please enter a city to start from (0-" + totalAirports + ")",
                totalAirports);
        final int destinationCity = getUserIntBetweenZeroAndMax(
                "Please enter a destination city (0-" + totalAirports + ")",
                totalAirports);

        // No Flying to the Same City!
        if (destinationCity != startingCity){

            // Finds Singular NonStopFlight
            final FlightCost nonStopFlight = findNonStopFlight(flights, startingCity, destinationCity);

            // Finds all OneStopFlights
            final List<OneStopFlight> oneStopFlights= findOneStopFlights(flights, startingCity, destinationCity);

            // Narrows All OneStopFlights down the the Cheapest One
            final OneStopFlight cheapestOneStopFlight = cheapestOneStopFlight(oneStopFlights);

            // Print Out the Two Flights
            printNonStopFlightInformation(nonStopFlight);
            printOneStopFlightInformation(cheapestOneStopFlight);
        }
        else{
            System.out.println("We don't take airplanes to where we already are...");
        }
    }

    /**
     * Shared Random for trueish randomness.
     */
    static Random myRandom = new Random();

    /**
     * Creates Between 3 and a Max Number of
     * This allows for modification on the fly.
     * Due to Basic Limitations cannot have less
     * than 3 airports and will Throw
     */
    static int airportGenerator(int max){
        if(max <= 3){
            throw new IllegalArgumentException("Must be more than 3 airports");
        }
        if(max > 5000){
            throw new IllegalArgumentException("Must be less than 5,000 airports");
        }
        return 3 + myRandom.nextInt(max - 3);
    }

    /**
     * Just a Random Against any intArray this allows you to change the prices at any time
     */
    static int randomPriceGenerator(int[] prices){
        // Randomly selects a price for a flight out of the prices
        return prices[myRandom.nextInt(prices.length)];
    }

    /**
     * Nested For Loop Creates a List of All Flights where you are not flying to the same city you are already in...
     * That's just silly.
     */
    static List<FlightCost> generateFlights(int totalAirports, int[] prices){
        List<FlightCost> Flights = new ArrayList<>();
        for (int i = 0; i <= totalAirports; i++){
            for (int j = 0; j <= totalAirports; j++){
                if ( j != i) {
                    FlightCost thisFlight = new FlightCost(i, j, randomPriceGenerator(prices));
                    Flights.add(thisFlight);
                }
            }
        }
        return Flights;
    }

    /**
     * Proper formatting for a NonStopFlight
     * Prints to Console
     */
    static void printNonStopFlightInformation(FlightCost flight){
        System.out.println("Nonstop: Start- " + flight.getStart() + " Dest- "+ flight.getDestination() +
                " Cost- " +  flight.getCost());
    }

    /**
     * Proper formatting for a OneStopFlight
     * Prints to Console
     */
    static void printOneStopFlightInformation(OneStopFlight flight){
        System.out.println("Onestop: Start- " + flight.getStart() + " Stop- " + flight.getStop() + " Dest- "+ flight.getDestination() +
                " Cost- " +  flight.getCost());
    }

    /**
     * Finds the flight that starts at the start and ends at the dest.
     * Utilizes a Stream Filter and then recollects into a list and we select the only element.
     */
    static FlightCost findNonStopFlight(List<FlightCost> flights, int start, int dest){
        return flights.stream()
                .filter(flight -> flight.getStart() == start && flight.getDestination() == dest)
                .collect(Collectors.toList())
                .get(0);
    }

    /**
     * This is probably the most complicated algorithm I used here.
     *
     * leavingStart finds all flights leaving the start point
     * arriving destination finds all flights that arrive at the destination
     *
     * then we perform a foreach on all flights leaving the start
     * we then select the flight that leaves from the destination city of the first hope
     * and creates a OneStopFlight in our List of OneStopFlights
     */
    static List<OneStopFlight> findOneStopFlights(List<FlightCost> flights, int start, int dest){

        List<OneStopFlight> OneStopFlights = new ArrayList<>();

        List<FlightCost> leavingStart = flights.stream()
                .filter(flight -> flight.getStart() == start && flight.getDestination() != dest)
                .collect(Collectors.toList());

        List<FlightCost> arrivingDestination = flights.stream()
                .filter(flight -> flight.getDestination() == dest)
                .collect(Collectors.toList());

        for (FlightCost flightCost : leavingStart) {

            FlightCost secondHop = arrivingDestination.stream()
                    .filter(flightCost1 -> flightCost1.getStart() == flightCost.getDestination())
                    .collect(Collectors.toList())
                    .get(0);

            OneStopFlights.add(
                    new OneStopFlight(
                        flightCost.getStart(),
                        flightCost.getDestination(),
                        secondHop.getDestination(),
                        flightCost.getCost()+secondHop.getCost()
                    )
            );
        }
        return OneStopFlights;
    }

    /**
     * Fairly Straightforward in that it turns a list of all Onestop flights
     * and takes the one with the lowest value for the getCost Method.
     *
     */
    static OneStopFlight cheapestOneStopFlight(List<OneStopFlight> OneStopFlights){
        return OneStopFlights.stream()
                .min(Comparator.comparing(oneStopFlight -> oneStopFlight.getCost()))
                .get();
    }

    /**
     * This Method Utilizes a Scanner to get an Integer in the proper Range
     * Scanner utilizes regular expressions.
     *
     */
    static int getUserIntBetweenZeroAndMax(String prompt, int totalAirports){
        Scanner scanner = new Scanner(System.in);
        int validNumber;
        do {
            System.out.println(prompt);
            while (!scanner.hasNextInt()){
                System.out.println(prompt);
                scanner.next();
            }
            validNumber = scanner.nextInt();
        } while (validNumber < 0 || totalAirports < validNumber);

        return validNumber;
    }

    /**
     * This generates a class to contain our flight cost
     * This closely models a normalized table for all flights
     *
     * This class is important because the constructor and the getter
     * methods are doing all the work for us.
     */
    static class FlightCost{
        int start;
        int destination;
        int cost;

        public int getStart(){
            return start;
        }
        public int getDestination(){
            return destination;
        }
        public int getCost(){
            return cost;
        }

        public FlightCost(int start, int destination, int cost){
            this.start = start;
            this.destination = destination;
            this.cost = cost;
        }
    }

    /**
     * Add a stop and the methods to construct a OneStopFlight
     */
    static class OneStopFlight extends FlightCost{
        int stop;

        public int getStop(){
            return stop;
        }

        public OneStopFlight(int start, int stop, int destination, int cost){
            super(start, destination,cost);
            this.stop = stop;
        }
    }
}
