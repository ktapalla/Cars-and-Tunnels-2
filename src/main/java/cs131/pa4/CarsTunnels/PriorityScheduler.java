package cs131.pa4.CarsTunnels;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cs131.pa4.Abstract.Scheduler;
import cs131.pa4.Abstract.Tunnel;
import cs131.pa4.Abstract.Vehicle;

/**
 * The priority scheduler assigns vehicles to tunnels based on their priority
 * It extends the Scheduler class.
 * @author cs131a
 *
 */
public class PriorityScheduler extends Scheduler{

	Collection<Tunnel> tunnels;
	LinkedList<Vehicle> priList;
	private Lock lock;
	private Condition admitGo;
	private boolean canAdmit;

	/**
	 * Creates an instance of a priority scheduler with the given name and tunnels
	 * @param name the name of the priority scheduler
	 * @param tunnels the tunnels where the vehicles will be scheduled to
	 */
	public PriorityScheduler(String name, Collection<Tunnel> tunnels) {
		super(name, tunnels);
		this.tunnels = tunnels;
		this.priList = new LinkedList<Vehicle>();
		this.lock = new ReentrantLock();
		this.admitGo = this.lock.newCondition();
		this.canAdmit = true;
	}

	@Override
	/**
	 * Admits vehicles into tunnels based on priority 
	 * @param vehicle vehicle currently trying to enter a tunnel 
	 */
	public Tunnel admit(Vehicle vehicle) {
		this.lock.lock();
		try {
			int ind = 0;
			// other vehicles are waiting to enter a tunnel 
			if (!this.priList.isEmpty()) {
				this.canAdmit = false;
				int pri = vehicle.getPriority();
				// find appropriate index to add vehicle into list, based on priority 
				while (ind < this.priList.size() && pri <= this.priList.get(ind).getPriority() ) {
					ind++;
				}
			}
			// vehicle added to list at proper index 
			this.priList.add(ind, vehicle);
			// waits while all tunnels are full/can't have any new vehicles entered 
			while (this.canAdmit == false) {
				try {
					this.admitGo.await();	
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// vehicle with highest priority 
			Vehicle highestPri = this.priList.pollFirst();
			// iterates/goes over all tunnels 
			Iterator<Tunnel> iter = this.tunnels.iterator();
			while(iter.hasNext()) {
				Tunnel t = iter.next(); 
				// checks if highest priority vehicle can enter 
				if (t.tryToEnter(highestPri) ) {
					return t; 
				}
				// highest priority vehicle tried to enter all tunnels and can't, or other vehicles are still waiting to enter a tunnel
				else if ((!iter.hasNext() && !t.tryToEnter(highestPri)) || this.priList.size() > 0) {
					// highest priority can't enter tunnels, sets to false to wait again
					this.canAdmit = false;
					// current highest priorty vehicle added back to beginning of list (can't enter any tunnels)
					this.priList.add(0, highestPri);
					// waiting while highest priority can't enter 
					while (this.canAdmit == false) {
						try {
							this.admitGo.await();	
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			// sees if highest priority vehicle can enter tunnels again 
			highestPri = this.priList.pollFirst();
			for (Tunnel t2 : this.tunnels) {
				if (t2.tryToEnter(highestPri)) {
					return t2;
				}
			}	
		} finally {
			this.lock.unlock();
		}
		return null;
	}

	@Override
	/** 
	 * Removes vehicle from tunnel 
	 * @param vehicle vehicle trying to exit tunnel 
	 */
	public void exit(Vehicle vehicle) {
		this.lock.lock();
		try {
			// goes through existing tunnel to fine the one the vehicle is in and removes it from appropriate tunnle
			for (Tunnel t : this.tunnels) {
				t.exitTunnel(vehicle);
			}	
			// new vehicle can be admited 
			this.canAdmit = true;		
			// signals to try admitting a new vehicle since one was removed 
			this.admitGo.signal();
		} finally {
			this.lock.unlock();
		}


	}

}
