//DONE FOR NOW
package edu.yu.cs.intro.bank2023;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockExchange {
    private Map<String, StockListing> stockListings;

    protected StockExchange(){
        this.stockListings = new HashMap<>();
    }

    /**
     *
     * @param tickerSymbol symbol of the new stock to be created, e.g. "IBM", "GOOG", etc.
     * @param initialPrice price of a single share of the stock
     * @param availableShares how many shares of the stock are available initially
     * @throws IllegalArgumentException if there's already a listing with that tickerSymbol
     */
    public void createNewListing(String tickerSymbol, double initialPrice, int availableShares){
        if(stockListings.containsKey(tickerSymbol)){
            throw new IllegalArgumentException();
        }
        StockListing listing = new StockListing(tickerSymbol, initialPrice, availableShares);
        stockListings.put(tickerSymbol, listing);
    }

    /**
     * @param tickerSymbol
     * @return the StockListing object for the given tickerSymbol, or null if there is none
     */
    public StockListing getStockListing(String tickerSymbol){
        //StockListing = new tickerSymbol();
        if(tickerSymbol != null){
            return stockListings.get(tickerSymbol);
        }
        else return null;
    }

    /**
     * @return an umodifiable list of all the StockListings currently found on this exchange
     * @see java.util.Collections#unmodifiableList(List)
     */
    public List<StockListing> getAllCurrentListings(){
        return Collections.unmodifiableList(new ArrayList<>(stockListings.values()));
    }
}