package edu.yu.cs.intro.bank2023;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * the account number, patron, and transaction history MUST be stored in instance variables of Account, NOT in other classes or subclasses.
 */
public abstract class Account {
    private final int accountNumber;
    private final Patron patron;
    protected List<Transaction> transactions;

    /**
     * @param accountNumber the account number assigned by the bank to this new account
     * @param patron the Patron who this account is being created for
     */
    protected Account(int accountNumber,Patron patron){
        this.accountNumber = accountNumber;
        this.patron = patron;
        this.transactions = new ArrayList<>();
    }

    /**
     * @return the account number
     */
    public int getAccountNumber(){
        return this.accountNumber;
    }

    /**
     * @return the Patron who owns this account
     */
    public Patron getPatron(){
        return this.patron;
    }

    /**
     * @return an unmodifiable List of transactions executed on this account. The only way to add a transaction to this account is to call executeTransaction.
     * @see #executeTransaction(Transaction)
     * @see java.util.Collections#unmodifiableList(List)
     */
    public List<Transaction> getTransactionHistory(){
        return Collections.unmodifiableList(this.transactions);
    }

    /**
     * Execute the given transaction (tx) on this account.
     * @param tx
     * @throws InsufficientAssetsException if the Patron lacks the needed assets to execute tx
     * @throws InvalidTransactionException if the type of transaction passed in is not valid/relevant to the type of account this method is called on
     */
    abstract void executeTransaction(Transaction tx) throws InsufficientAssetsException,InvalidTransactionException;

    /**
     * Note that the only way to CHANGE the value of an account is by running transactions, i.e. by calling the executeTransaction method.
     * @return returns the value of this account
     * @see #executeTransaction(Transaction)
     */
    abstract double getValue();
}