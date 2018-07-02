package prova.model;

import java.util.ArrayList;
import java.util.List;

public class Passeggero {

	/*Passeggeri li devo distinguere, quindi uso id,
	 * e devo tener traccia del numero di voli presi da questo, perchè
	 * la simulazione termina dopo che ciascun passeggero ha preso V voli,
	 * e devo stampare il ritardo complessivo (“ARRIVAL_DELAY”) accumulato da ciascun passeggero.*/
	
	
	private int passeggeroId;
	private int voli;
	private double delay;
	
	public Passeggero (int passeggeroId, int voli) {
		
		this.passeggeroId = passeggeroId;
		this.voli = voli; //lo inizzializzo col numero di voli max che questo deve fare
		this.delay = 0;
	}
	

	public Passeggero() {
		super();
	}


	public int getPasseggeroId() {
		return passeggeroId;
	}
	public void setPasseggeroId(int passeggeroId) {
		this.passeggeroId = passeggeroId;
	}
	
	
	public int getVoli() {
		return voli;
	}
	public void setVoli(int voli) {
		this.voli = voli;
	}
	
	
	public double getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	
	// decremento il numero di voli ogni volta che viene effettuato un viaggio
	//quando arriverà a zero non dovrà più viaggiare
	public void voloEffettuato() {
		this.voli --;
	}
	
	public void accumuloRitardo (double ritardo) {
		this.delay += ritardo;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + passeggeroId;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Passeggero other = (Passeggero) obj;
		if (passeggeroId != other.passeggeroId)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {                                     //+1 erchè parto da 0
		return String.format("Passeggero: %d - Ritado totale: %.2f", this.passeggeroId+1, this.delay);
	}


}
