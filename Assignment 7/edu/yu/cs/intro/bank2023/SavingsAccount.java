package edu.yu.cs.intro.bank2023;
import edu.yu.cs.intro.bank2023.Transaction.TxType;

public class SavingsAccount extends Account{
    private double balance;
   
    protected SavingsAccount(int accountNumber, Patron patron) {
        super(accountNumber, patron);
    }

    /**
     * for a DEPOSIT transaction: increase the balance by transaction amount
     * for a WITHDRAW transaction: decrease the balance by transaction amount
     * add the transaction to the transaction history of this account
     * @param tx
     * @return
     * @throws InvalidTransactionException thrown if tx is not a CashTransaction
     */
    @Override
    public void executeTransaction(Transaction tx) throws InsufficientAssetsException,InvalidTransactionException {
        if(!(tx instanceof CashTransaction)){
            throw new InvalidTransactionException("Invalid transaction", tx.getType());
        }
        CashTransaction cashTransaction = (CashTransaction) tx;
        if(cashTransaction.getType() == TxType.DEPOSIT){
            this.balance += cashTransaction.getAmount();
        }
        else if(cashTransaction.getType() == TxType.WITHDRAW){
            if(this.balance < cashTransaction.getAmount()){
                throw new InsufficientAssetsException(cashTransaction, getPatron());
            }
            this.balance -= cashTransaction.getAmount();
        }
        this.transactions.add(cashTransaction);
    }

    /**
     * @return the account's balance
     */
    @Override
    public double getValue() {
        return this.balance;
    }
}