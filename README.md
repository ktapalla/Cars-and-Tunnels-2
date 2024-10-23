# README - Cars and Tunnels pt. 2

This program builds on the traffic control system for vehicles (cars and sleds) sharing a system of tunnels from part 1 by making tunnel entry based on vehicle priority. There are constraints on which vehicles can be in a tunnel at any given time. This improved version of the project builds off of part 1 by adding Java condition variables to avoid busy-waiting, unless advantageous, and by adding a priority scheduler. This program was submitted as an assignment for an Operating Systems class. As instructed, only the CarsTunnels package was edited to complete the assignment. All other packages/files outside of it were not edited in any way or changed from the original files provided. The BasicJava file used is from part 1 and has not been edited as well. 

## Installation and Execution 

This project contains no main method and relies only on the JUnit tests provided from the class. The JUnit file to run for testing is the PrioritySchedulerTest.

## Tunnel Constraints 

The Tunnel class being implemented is the same as from part 1 and abides by the same following constraints: 

* Each tunnel only has one lane so all vehicles traveling on one must be heading in the same direction 
* Only 3 cars can be in a tunnel at any given time 
* Only 1 sled can be in a tunnel at any given time 
* Cars and sleds can't share a tunnel 

## Priority Scheduler 

A vehicle's priority is a number between 0 and 4 (inclusive), where the higher the number, the higher the priority. The PriorityScheduler keeps references to BasicTunnels as private member variables inherited form Scheduler inside PriorityScheduler. 

When a vehicle calls enter on a PriorityScheduler, the Vehicle has to wait if there are others with a higher priority still waiting to enter a tunnel. If not, then the vehicle can enter one of the tunnels. 

When a vehicle exits a tummel on a PriorityScheduler, the scheduler has to call on the exitTunnel method on the proper tunnel that the vehicle is in. After it has exited, any waiting vehicles are signaled to try to enter a tunnel again. Vehicles with the highest priority will be allowed to enter first (assuming they follow the tunnel constraints). 