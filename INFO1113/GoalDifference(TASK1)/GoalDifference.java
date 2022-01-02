//imports here
import java.util.Scanner;

public class GoalDifference{
    public static void main(String[] args){
        //TODO: write your code here
        
        try {
            Scanner scanner = new Scanner(System.in);
        int score1 = scanner.nextInt();
        int score2 = scanner.nextInt();
        int diff = score1 - score2;
        if (score1<0 || score2<0){
            System.out.println("Bad input");
        }
        else if (diff == 0){
            System.out.println("The match is a tie");   
        }
        else if(diff >0){
            System.out.print("The match is won by");
            System.out.print(" "+diff + " ");
            System.out.println("goals");
        }
        else if(diff <0){
            diff = 0-diff;
            System.out.print("The match is won by");
            System.out.print(" "+diff + " ");
            System.out.println("goals");
            }
            
            

        //  Block of code to try

        }
        catch(Exception e) {
        //  Block of code to handle errors
        System.out.println("Bad input");
        }
        
    }
}
