package prova.model;

public class Airline {
	
	private int id;
	private String iata_code;
	private String name;
	
	public Airline(int id, String iata_code, String name) {
		super();
		this.id = id;
		this.iata_code = iata_code;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIata_code() {
		return iata_code;
	}

	public void setIata_code(String iata_code) {
		this.iata_code = iata_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	
	
}
