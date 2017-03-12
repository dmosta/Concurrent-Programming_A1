import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.Random;

// This class defines what each thread will do
class Drawer implements Runnable {
    // each has a colour
    private int rgb;
    // If we use a single Random object for everyone we
    // introduce an(other) bottleneck, so we give each
    // thread its own Random object.
    private Random r;

    // Number of pixels we need to set
    public int done;

    public Drawer(int rgb,int todo) {
        this.rgb = rgb;
        r = new Random();
        done = todo;
    }

    public void run() {
        // While we haven't finished
        while (done>0) {
            int x,y;
            // Choose random coordinates
            x = r.nextInt(q2.width);
            y = r.nextInt(q2.height);
            // Now, we need to _atomically_ check and set a pixel.  If we use
            // a global lock then we won't get much (any) speedup, so we divide
            // the protection of pixels among the different locks.
            synchronized(q2.locks[(x+y*q2.width)%q2.MAXLOCKS]) {
                int c = q2.img.getRGB(x,y);
                if (c==0) {
                    q2.img.setRGB(x,y,rgb);
                    done--;
                } 
            }
        }
    }
}

public class q2 {

    public static BufferedImage img;

    // image size and number of pixels
    public static int pcount;
    public static int width;
    public static int height;

    // given from template code
    public static int rgbFromN(int n) {
        int c = 255<<24;
        
        switch(n) {
        case 0:  c |= 255<<16;
            break;
        case 1:  c |= 255<<8;
            break;
        case 2:  c |= 255;
            break;
        case 3:  c |= 127<<16 | 127<<8;
            break;
        case 4:  c |= 127<<16 | 127;
            break;
        case 5:  c |= 127<<8 | 127;
            break;
        default: c |= 99<<16 | 123<<8 | 17;
        }
        return c;
    }

    // To ensure each pixel is set at most once we will use an array of locks
    public static int MAXLOCKS = 4;
    public static Object[] locks;

    public static void main(String[] args) {
        Random r = new Random();
        try {
            // arg 0 is the width
            width = Integer.parseInt(args[0]);
            // arg 1 is the height
            height = Integer.parseInt(args[1]);
            // arg 2 is the number of threads
            int nt = Integer.parseInt(args[2]);
            // arg 3 is the number of locks, and is optional.
            // We default to 4 locks, but as speedup is an interplay
            // between image size, threads, and the number of locks
            // it can also be specified.
            if (args.length>3) 
                MAXLOCKS = Integer.parseInt(args[3]);

            img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            // initial the total number of pixels in the image
            pcount = width*height;
            for (int i=0;i<width;i++) {
                for (int j=0;j<height;j++) {
                    img.setRGB(i,j,0);
                }
            }

            // Allocate our array of locks
            locks = new Object[MAXLOCKS];
            for (int i=0;i<MAXLOCKS;i++)
                locks[i] = new Object();

            // And an array of threads, and Drawer objects
            Thread[] threads = new Thread[nt];
            Drawer[] d = new Drawer[nt];

            // Create the Drawer's.  These each have a unique colour,
            // and expected number of pixels to fill
            for (int i=0;i<nt;i++) {
                d[i] = new Drawer(rgbFromN(i),pcount/nt);
            }
            // give any extra pixels to the last Drawer
            if ((pcount/nt)*nt!=pcount)
                d[nt-1].done = pcount-(pcount/nt)*(nt-1);

            long t = System.currentTimeMillis();

            // Ok, off we go, letting them draw.
            for (int i=0;i<nt;i++) {
                threads[i] = new Thread(d[i]);
                threads[i].start();
            }

            // And wait for them to all finish.
            for (int i=0;i<nt;i++) {
                try {
                    threads[i].join();
                } catch(InterruptedException ie) {
                    System.out.println("IE: "+ie);
                }
            }

            System.out.println(System.currentTimeMillis()-t);
                
            // write out the image
            File outputfile = new File("outputimage.png");
            ImageIO.write(img, "png", outputfile);

        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }
}
