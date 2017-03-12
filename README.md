# Concurrent-Programming_A1

Question1:

A drone is composed of 4 separate rotors, a central controller, and a battery. The controller sets the speed of each rotor; each rotor imposes a drain on the battery based on its actual speed of rotation.
Develop a simulation of such a drone using 5 threads—1 for each rotor, and one for the controller. The controller thread simply generates requests to set random rotors to random speeds (speed values being integers in the range 0–10, inclusive) every Xms. Speed goals for individual rotors are stored in integer variables.
Each rotor thread maintains its own, internal speed value representing its actual speed. Periodically, every Y ms, it examines the goal speed set by the controller, and attempts to change its speed to the desired value. Its ability to do so, however, depends on the total battery drain imposed by all 4 rotors, where each unit of rotor speed consumes one unit of drain, and must never, under any circumstances exceed a fixed maximum of 20. The total drain is not maintained globally, however, so a rotor must inspect the current speeds of each other rotor to find out if the desired speed can be safely reached or not. If a rotor cannot reach its desired speed then it should set it to be as fast as it can. Each rotor should keep track of how many times it succeeds and how many times it fails to set its speed to the desired value, as well as the maximum speed it reaches.

Develop a program called q1 which accepts 2 integer command-line arguments, X and Y and simulates the drone running for 10s. Upon termination, for each rotor print out a line, Rotor i: checks=c, success rate=r, max=s where i is the rotor id (0. . .3), c is the total number of attempts this rotor made to set the speed, r is its success ratio in setting the speed to the goal speed, and s is the maximum speed achieved.


Question2:

In this question you will construct a multithreaded approach to filling an image with random pixels.
Template code is provided that constructs an image of specified dimensions, and is able to write out the image as a .png file. Each pixel of the image is initialized to 0. The program accepts 3 command-line arguments: width, height, and n, the total number of threads that should be used. 
Each pixel must be assigned exactly once, and each thread should be assigning an equal number of pixels (at least to the extent that width⇥height is evenly divisible by n).
Add timing code (using System.currentTimeMillis) to time the actual work of the threads (ie not including the initial image creation, or the file I/O). The program should emit as output a single integer (long) value, the time taken in milliseconds.
Your goal is to ensure you can achieve speedup over n = 1, for at least some other values of n. Time the system for each of n 2 {1, 2, 4, 8, 16} given an image size which takes at least a few hundred ms for n = 1 on your test system. In each case, execute the program at least 7 times, discarding the first timing (as cache warmup), and averaging the rest of the values. Provide an image of a plot of the average time, as well as the minimum and maximum time versus n.
Either as text within the image or as a separate .txt file (and not just as code comments), briefly explain your results in relation to your synchronization strategy and system characteristics (especially the number of cores).


Question3:

Develop a program, q3, in which 3 threads compete to traverse and modify a circular, singly linked list of single-characters. The list begins with 3 items “A”, “B”, and “C”. Thread 0 scans through the list printing out the characters it encounters (on the same output line, space- separated), and sleeping 100ms between each output.

Thread 1 scans through the list, removing each entry it encounters with a 1/10 chance, sleeping 20ms before moving onto the next entry. However, the original starting items, “A”, “B”, and “C” may never be deleted, ensuring the list will always contain at least 3 items.

Thread 2 scans through the list, inserting new entries (for random single characters which are not “A”, “B”, or “C”) after each entry it encounters, also with a 1/10 chance and sleeping 20ms before moving onto the next entry.

The simulation should run for 5s of execution, and then stop all list modifications and print out on a separate line the final contents of the linked list. Note that the potential for conflict between threads 1 and 2 mean that some nodes may not be fully or successfully inserted or deleted. Nevertheless, the integrity of the circular linked list must be guaranteed—it must remain circular, and threads should not crash or end up unable to traverse the list.

Give an implementation that does not use synchronized (or mutexes), but which still avoids race condi- tions of course. Ensure concurrency is maximized—it should be possible for all threads to be performing their actions at the same time.
