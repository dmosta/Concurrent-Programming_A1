import java.util.Random;

class Rotor implements Runnable {

    // The desired speed; volatile as it is set by the main thread
    public volatile int desired;

    // Speed is only accessed by Rotors, within synchronization
    private int speed;

    // This data is all private to each thread, at least until the thread terminates
    private int id;
    private int succeeded;
    private int failed;
    private int maxspeed;

    public Rotor(int id) {
        this.id = id;
    }

    public void run() {
        // Continue until the main thread indicates we should stop through the terminate flag
        while(q1.terminate==0) {
            // Set the speed to whatever is expected
            setSpeed(desired);
            try {
                // And wait for however long it is we should sleep between speed adjustments
                Thread.sleep(q1.inspectRate);
            } catch(InterruptedException ie) {
                // this is safe to ignore
            }
        }
        // We are done; print out our success rate and maximum speed
        System.out.println("Rotor "+id+": checks="+(succeeded+failed)+", success rate="+((failed==0) ? 1.0 : ((double)succeeded)/((double)failed))+", max="+maxspeed);
    }

    // In order to set the speed, and ensure we stay below the MAXDRAIN, we need
    // to find out the total speed of all rotors.  To ensure an instantaneously
    // accurate solution, we do that by locking each of the 4 rotors (we could've
    // used one global lock too, but we squeeze out a bit more concurrency this way)
    public void setSpeed(int d) {
        int e = 0; // total drain
        synchronized(q1.rotors[0]) {
            // notice that we add up all the speeds for everyone but ourselves
            if (id!=0) e += q1.rotors[0].speed;
            synchronized(q1.rotors[1]) {
                if (id!=1) e += q1.rotors[1].speed;
                synchronized(q1.rotors[2]) {
                    if (id!=2) e += q1.rotors[2].speed;
                    synchronized(q1.rotors[3]) {
                        if (id!=3) e += q1.rotors[3].speed;
                        // Ok, lets see if we can reach our desired speed
                        if (q1.MAXDRAIN-e>=d) {
                            speed = d;
                            if (speed>maxspeed) maxspeed = speed;
                            succeeded++;
                        } else {
                            speed = q1.MAXDRAIN-e;
                            if (speed>maxspeed) maxspeed = speed;
                            failed++;
                        }
                    }
                }
            }
        }
    }
}


public class q1 {
    // some static constaints
    public static final int MAXDRAIN = 20; // max battery drain
    public static final int ROTORMAX = 11; // max speed+1 of a rotor
    public static final int ROTORS = 4; // nb: we still assume 4 later in locking

    public static final long SIMLENGTH = 10000; // simulation time in ms

    public static int inspectRate;
    
    public static Rotor[] rotors = new Rotor[ROTORS];

    // the terminate flag is volatile, as other threads will inspect it concurrently
    public static volatile int terminate = 0;

    public static void main(String[] args) {
        Thread[] threads;
        Random r = new Random();
        try {
            int setRate = Integer.parseInt(args[0]);
            inspectRate = Integer.parseInt(args[1]);

            for (int i=0;i<ROTORS;i++) {
                rotors[i] = new Rotor(i);
                rotors[i].desired = r.nextInt(ROTORMAX);
            }

            threads = new Thread[ROTORS];
            
            long start = System.currentTimeMillis();

            for (int i=0;i<ROTORS;i++) {
                threads[i] = new Thread(rotors[i]);
                threads[i].start();
            }

            while (System.currentTimeMillis()-start<SIMLENGTH) {
                try {
                    Thread.sleep(setRate);
                } catch(InterruptedException ie) {
                    // this is safe to ignore
                }
                // choose a random rotor
                int rotor = r.nextInt(ROTORS);
                // set it to a random speed
                rotors[rotor].desired = r.nextInt(ROTORMAX);
            }

            // set our terminate flag
            terminate = 1;
            // and join all the threads.  we don't really need
            // to do this, as the program won't end until all the rotors
            // end, but it makes for a clean exit
            for (int i=0;i<ROTORS;i++) {
                try {
                    threads[i].join();
                } catch(InterruptedException ie) {
                    System.out.println("IE: "+ie);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }
}
