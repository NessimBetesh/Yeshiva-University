public class prueba{
    public static void main(String[] args){
    double balance = 46000.0;
    System.out.println("Your opening balance is $46000.0");
    double print = getValidDollarAmount(args[0]);
    System.out.println(print);
    }
public static double getValidDollarAmount(String str){
		 if (str.startsWith("$")) {
            str = str.substring(1);
            if (str.matches("\\d*\\.?\\d{0,2}")) {
                return Double.parseDouble(str);
            }
        }
        return -1;
    }
}