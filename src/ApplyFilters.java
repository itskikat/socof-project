import java.io.IOException;
import java.util.Scanner;

public class ApplyFilters {
 
    public static void main(String[] args) throws IOException {
		
		Scanner input = new Scanner(System.in);        
		String filePath = "";
		System.out.println("Insert the name of the file path you would like to use:");
        filePath = input.nextLine();
        System.out.println("Insert the red value threshold:");
        float threshold = input.nextFloat();
        input.close();
        Filters filters = new Filters(filePath);

        // PERFORMANCE STATS
        StringBuilder stringBuilder = new StringBuilder(); // My Table
        stringBuilder.append("------------ Highlight Fire Filter ------------ \n");
        long start, end, total;

        start = System.currentTimeMillis();
        filters.HighLightFireFilter("test_hf_seq.jpg", threshold, "sequential", 0);
        end = System.currentTimeMillis();
        total = end - start;
        stringBuilder.append("-- Sequential:  ");
        stringBuilder.append(total + " (ms) -------------------- \n");

        start = System.currentTimeMillis();
        filters.HighLightFireFilter("test_hf_mt4.jpg", threshold, "multithread", 4);
        end = System.currentTimeMillis();
        total = end - start;
        stringBuilder.append("-- MultiThreaded (4):  ");
        stringBuilder.append(total + " (ms) --------------- \n");

        start = System.currentTimeMillis();
        filters.HighLightFireFilter("test_hf_tp5.jpg", threshold, "threadpool", 5);
        end = System.currentTimeMillis();
        total = end - start;
        stringBuilder.append("-- Thread-Pool (5):  ");
        stringBuilder.append(total + " (ms) --------------- \n");

        // SHOW STATS
        System.out.println(stringBuilder.toString());


        // USED FOR PERFORMANCE ASSESSMENT
        //
        // for (int a = 2; a < 21; a++) {
        //     start = System.currentTimeMillis();
        //     filters.HighLightFireFilter("test_hf_mt" + a + ".jpg", threshold, "multithread", a);
        //     end = System.currentTimeMillis();
        //     total = end - start;
        //     stringBuilder.append("-- MultiThreaded (" + a + "):  ");
        //     stringBuilder.append(total + " (ms) -------------- \n");
        // }
        // 
        // for (int a = 2; a < 21; a++) {
        //     start = System.currentTimeMillis();
        //     filters.HighLightFireFilter("test_hf_tp" + a + ".jpg", threshold, "threadpool", a);
        //     end = System.currentTimeMillis();
        //     total = end - start;
        //     stringBuilder.append("-- Thread-Pool (" + a + "):  ");
        //     stringBuilder.append(total + " (ms) --------------- \n");
        // }
        
        
    }

}
