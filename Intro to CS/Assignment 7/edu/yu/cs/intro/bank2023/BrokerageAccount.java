package edu.yu.cs.intro.bank2023;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Soundbank;

import edu.yu.cs.intro.bank2023.Transaction.TxType;

/**
 * Models a brokerage account, i.e. an account used to buy, sell, and own stocks
 */
    public class BrokerageAccount extends Account{
    private Map<String,StockShares>sharesMap = new HashMap<String,StockShares>();

    /**
     * This will be called by the Bank class.
     * @param accountNumber the account number assigned by the bank to this new account
     * @param patron the Patron who owns this account
     * @see Bank#openNewBrokerageAccount(Patron)
     */
    protected BrokerageAccount(int accountNumber, Patron patron) {
        super(accountNumber, patron);
    }

    /**
     * @return an unmodifiable list of all the shares of stock currently owned by this account
     * @see java.util.Collections#unmodifiableList(List)
     */
    public List<StockShares> getListOfShares(){
        List<StockShares> unmodifiableList = Collections.unmodifiableList(new ArrayList<>(sharesMap.values()));
        return unmodifiableList;
    }

    /**
     * If the transaction is not an instanceof StockTransaction, throw an IllegalArgumentException.
     *
     * If tx.getType() is BUY, do the following:
     *         If there aren't enough shares of the stock available for purchase, throw an InvalidTransactionException.
     *         The total amount of cash needed for the tx  = tx.getQuantity() * tx.getStock().getPrice(). If the patron doesn't have enough cash in his SavingsAccount for this transaction, throw InsufficientAssetsException.
     *         If he does have enough cash, do the following:
     *         1) reduce available share of StockListing by tx.getQuantity()
     *         2) reduce cash in patron's savings account by tx.getQuantity() * StockListing.getPrice()
     *         3) create a new StockShare for this stock with the quantity set to tx.getQuantity() and listing set to tx.getStock() (or increase StockShare quantity, if there already is a StockShare instance in this account, by tx.getQuantity())
     *         4) add this to the set of transactions recorded in this account
     *
     * If tx.getType() is SELL, do the following:
     *          //If this account doesn't have the specified number of shares in the given stock, throw an InsufficientAssetsException.
     *          //Reduce the patron's shares in the stock by the tx.getQuantity()
     *          //The revenue from the sale = the current price per share of the stock * number of shares to be sold. Use a DEPOSIT transaction to add the revenue to the Patron's savings account.
     *
     * @param tx the transaction to execute on this account
     * @see StockTransaction
     */
    @Override
    public void executeTransaction(Transaction tx) throws InsufficientAssetsException,InvalidTransactionException {
        if (!(tx instanceof StockTransaction)) {
            throw new InvalidTransactionException("Invalid transaction", tx.getType());
        }
        StockTransaction stockTransaction = (StockTransaction) tx;
        StockListing stockListing = stockTransaction.getStock();
        //If tx.getType() is BUY, do the following:
        if (stockTransaction.getType() == TxType.BUY){
            int requestedQuantity = stockTransaction.getQuantity();
            int availableShares = stockListing.getAvailableShares();
            
              //If there aren't enough shares of the stock available for purchase, throw an InvalidTransactionException.
              //System.out.println(requestedQuantity + availableShares);
              if(availableShares < requestedQuantity){
              //if(sharesMap.containsKey(stockListing.getTickerSymbol()) && sharesMap.get(stockListing.getTickerSymbol()).getQuantity() < requestedQuantity) {
                throw new InvalidTransactionException("Invalid transaction", stockTransaction.getType());
                //done creo
            }
            //The total amount of cash needed for the tx = tx.getQuantity() * tx.getStock().getPrice(). If the patron doesn't have enough cash in his SavingsAccount for this transaction, throw InsufficientAssetsException.
            double cashNeeded = stockTransaction.getQuantity() * stockTransaction.getStock().getPrice();
            if (getPatron().getSavingsAccount().getValue() < cashNeeded) {
                throw new InsufficientAssetsException(stockTransaction, getPatron());
                //done creo
            }
            //If he does have enough cash, do the following:

                // 1  reduce available share of StockListing by tx.getQuantity()
                //stockTransaction.getStock().reduceAvailableShares(stockTransaction.getQuantity());//done
                stockListing.reduceAvailableShares(requestedQuantity);//done

                //2   reduce cash in patron's savings account by tx.getQuantity() * StockListing.getPrice()
                //double cashToReduce = tx.getQuantity() * stockListing.getPrice();
                //Transaction reduceCash = new Transaction(TxType.WITHDRAW, cashToReduce);
                CashTransaction reduceCash = new CashTransaction(TxType.WITHDRAW, cashNeeded);
                getPatron().getSavingsAccount().executeTransaction(reduceCash);
                //savingsAccount.executeTransaction(reduceCash);    //done creo

                // 3   create a new StockShare for this stock with the quantity set to tx.getQuantity() and listing set to tx.getStock() (or increase StockShare quantity, if there already is a StockShare instance in this account, by tx.getQuantity())

                /*if (sharesMap.containsKey(stockListing.getTickerSymbol())) {
                    StockShares stockShares = sharesMap.get(stockListing.getTickerSymbol());
                    stockShares.setQuantity(stockShares.getQuantity() + stockTransaction.getQuantity());
                } else {
                    sharesMap.put(stockListing.getTickerSymbol(), new StockShares(stockListing));
                }*/
                StockShares existingShares = sharesMap.get(stockTransaction.getStock().getTickerSymbol());
                if (existingShares != null) {
                    existingShares.setQuantity(existingShares.getQuantity() + requestedQuantity);
                } 
                else {
                    existingShares = new StockShares(stockTransaction.getStock());
                    existingShares.setQuantity(requestedQuantity);
                    //sharesMap.put(stockListing.getTickerSymbol(), new StockShares(stockListing));
                }
                sharesMap.put(stockTransaction.getStock().getTickerSymbol(), existingShares);
                //4    add this to the set of transactions recorded in this account
                //transactions.add(new StockTransaction(stockListing, stockTransaction.getType(), stockTransaction.getQuantity()));
                transactions.add(stockTransaction);
            }
            //If tx.getType() is SELL, do the following:
            else if (stockTransaction.getType() == TxType.SELL){

                StockShares existingShares = sharesMap.get(stockListing.getTickerSymbol());
                int existingQuantity = (existingShares != null) ? existingShares.getQuantity() : 0;
                int requestedQuantity = stockTransaction.getQuantity();
                
                //If this account doesn't have the specified number of shares in the given stock, throw an InsufficientAssetsException.
                if (existingQuantity < requestedQuantity) {
                    throw new InsufficientAssetsException(stockTransaction, getPatron());
                }
                //Reduce the patron's shares in the stock by the tx.getQuantity()
                    existingShares.setQuantity(existingQuantity - requestedQuantity);
            
                //The revenue from the sale = the current price per share of the stock * number of shares to be sold. Use a DEPOSIT transaction to add the revenue to the Patron's savings account.
                double currentPricePerShare = stockListing.getPrice();
                int numberOfSharesToSell = stockTransaction.getQuantity();

                double totalPrice = currentPricePerShare * numberOfSharesToSell;
                CashTransaction depositTransaction = new CashTransaction(TxType.DEPOSIT, totalPrice);
                getPatron().getSavingsAccount().executeTransaction(depositTransaction);
                transactions.add(stockTransaction);
            }
        }

    /**
     * the value of a BrokerageAccount is calculated by adding up the values of each StockShare.
     * The value of a StockShare is calculated by multiplying the StockShare quantity by its listing's price.
     * @return
     */
    @Override
    public double getValue() {
        double value = 0.0;
        for (StockShares stockShares : sharesMap.values()) {
            value += stockShares.getQuantity() * stockShares.getListing().getPrice();
        }
        return value;
    }
}