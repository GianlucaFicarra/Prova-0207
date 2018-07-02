package prova.model;

public class Event { //PARTENZA
	
	/*HO UN SOLO EVENTO - EVENTO PARTENZA:
	 * PASSEGGERO PARTE DA UN AEREOPORTO, QUANDO ARRIVA QUESTA SARà LA NUOVA PARTENZA */

	private Passeggero passeggero;
	private Flight flight;       //volo preso verso nuova destinazione dopo certo orario

	public Event(Passeggero p, Flight volo) {
		this.passeggero=p;
		this.flight=volo;
	}

	public Passeggero getPasseggero() {
		return passeggero;
	}

	public void setPasseggero(Passeggero passeggero) {
		this.passeggero = passeggero;
	}

	public Flight getFlight() {
		return flight;
	}
	public void setFlight(Flight flight) {
		this.flight = flight;
	}
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flight == null) ? 0 : flight.hashCode());
		result = prime * result + ((passeggero == null) ? 0 : passeggero.hashCode());
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
		Event other = (Event) obj;
		if (flight == null) {
			if (other.flight != null)
				return false;
		} else if (!flight.equals(other.flight))
			return false;
		if (passeggero == null) {
			if (other.passeggero != null)
				return false;
		} else if (!passeggero.equals(other.passeggero))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Passeggero %s ---Volo con destinazione %s", this.passeggero.getPasseggeroId(), this.flight.getDestinationAirportId());
	}
	
	
}
