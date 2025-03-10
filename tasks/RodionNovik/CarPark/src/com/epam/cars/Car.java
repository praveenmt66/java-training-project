package com.epam.cars;
/**
 * @author epam0010
 *
 */
public abstract class Car implements Comparable<Object> {
	private String name;
	private int cost;
	private float consumption;
	

	public Car(){
		this("No Name",0,0.0F);
	}
	public Car(String name,int cost,float consumption){
		this.name=name;
		this.cost=cost;
		this.consumption=consumption;
	}

	
	public abstract String getType();
	
	public int compareTo(Object obj) {
		Car tmp = (Car) obj;
		if (this.consumption < tmp.consumption) // current less than received
		{
			return -1;
		} else if (this.consumption > tmp.consumption) // current larger than
														// received
		{
			return 1;
		}
		return 0; // both objects are equals
	}
	 
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public float getConsumption() {
		return consumption;
	}

	public void setConsumption(float consumption) {
		this.consumption = consumption;
	}
	
}