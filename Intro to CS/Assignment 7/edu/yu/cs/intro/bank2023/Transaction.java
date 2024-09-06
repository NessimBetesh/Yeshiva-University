package edu.yu.cs.intro.bank2023;

/**
 * Instances model a single transaction.
 */
interface Transaction extends Comparable<Transaction>{
    /**
     * the four types of transactions our system can process
     */
    enum TxType{BUY, SELL, DEPOSIT, WITHDRAW}

    /**
     * @return which type of transaction is this?
     */
    TxType getType();

    /**
     * @return timestamp of transaction. Value of nanoTimeStamp must be set at time of construction to the return value of System.nanoTime()
     * @see System#nanoTime()
     */
    long getNanoTimestamp();

    /**
     * Java 8 added default methods to interfaces.See https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html
     * @param other the object to be compared to.
     * @return
     */
    default int compareTo(Transaction other) {
        if(this.getNanoTimestamp() < other.getNanoTimestamp()){
            return -1;
        }
        if(this.getNanoTimestamp() == other.getNanoTimestamp()){
            return 0;
        }
        return 1;
    }

}
