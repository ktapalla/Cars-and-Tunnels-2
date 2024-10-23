package cs131.pa4.CarsTunnels;

import java.util.LinkedList;

import cs131.pa4.Abstract.Tunnel;
import cs131.pa4.Abstract.Vehicle;

/**
 * 
 * The class for the Basic Tunnel, extending Tunnel.
 * @author cs131a
 *
 */
public class BasicTunnel extends Tunnel{

	private LinkedList<Vehicle> tunnel; 
	
	/**
	 * Creates a new instance of a basic tunnel with the given name
	 * @param name the name of the basic tunnel
	 */
	public BasicTunnel(String name) {
		super(name);
		this.tunnel = new LinkedList<Vehicle>(); 
	}

	@Override
	/**
	 * Checks if a vehicle can enter a tunnel 
	 * @param vehicle vehicle trying to enter tunnel 
	 * @return returns a boolean indicating whether the vehicle can enter or not
	 */
	protected synchronized boolean tryToEnterInner(Vehicle vehicle) {
		// empty tunnels can always accept a vehicle
		if (this.tunnel.isEmpty()) {
			this.tunnel.add(vehicle);
			return true;
		} 
		// vehicle is a car and there's less than 3 in the tunnel 
		else if ((vehicle instanceof Car && this.tunnel.get(0) instanceof Car ) && this.tunnel.size() < 3 ) {
			// checks if cars are headed in the same/proper direction 
			if (this.tunnel.get(0).getDirection() == vehicle.getDirection()) {
				this.tunnel.add(vehicle);
				return true;				
			} else {
				return false;
			}
		} 
		// vehicle is a sled and tunnel doesn't already have a vehicle in it
		else if(vehicle instanceof Sled && this.tunnel.size() < 1) {
			this.tunnel.add(vehicle);
			return true;
		}
		return false;
	}

	@Override
	/** 
	 * Removes a vehicle from a tunnel 
	 * @param vehicle vehicle being removed from the tunnel 
	 */
	protected synchronized void exitTunnelInner(Vehicle vehicle) {
		// tunnel isn't empty and the vehicle is inside the tunnel 
		if (!this.tunnel.isEmpty() && this.tunnel.contains(vehicle)) {
			this.tunnel.remove(vehicle);			
		}
	}
	
}
