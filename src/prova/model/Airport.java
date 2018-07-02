package prova.model;

public class Airport {

	private int id;
	private String iata_code;
	private String name;
	private String city;
	private String state;
	private String country;
	private double latitude;
	private double longitude;
	private double timezoneOffSet;
	

	public Airport(int id, String iata_code, String name, String city, String state, String country, double latitude,
			double longitude, double timezoneOffSet) {
		super();
		this.id = id;
		this.iata_code = iata_code;
		this.name = name;
		this.city = city;
		this.state = state;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timezoneOffSet = timezoneOffSet;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getTimezoneOffSet() {
		return timezoneOffSet;
	}

	public void setTimezoneOffSet(double timezone_offSet) {
		this.timezoneOffSet = timezone_offSet;
	}

	@Override
	public String toString() {
		return "Airport [id=" + id + ", name=" + name + "]" + "\n";
	}

	
	
}
