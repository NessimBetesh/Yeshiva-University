//DONE FOR NOW
package edu.yu.cs.intro.bank2023;

import edu.yu.cs.intro.bank2023.Transaction.TxType;

/**
 * A StockTransaction is immutable. Value of nanoTimeStamp must be set at time of construction to the return value of System.nanoTime().
 */
public class StockTransaction implements Transaction{
    private final StockListing listing;
    private final TxType type;
    private final int quantity;
    private final long nanoTimeStamp;
    /**
     *
     * @param listing
     * @param type
     * @param quantity
     * @throws InvalidTransactionException thrown if TxType is neither BUY nor SELL, or if quantity <= 0, or if listing == null
     */
    public StockTransaction(StockListing listing, TxType type, int quantity) throws InvalidTransactionException{
        if((type != TxType.BUY && type != TxType.SELL) || quantity <= 0 || listing == null){
            throw new InvalidTransactionException("Invalid transaction", type);
        }
        this.listing = listing;
        this.type = type;
        this.quantity = quantity;
        this.nanoTimeStamp = System.nanoTime();
    }
    public StockListing getStock(){
        return listing;
    }
    public int getQuantity(){
        return quantity;
    }
    @Override
    public TxType getType() {
        return type;
    }
    @Override
    public long getNanoTimestamp() {
        return nanoTimeStamp;
    }
}
