import java.util.*;

public class test{

    public static void main(String []args){
        Scanner in = new Scanner(System.in);

        String line = in.nextLine().trim();
        if (line.toLowerCase().startsWith("@")) {
            String[] splitMsg = line.split(" ", 2);
            System.out.println(Arrays.toString(splitMsg));
        }
    }
}