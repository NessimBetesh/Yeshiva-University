public class StudentAccount{
	private static double balance = 46000;
    private static double loansTotal = 0;
    private static int loanAmountLimit = 10000;

	public static void main (String[] args){
	System.out.println("Your opening balance is: $46000.0");
		for (int i = 0; i < args.length; i++) {
		
		if (args[i].equals ("pay")){
			if (i + 1 == args.length) {		
			System.out.println("Missing amount");
			System.out.println("Your closing balance is: $" + balance); 
			return;
		}
			submitPayment(args[i + 1]);
			i++;
		}
		else if (args[i].equals ("borrow")) {
			if (i + 1 == args.length) {		
			System.out.println("Missing amount");
			System.out.println("Your closing balance is: $" + balance);
			return;
		}
			takeLoan(args[i + 1]);
			i++;
		}
		else if (args[i].equals ("paybackLoan")) {
			if (i + 1 == args.length) {		
			System.out.println("Missing amount");
			System.out.println("Your closing balance is: $" + balance);
			return;
		}
			paybackLoan(args[i + 1]);
			i++;
		}
		else if (args[i].equals ("scholarship")) {
			if (i + 1 == args.length) {		
			System.out.println("Missing amount");
			System.out.println("Your closing balance is: $" + balance);
			return;
		}
			applyScholarship(args[i + 1]);
			i++;
		}
		else if (args[i].equals ("activityFee")) {
			if (i + 1 == args.length) {		
			System.out.println("Missing amount");
			System.out.println("Your closing balance is: $" + balance);
			return;
		}
			addActivityFee(args[i + 1]);
			i++;
		}
		else{
			System.out.println("Invalid operation: " + args[i]);
			System.out.println("Your closing balance is: $" + balance);
			return;
		}
	}
	
	System.out.println("Your closing balance is $" + balance);
}
	public static boolean addActivityFee(String amount){
		double amountDouble = getValidDollarAmount(amount);
		if (amountDouble < 0) {
			System.out.println ("Invalid fee amount: " + amount);
			return false;
		}
			balance += amountDouble;
			System.out.println("Activity fee applied. Your balance is now: $" + balance);
			return true;
	}
	public static boolean applyScholarship(String amount){
		double amountDouble = getValidDollarAmount(amount);

		if (amountDouble < 0){
			System.out.println ("Invalid scholarship amount: " + amount);
			return false;
		}
		if (amountDouble > balance) {
		    System.out.println("Scholarship rejected: scholarship can't exceed current balance. Student's current balance is $" + balance);
		    return false;
		}
		else{
			balance -= amountDouble;
			System.out.println("Scholarship for $" + amountDouble + " has been applied to your balance. Your current balance is now $" + balance);
			return true;
		}
	}
	public static double submitPayment(String amount){
		double amountDouble = getValidDollarAmount(amount);
		double balanceDouble;
		if (amountDouble == -1){
			System.out.println("Invalid payment amount: " + amount);
			return -1;
		}
		if (amountDouble >= balance){
			balanceDouble = amountDouble - balance;
			balance = 0;
			System.out.println("Balance is now zero");
			return balanceDouble;
		}
		if (amountDouble < balance){
			balance = balance - amountDouble;
			return balance;
		}
		return balance;
	}
	public static double takeLoan(String amount){
		double loanAmount = getValidDollarAmount(amount);

        if (loanAmount < 0.0) {
            System.out.println("Invalid loan amount: " + amount);
            return -1;
        }

        if (loansTotal + loanAmount > loanAmountLimit) {
            System.out.println("Loan application rejected: total loans may not exceed $10,000, and you have already borrowed $" + loansTotal);
            return -1;
        }
        if (loanAmount > balance) {
        System.out.println("Loan application rejected: loan can't exceed current balance. Student's current balance is $" + balance);
        return -1;
    }
    	balance -= loanAmount;
        loansTotal += loanAmount;
        return balance;
    }
	public static double paybackLoan(String amount){
		double amountDouble = getValidDollarAmount(amount);
		if (amountDouble == -1){
			System.out.println("Invalid payback amount: " + amount);
			return -1;
		}
		if (loansTotal ==  0){
			System.out.println("No outstanding Loan");
			return -1;
		}
		if (amountDouble > loansTotal){
			double loansRemain = amountDouble - loansTotal;
			loansTotal = 0;
			System.out.println("Loans fully paid off");
			return loansRemain;
		}
		if (amountDouble <=	 loansTotal){
			loansTotal = loansTotal - amountDouble;
			if(loansTotal == 0){
				System.out.println("Loans fully paid off");
			}
			return loansTotal;
		}
		return balance;
	}
	public static double getAccountBalance(){
		return balance;
	}
	public static void setAccountBalance(double value){
		balance = value;
	}
	public static double getLoansTotal(){
		return loansTotal;
	}
	public static double getValidDollarAmount(String str){
		double strDouble = -1;
		    	if (!(str.startsWith("$"))) {
		    	return -1;				
		    }
    		try {
        		if (str.startsWith("$")) {
            	str = str.substring(1);
        	}

        		strDouble = Double.parseDouble(str);

        		if (strDouble < 0.0) {
            		return -1;
        			}

        				String[] parts = str.split("\\.");

        				if (parts.length == 2){
        					if (Integer.parseInt(parts[1]) > 99){

        						return -1;
        					}
        				}
        					return strDouble;
    						} catch (NumberFormatException e) {
        						return -1;
    						}
}
    public static boolean isValidOperation(String operator){
    	if (operator.equals("pay") || operator.equals("borrow") || operator.equals("paybackLoan") || operator.equals("scholarship") || operator.equals("activityFee"))
    	return true;
  		else{
    		return false;
  			}
  		}
 }