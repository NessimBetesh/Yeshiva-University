package edu.yu.cs.intro.bank2023;

import java.util.*;

public class Bank {
    private Set<Account> accounts = new HashSet<>();
    private Set<Patron> patrons = new HashSet<>();
    private StockExchange exchange;
    private int nextPatronId = 1;
    private int nextSavingsId = 1;
    private int nextBrokerageId = 1;

    /**
     * @param exchange the stock exchange on which all stock are listed
     * @throws IllegalArgumentException if exchange is null
     */
    protected Bank(StockExchange exchange){
        if(exchange == null){
            throw new IllegalArgumentException();
        }
        this.exchange = exchange;
    }
    /**
     * Create a new Patron whose ID is the next unique available Patron ID and whose Bank is set to this bank.
     * Add the new Patron to the Bank's Set of Patrons.
     * No two Patrons can have the same ID. Each ID which is assigned should be greater than the previous ID.
     * @return a new Patron with a unique ID, but no accounts
     */
    public Patron createNewPatron(){
        Patron p = new Patron(nextPatronId++, this);
        patrons.add(p);
        return p;
    }
    /**
     * Create a new SavingsAccount for the Patron.
     * The SavingsAccount's id must be the next unique account ID available.
     * No two accounts of ANY KIND can have the same ID. Each ID which is assigned should be greater than the previous ID.
     * Add the new SavingsAccount to the Bank's Set of Accounts.
     * @param p the Patron for whom the account is being created
     * @return the SavingsAccount's id
     * @throws ApplicationDeniedException thrown if Patron already has a SavingsAccount
     * @throws IllegalArgumentException if p is null
     */
    public int openNewSavingsAccount(Patron p) throws ApplicationDeniedException{
        if(p == null){
            throw new IllegalArgumentException();
        }
        if(p.getSavingsAccount() != null){
            throw new ApplicationDeniedException("Patron already has a savings account");
        }
        SavingsAccount savingsAccount = new SavingsAccount(nextSavingsId++, p);
        p.setSavingsAccount(savingsAccount);
        accounts.add(savingsAccount);
        return savingsAccount.getAccountNumber();
        /*Patron.add(p);
        return p;*/
    }

    /**
     * Create a new BrokerageAccount for the Patron.
     * The BrokerageAccount's id must be the next unique account ID available.
     * No two accounts of ANY KIND can have the same ID. Each ID which is assigned should be greater than the previous ID.
     * Add the new BrokerageAccount to the Bank's Set of Accounts.
     * @param p the Patron for whom the account is being created
     * @return the BrokerageAccount's id
     * @throws ApplicationDeniedException thrown if the Patron doesn't have a SavingsAccount or DOES already have a BorkerageAccount
     * @throws IllegalArgumentException if p is null
     */
    public int openNewBrokerageAccount(Patron p)throws ApplicationDeniedException{
        if(p == null){
            throw new IllegalArgumentException();
        }
        if(p.getBrokerageAccount()!= null){
            throw new ApplicationDeniedException("Patron already has a brokerage account");
        }
        if(p.getSavingsAccount() == null){
            throw new ApplicationDeniedException("Patron dosent have a savings account");
        }
        BrokerageAccount brokerageAccount = new BrokerageAccount(nextBrokerageId++, p);
        accounts.add(brokerageAccount);
        p.setBrokerageAccount(brokerageAccount);
        //System.out.println("Bank opennewbrokerage   " + brokerageAccount);
        return brokerageAccount.getAccountNumber();
    }

    /**
     *
     * @return an unmodifiable set of all the accounts (both Savings and Brokerage)
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    protected Set<Account> getAllAccounts() {
        return Collections.unmodifiableSet(accounts);
    }

    /**
     *
     * @return an unmodifiable set of all the Patrons
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    protected Set<Patron> getAllPatrons() {
        return Collections.unmodifiableSet(patrons);
    }

    /**
     * @return the exchange used by this Bank
     */
    protected StockExchange getExchange() {
        return this.exchange;
    }
}