
import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Creating image filters 
 * 
 * @author Jorge Coelho
 * @contact jmn@isep.ipp.pt
 * @version 1.0
 * @since 2022-01-04
 */
public class Filters {
    
    String file;
    Color image[][];


    // Constructor with filename for source image
    Filters(String filename) {
        this.file = filename;
        image = Utils.loadImage(filename);
    }


    // Highlight Fires
    public void HighLightFireFilter(String outputFile, float threshold, String execution_option, int number_of_threads) throws IOException {
        Color[][] tmp = Utils.copyImage(image);

        switch (execution_option) {

            // Sequential Approach
            case "sequential": 
                for (int i = 0; i < tmp.length; i++) {
                    for (int j = 0; j < tmp[i].length; j++) {
        
                        // fetches values of each pixel
                        Color pixel = tmp[i][j];
                        int r = pixel.getRed();
                        int g = pixel.getGreen();
                        int b = pixel.getBlue();
        
                        // takes average of color values
                        int avg = (r + g + b) / 3;
        
                        if(r > avg*threshold && g < 100 && b < 200)
                            // outputs red pixel
                            tmp[i][j] = new Color(255, 0, 0);
                        else
                            // outputs grey pixel
                            tmp[i][j] = new Color(avg, avg, avg);
                    }
                }
                break;

            // Multithreaded Approach, no ThreadPools
            case "multithread":
                // int threads_count = number_of_threads;
                Thread[] threads = new Thread[number_of_threads];
                for (int i = 0; i < number_of_threads; i++) {
                    threads[i] = new Thread(new FilterThread(number_of_threads, i, tmp, threshold));
                    threads[i].start();
                }
                try {
                    for (int k = 0; k < threads.length ; k++) {
                        threads[k].join();
                    }
                } catch (Exception e) {
                    System.out.println("Interrupted!");
                }
                break;

            // ThreadPool approach
            case "threadpool":
                ExecutorService executor = Executors.newFixedThreadPool(number_of_threads);
                for (int i = 0; i < number_of_threads; i++) {
                    executor.execute(new FilterThread(number_of_threads, i, tmp, threshold));
                }
                executor.shutdown();
                while(!executor.isTerminated()) {}
        }

        // Write to file
        Utils.writeImage(tmp, outputFile);
    }

    /**********
    * THREADS *
    ***********/

    private class FilterThread implements Runnable {

        private Color[][] image;
        private int total_threads;
        private int thread_id;
        private float threshold;

        public FilterThread(int total_threads, int thread_id, Color[][] image, float threshold) {
            this.total_threads = total_threads;
            this.thread_id = thread_id;
            this.image = image;
            this.threshold = threshold;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (image) {
                int width = image.length;
                int height = image[0].length;
                // Go through the color-array horizontally and vertically (get the pixel in (x,y))
                for (int i = 0; i < width; i++) {
                    for (int j = (height / total_threads) * thread_id; 
                            j < (thread_id < (total_threads-1) ? (height / total_threads * (thread_id + 1)) : height); 
                            j++) 
                    {
                        // fetches values of each pixel
                        Color pixel = image[i][j];
                        int r = pixel.getRed();
                        int g = pixel.getGreen();
                        int b = pixel.getBlue();

                        // takes average of color values
                        int avg = (r + g + b) / 3;

                        if(r > avg*threshold && g < 100 && b < 200) {
                            // outputs red pixel
                            image[i][j] = new Color(255, 0, 0);
                        }
                        else {
                            image[i][j] = new Color(avg, avg, avg);
                        }
                    }
                }
            }
        }
    } 


}
