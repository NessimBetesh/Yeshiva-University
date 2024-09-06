package edu.yu.cs.intro.bank2023;

public class Demo {
    public static void main(String[] args) {
        new Demo().run();
    }

    public void run(){
        //create a bank and stock exchange
        StockExchange exchange = new StockExchange();
        Bank bank = new Bank(exchange);

        //list some stocks on the exchange
        exchange.createNewListing("IBM",10,1000);
        StockListing listing = exchange.getStockListing("IBM");
        assert(listing != null);
        assert(listing.getAvailableShares() == 1000);
        assert(listing.getPrice() == 10);

        exchange.createNewListing("GOOG",50,10);
        listing = exchange.getStockListing("GOOG");
        assert(listing != null);
        assert(listing.getAvailableShares() == 10);
        assert(listing.getPrice() == 50);

        exchange.createNewListing("AMZN",100,10);
        listing = exchange.getStockListing("AMZN");
        assert(listing != null);
        assert(listing.getAvailableShares() == 10);
        assert(listing.getPrice() == 100);

        //print out initial info about the listings
        this.printOutStockExchangeInformation(exchange);

        //create a patron with a savings account and a brokerage account
        Patron gvir = bank.createNewPatron();
        try {
            bank.openNewSavingsAccount(gvir);
        } catch (ApplicationDeniedException e) {
            //something went horribly wrong - should be impossible to get this exception
            // at this point in this demo - bail!
            e.printStackTrace();
            System.exit(-1);
        }
        try {
            bank.openNewBrokerageAccount(gvir);
        } catch (ApplicationDeniedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        BrokerageAccount ba = gvir.getBrokerageAccount();
        //give the gvir some cash in his savings account
        try {
            gvir.getSavingsAccount().executeTransaction(new CashTransaction(Transaction.TxType.DEPOSIT,1000));
            assert(gvir.getSavingsAccount().getValue() == 1000);
        } catch (InvalidTransactionException | InsufficientAssetsException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        //buy some stock successfully
        try {
            ba.executeTransaction(new StockTransaction(exchange.getStockListing("IBM"), Transaction.TxType.BUY,1));
            ba.executeTransaction(new StockTransaction(exchange.getStockListing("GOOG"), Transaction.TxType.BUY,2));
            ba.executeTransaction(new StockTransaction(exchange.getStockListing("AMZN"), Transaction.TxType.BUY,5));
            System.out.println("Patron ID " + gvir.getId() + " bought 1 share of IBM, 2 shares of GOOG, and 5 shares of AMZN");
        } catch (InsufficientAssetsException e) {
            throw new RuntimeException(e);
        } catch (InvalidTransactionException e) {
            throw new RuntimeException(e);

        }

        //try to buy stock but fail due to insufficient assets
        try {
            ba.executeTransaction(new StockTransaction(exchange.getStockListing("IBM"), Transaction.TxType.BUY,100));
        } catch (InsufficientAssetsException e) {
            //taking advantage of fluent interface sytle: https://en.wikipedia.org/wiki/Fluent_interface
            StringBuilder sb = new StringBuilder("\nPatron ID ").append(e.getPatron().getId()).append(" tried to buy 100 shares of IBM at time ").append(e.getTx().getNanoTimestamp()).append(" but only had $").append(e.getPatron().getSavingsAccount().getValue()).append(" in his account\n");
            System.out.println(sb.toString());
        } catch (InvalidTransactionException e) {
            throw new RuntimeException(e);
        }

        //print out updated exchange information
        this.printOutStockExchangeInformation(exchange);

        //print out some data about the gvir
        this.printOutPatronInformation(gvir);

        //add another patron, move some cash around
        Patron patron = bank.createNewPatron();
        try {
            bank.openNewSavingsAccount(patron);
        } catch (ApplicationDeniedException e) {
            throw new RuntimeException(e);
        }
        SavingsAccount savings = patron.getSavingsAccount();
        try {
            savings.executeTransaction(new CashTransaction(Transaction.TxType.DEPOSIT,500));
            savings.executeTransaction(new CashTransaction(Transaction.TxType.DEPOSIT,500));
            savings.executeTransaction(new CashTransaction(Transaction.TxType.WITHDRAW,800));
            savings.executeTransaction(new CashTransaction(Transaction.TxType.DEPOSIT,573));
        } catch (InvalidTransactionException | InsufficientAssetsException e) {
            throw new RuntimeException(e);
        }
        this.printOutPatronInformation(patron);

        //change prices of some stocks
        for(StockListing stock : exchange.getAllCurrentListings()) {
            int amount = (int)(Math.random()*1000);
            System.out.println("Issuing " + amount + " new shares of " + stock.getTickerSymbol());
            stock.addAvailableShares(amount);
        }

        this.printOutStockExchangeInformation(exchange);
    }

    private void printOutStockExchangeInformation(StockExchange exchange){
        StringBuilder builder = new StringBuilder("\n*********\nCurrent data on stocks listed on this exchange:");
        for(StockListing stock : exchange.getAllCurrentListings()){
            builder.append("\n\tSymbol: ").append(stock.getTickerSymbol()).append(", Available Shares: ").append(stock.getAvailableShares()).append(", Current Price: $").append(stock.getPrice());
        }
        builder.append("\n*********\n");
        System.out.println(builder.toString());
    }

    private void printOutPatronInformation(Patron patron){
        StringBuilder sb = new StringBuilder("\n*********\nPatron ID: ").append(patron.getId()).append("\n").append("Patron Net Worth: ").append(patron.getNetWorth());
        BrokerageAccount ba = patron.getBrokerageAccount();
        if(ba != null){
            sb.append("\nPatron's brokerage account value: ").append(ba.getValue());
            sb.append("\n\nStock Shares\n");
            for(StockShares share : ba.getListOfShares()){
                sb.append("\t").append(share.getQuantity()).append(" shares of ").append(share.getListing().getTickerSymbol());
            }
            sb.append("\n\nStock Transactions:");
            for(Transaction tx : ba.getTransactionHistory()){
                StockTransaction stx = (StockTransaction)tx;
                sb.append("\n\tType: ").append(stx.getType()).append(" Quantity: ").append(stx.getQuantity()).append(" Ticker Symbol: ").append(stx.getStock().getTickerSymbol()).append(", Timestamp: ").append(stx.getNanoTimestamp());
            }
        }
        SavingsAccount savings = patron.getSavingsAccount();
        if(savings != null){
            sb.append("\n\nPatron's savings account value: ").append(savings.getValue());
            sb.append("\n\nCash Transactions:");
            for(Transaction tx : savings.getTransactionHistory()){
                CashTransaction cs = (CashTransaction)tx;
                sb.append("\n\tType: ").append(cs.getType()).append(", Amount: $").append(cs.getAmount()).append(", Timestamp: ").append(cs.getNanoTimestamp());
            }
        }
        sb.append("\n*********\n");
        System.out.println(sb.toString());
    }
}
