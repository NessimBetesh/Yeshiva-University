//KIND OF DONE FOR NOW
package edu.yu.cs.intro.bank2023;
import java.lang.Exception;

public class InvalidTransactionException extends Exception{
    private Transaction.TxType type;

    public InvalidTransactionException(String message, Transaction.TxType type){
        //if(message == null || message.isEmpty() || type == null){
          //  throw new IllegalArgumentException();
        //}
        super(message);
        this.type = type;
    }

    public Transaction.TxType getType(){
        return this.type;
    }
}