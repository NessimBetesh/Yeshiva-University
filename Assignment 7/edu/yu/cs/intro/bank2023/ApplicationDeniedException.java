package edu.yu.cs.intro.bank2023;
/**
 * an exception to be thrown by the bank if it has reason to refuse to create an account
 * @see Bank#openNewSavingsAccount(Patron)
 * @see Bank#openNewBrokerageAccount(Patron)
 */
public class ApplicationDeniedException extends Exception{
    /**
     * @param message an informative message, describing why the application was denied
     */
    public ApplicationDeniedException(String message){
        super(message);
    }
}