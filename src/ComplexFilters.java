
import java.awt.Color;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Creating complex image filters 
 * 
 * @author Francisca Barros
 * @contact 1210099@isep.ipp.pt
 */
public class ComplexFilters {
    
    ArrayList<String> file_names;
    ArrayList<Color[][]> images;

    // Constructor with filename for source image
    ComplexFilters(ArrayList<String> filenames) {
        this.images = new ArrayList<>();
        this.file_names = filenames;
        // Load each image into the array list
        for (String file_name: this.file_names) {
            Color[][] loaded_image = Utils.loadImage(file_name);
            this.images.add(loaded_image);
        }
    }

    // Clean Image Filter for N-Images.
    public void CleanImageFilterImproved(String outputFile,  String execution_option, int number_of_threads) throws IOException {

        // Temporary image, used for the final product. 
        Color[][] tmp = Utils.copyImage(this.images.get(0));

        switch (execution_option) {

            // Sequential Approach
            case "sequential":
                // Go through the color-array horizontally and vertically (get the pixel in (x,y))
                for (int i = 0; i < tmp.length; i++) {
                    for (int j = 0; j < tmp[i].length; j++) {
                        
                        ArrayList<Color> pixels = new ArrayList<>();

                        // Fetch values of pixel (x,y) in each image and append to array
                        for (int a = 0; a < this.images.size(); a++) {
                            Color pixel = this.images.get(a)[i][j];
                            pixels.add(pixel);
                        }

                        // Calculate the average pixel of the array
                        Color average_pixel = calculateAverage(pixels);

                        // The chosen one
                        Color chosen_one = calculateDistanceAndGetResult(average_pixel, pixels);

                        tmp[i][j] = chosen_one;
                    }
                }
                break;

            // Multithreaded Approach, no ThreadPools
            case "multithread":
                Thread[] threads = new Thread[number_of_threads];
                for (int i = 0; i < number_of_threads; i++) {
                    threads[i] = new Thread(new ComplexFilterThread(number_of_threads, i, tmp, this.images));
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
            
            // ThreadPools approach
            case "threadpool":
                ExecutorService executor = Executors.newFixedThreadPool(number_of_threads);
                for (int i = 0; i < number_of_threads; i++) {
                    executor.execute(new ComplexFilterThread(number_of_threads, i, tmp, this.images));
                }
                executor.shutdown();
                while(!executor.isTerminated()) {}
                break;
        }
        
        Utils.writeImage(tmp, outputFile);        

    }

    /**********
    * THREADS *
    ***********/

    private class ComplexFilterThread implements Runnable {

        private Color[][] image;
        private ArrayList<Color[][]> images;
        private int total_threads;
        private int thread_id;

        public ComplexFilterThread(int total_threads, int thread_id, Color[][] image, ArrayList<Color[][]> images) {
            this.total_threads = total_threads;
            this.thread_id = thread_id;
            this.image = image;
            this.images = images;
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
                        ArrayList<Color> pixels = new ArrayList<>();

                        // Fetch values of pixel (x,y) in each image and append to array
                        for (int a = 0; a < this.images.size(); a++) {
                            Color pixel = this.images.get(a)[i][j];
                            pixels.add(pixel);
                        }

                        // Calculate the average pixel of the array
                        Color average_pixel = calculateAverage(pixels);

                        // The chosen one
                        Color chosen_one = calculateDistanceAndGetResult(average_pixel, pixels);

                        image[i][j] = chosen_one;
                    }
                }
            }
        }
        
    }


    /****************
    * HELPER METHOD *
    *****************/

    /**
     * Calculates and returns the pixel at the minimal distance to the average pixel.
     * 
     * @param average_pixel    the average of all (x,y)
     * @param pixels           list of (x,y) being considered
     */
    public Color calculateDistanceAndGetResult(Color average_pixel, ArrayList<Color> pixels) {

        int distance = 0;
        Color lowest_pixel = null;
        
        for (Color pixel: pixels) {
            int tmp = (int) Math.round(
                Math.sqrt(
                    (Math.pow((pixel.getRed() - average_pixel.getRed()), 2) + 
                    Math.pow((pixel.getGreen() - average_pixel.getGreen()), 2) + 
                    Math.pow((pixel.getBlue() - average_pixel.getBlue()), 2)
                ))
            );

            // Edge case, first one
            if (distance == 0 && lowest_pixel == null) {
                distance = tmp;
                lowest_pixel = pixel;
            }

            if (distance != 0 && tmp < distance) {
                distance = tmp;
                lowest_pixel = pixel;
            }
        }

        return lowest_pixel;
    }

    /**
     * Calculates and returns the average pixel.
     * 
     * @param pixels           list of (x,y) being considered
     */
    public Color calculateAverage(ArrayList<Color> pixels) {

        int sum_red = 0;
        int sum_green = 0;
        int sum_blue = 0;

        for (Color pixel: pixels) {
            sum_red += pixel.getRed();
            sum_green += pixel.getGreen();
            sum_blue += pixel.getBlue();
        }

        return new Color(Math.round(sum_red/pixels.size()), 
                        Math.round(sum_green/pixels.size()), 
                        Math.round(sum_blue/pixels.size()));
    }

}
