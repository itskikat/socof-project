import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class ApplyComplexFilters {
 
    public static void main(String[] args) throws IOException {

        ArrayList<String> file_names = new ArrayList<>();
		Scanner input = new Scanner(System.in);
        String filePath = "";
        System.out.println("Insert the name of the file paths you would like to use \n (1 per line) \n (END to stop):");
        while (input.hasNext()) {
            filePath = input.nextLine();
            if (filePath.equals("END")) {
                break;
            }
            file_names.add(filePath);
        }
        input.close();
        ComplexFilters filters = new ComplexFilters(file_names);

        // PERFORMANCE STATS
        StringBuilder stringBuilder = new StringBuilder(); // My Table
        stringBuilder.append("------------ Clean Image Filter ------------ \n");
        long start, end, total;

        start = System.currentTimeMillis();
        filters.CleanImageFilterImproved("test_ci_seq.jpg", "sequential", 0);
        end = System.currentTimeMillis();
        total = end - start;
        stringBuilder.append("-- Sequential:  ");
        stringBuilder.append(total+" (ms) ------------------ \n");

        start = System.currentTimeMillis();
        filters.CleanImageFilterImproved("test_ci_mt6.jpg", "multithread", 6);
        end = System.currentTimeMillis();
        total = end - start;
        stringBuilder.append("-- MultiThreaded (6):  ");
        stringBuilder.append(total+" (ms) ----------- \n");

        start = System.currentTimeMillis();
        filters.CleanImageFilterImproved("test_ci_tp8.jpg", "threadpool", 8);
        end = System.currentTimeMillis();
        total = end - start;
        stringBuilder.append("-- Thread-Pool (8):  ");
        stringBuilder.append(total+" (ms) ------------- \n");

        // SHOW STATS
        System.out.println(stringBuilder.toString());


        // USED FOR PERFORMANCE ASSESSMENT
        //
        // for (int a = 2; a < 21; a++) {
        //     start = System.currentTimeMillis();
        //     filters.CleanImageFilterImproved("test_ci_mt" + a + ".jpg", "multithread", a);
        //     end = System.currentTimeMillis();
        //     total = end - start;
        //     stringBuilder.append("-- MultiThreaded (" + a + "):  ");
        //     stringBuilder.append(total + " (ms) -------------- \n");
        // }
        // 
        // for (int a = 2; a < 21; a++) {
        //     start = System.currentTimeMillis();
        //     filters.CleanImageFilterImproved("test_ci_tp" + a + ".jpg", "multithread", a);
        //     end = System.currentTimeMillis();
        //     total = end - start;
        //     stringBuilder.append("-- Thread-Pool (" + a + "):  ");
        //     stringBuilder.append(total+" (ms) ------------- \n");
        // }

    }

}
