public class bounce {
	static String CLS = "\u001b[2J";
	static String HOME = "\u001b[H";
	static String BOLD = "\u001b[1m";
	static String FIRE = "\uD83D\uDD25";
	static String BELL="\u0007"; //in Windows 10, this plays the "Critical Stop" system sound
	static String HIDE_CURSOR="\u001b[?25l";
	static String SHOW_CURSOR="\u001b[?25h";
	static int delay = 100; //how many milliseconds each frame will remain on the screen before the next one is drawn
	
	public static void main(String[] args){
	System.out.println(CLS + BOLD + HIDE_CURSOR);
	base(args[i]);
}
	public static void base(){
		System.out.println(HOME);
		System.out.println();
		System.out.println();
		System.out.println("================================");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("================================");
	} 
	public static void animation(){
		for (int i = 0;i < 6 ;i++ ) {
			
		}
	}
}