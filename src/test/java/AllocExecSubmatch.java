import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 10/29/14
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class AllocExecSubmatch {

    public static void main(String[] args) throws IOException, ParseException {
        List<Integer> allocs = new ArrayList<Integer>(Arrays.asList(10, 5, 3, 7, 25, 50));

        testMatch(allocs, 70);  // Positive
        testMatch(allocs, 25);  // Positive
        testMatch(allocs, 22);  // Positive
        testMatch(allocs, 21);  // Negative
        testMatch(allocs, 1000);// Negative
    }

    public static List<Integer> match(List<Integer> allocs, int exec) throws IllegalArgumentException {
        Collections.sort(allocs);		// Should be done in DB query.
        Collections.reverse(allocs);	// Should be done in DB query.
        List<Integer> res = new ArrayList<Integer>(allocs.size()/2);
        matchR(allocs, exec, res);
        return res;
    }

    private static List<Integer> matchR(List<Integer> allocs, int exec, List<Integer> res) throws IllegalArgumentException {
        for(Integer alloc : allocs) {
            if(alloc > exec) {
                return matchR(allocs.subList(1, allocs.size()), exec, res);
            }
            // exec <= alloc
            res.add(alloc);
            if(alloc == exec) {
                return res;
            }
            // exec < alloc
            exec -= alloc;
            matchR(allocs.subList(1, allocs.size()), exec, res);
            return res;
        }
        throw new IllegalArgumentException("There is no allocations combinations for this execution");
    }

    private static void testMatch(List<Integer> allocs, int exec) {
        try {
            List<Integer> res = match(allocs, exec);
            // Test result
            int sum = 0;
            for(Integer alloc : res) {
                sum += alloc;
            }
            System.out.print(sum == exec ? "OK" : "Error: " + sum + " <> " + exec);
            System.out.println("\t" + allocs + "\t for " + exec);

        } catch(IllegalArgumentException e) {
            System.err.println(e.getMessage() + " (" + exec + ")");
        }
    }
}