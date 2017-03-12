import java.util.Random;

// reading thread
class Thread0 implements Runnable {
    Node head;
    
    Thread0(Node head) {
        this.head = head;
    }

    public void run() {
        Node n = head;
        while(!q3.terminate) {
            try {
                Thread.sleep(q3.PRINTSLEEP);
            } catch(InterruptedException ie) { }
            System.out.print(n.c+" ");
            n = n.next;
        }
    }
}

// deleting thread
class Thread1 implements Runnable {
    Node head;
    Random r = new Random();
    
    Thread1(Node head) {
        this.head = head;
    }

    public void run() {
        Node n = head;
        while(!q3.terminate) {
            try {
                Thread.sleep(q3.ACTSLEEP);
            } catch(InterruptedException ie) { }
            // move to next node
            Node prev = n;
            n = n.next;
            if (n.c!='A' && n.c!='B' && n.c!='C' && r.nextInt(q3.ACTCHANCE)==0) {
                // Delete the next node
                prev.next = n.next;
            }
        }
    }
}

// inserting thread
class Thread2 implements Runnable {
    Node head;
    Random r = new Random();
    
    Thread2(Node head) {
        this.head = head;
    }

    public void run() {
        Node n = head;
        while(!q3.terminate) {
            try {
                Thread.sleep(q3.ACTSLEEP);
            } catch(InterruptedException ie) { }
            if (r.nextInt(q3.ACTCHANCE)==0) {
                // Insert a new node after the current one
                Node newn = new Node();
                newn.next = n.next;
                n.next = newn;
            }
            n = n.next;
        }
    }
}

class Node {
    static Random r = new Random();

    // next needs to be volatile as many threads may be accessing it
    public volatile Node next;
    // the char does not need to be volatile, as it is not changed by
    // threads and must be initialized in the constructor
    public final char c;
    Node() {
        // create a random uppercase character, not A, B, or C
        c = (char)(r.nextInt(23) + 'D');
    }
    Node(char c) {
        this.c = c;
    }
}

public class q3 {
    // chance of inserting or deleting
    public static final int ACTCHANCE = 10;
    // sleep time between actions in ms
    public static final int ACTSLEEP = 20;
    // sleep time between printing in ms
    public static final int PRINTSLEEP = 100;

    // duration in ms
    public static final int DURATION = 5000;

    // termination flag
    public static volatile boolean terminate;

    public static void main(String[] args) {
        Thread[] ts = new Thread[3];
        Node head = new Node('A');
        head.next = new Node('B');
        head.next.next = new Node('C');
        head.next.next.next = head;
        
        ts[0] = new Thread(new Thread0(head));
        ts[1] = new Thread(new Thread1(head));
        ts[2] = new Thread(new Thread2(head));

        ts[0].start();
        ts[1].start();
        ts[2].start();

        try {
            Thread.sleep(DURATION);
            terminate = true;
            ts[2].join();
            ts[1].join();
            ts[0].join();
        } catch(InterruptedException ie) { }

        System.out.println();
        Node n = head;
        do {
            System.out.print(n.c+" ");
            n = n.next;
        } while(n!=head);
        System.out.println();
    }
}
