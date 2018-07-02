package prova.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import prova.model.Airline;
import prova.model.AirlineIdMap;
import prova.model.Airport;
import prova.model.AirportIdMap;
import prova.model.Flight;
import prova.model.Tratta;


public class FlightDelaysDAO {

	
	//metodi semplici per prendere tutti i valori
	public List<Airline> loadAllAirlines() {
		String sql = "SELECT id, iata_code, airline from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("iata_code"), rs.getString("airline")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT id, iata_code, airport, city, state, country, latitude, longitude, timezone_offSet FROM airports";
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("id"),rs.getString("iata_code"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("latitude"), rs.getDouble("longitude"),rs.getDouble("timezone_offset"));
				result.add(airport);
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT id, airline_Id, flight_number, tail_number, origin_airport_id, destination_airport_id, scheduled_departure_date, " + 
				"departure_delay, elapsed_time, distance,  arrival_date, arrival_delay " + 
				"FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getInt("airline_Id"), rs.getInt("flight_number"),rs.getString("tail_number"),
						rs.getInt("origin_airport_id"), rs.getInt("destination_airport_id"),
						rs.getTimestamp("scheduled_departure_date").toLocalDateTime(), rs.getDouble("departure_delay"),
						rs.getDouble("elapsed_time"), rs.getInt("distance"), 
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getDouble("arrival_delay"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//tutte le linee con le mappe
	public List<Airline> getAllAirlines(AirlineIdMap airlineIdMap) {
		String sql = "SELECT * FROM airlines";
		
		List<Airline> list = new ArrayList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airline airline = new Airline(rs.getInt("ID"), rs.getString("iata_code"), rs.getString("airline"));
				list.add(airlineIdMap.get(airline));//alla lista inserisco oggetto già presente o appena aggiunta nell'idmap
			}
			conn.close();
			return list;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	//tutti gli aereoporti con la mappa
	public List<Airport> getAllAirports(AirportIdMap airportIdMap) {
		String sql = "SELECT id, iata_code, airport, city, state, country, latitude, longitude, timezone_offSet FROM airports";
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("id"),rs.getString("iata_code"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("latitude"), rs.getDouble("longitude"),rs.getDouble("timezone_offset"));
				result.add(airportIdMap.get(airport));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//------------------------------creati
	
	//VERTICI: data una compagnia, prendo gli aeroporti serviti da questa
    public List<Airport> getAirportFromAirline(Airline airline, AirportIdMap airportMap) {
		
		String sql = "select distinct a.id as id, iata_code, a.airport as airport, a.city as city, a.state as state, a.country as country, a.latitude as lat, a.longitude as lon, a.TIMEZONE_OFFSET " + 
				"from airports as a, flights as f " + 
				"where f.AIRLINE_ID= ? " + 
				"and (f.ORIGIN_AIRPORT_ID = a.ID or f.DESTINATION_AIRPORT_ID = a.ID)";
		
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, airline.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("id"),rs.getString("iata_code"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("lat"), rs.getDouble("lon"),rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airportMap.get(airport));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	//NON USATO- torna i voli di una linea
	public List<Flight> getFlightByAirline(Airline linea, AirportIdMap map) {
		String sql = "SELECT id, airline_Id, flight_number, tail_number, origin_airport_id, destination_airport_id, scheduled_departure_date, " + 
				"departure_delay, elapsed_time, distance,  arrival_date, arrival_delay " + 
				"FROM flights as f " + 
				"WHERE f.AIRLINE_ID=? ";
		
		List<Flight> list = new ArrayList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, linea.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Airport origine=  map.get(rs.getInt("origin_airport_id"));
				Airport destinazione= map.get(rs.getInt("destination_airport_id"));
				
				if(origine!= null || destinazione!= null) {
					
					Flight flight = new Flight(rs.getInt("id"), rs.getInt("airline_Id"), rs.getInt("flight_number"),rs.getString("tail_number"),
							rs.getInt("origin_airport_id"), rs.getInt("destination_airport_id"),
							rs.getTimestamp("scheduled_departure_date").toLocalDateTime(), rs.getDouble("departure_delay"),
							rs.getDouble("elapsed_time"), rs.getInt("distance"), 
							rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getDouble("arrival_delay"));
				list.add(flight); //alla lista inserisco oggetto già presente o appena aggiunta nell'idmap
					
					
					
				}
				
				
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	//NON USATO- torna il ritardo di una tratta
	public double getRitardoMedioSuTratta(Airline airline, Airport source, Airport destination) {
		String sql = "SELECT AVG(ARRIVAL_DELAY) as media " + 
				"from flights as f " + 
				"where f.AIRLINE_ID=? " + 
				"and origin_airport_id = ? " + 
				"and destination_airport_id = ?";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, airline.getId());
			st.setInt(2, source.getId());
			st.setInt(3, destination.getId());
			ResultSet rs = st.executeQuery();
			double media=0.0;
			if (rs.next()) {
				media = rs.getDouble("media");
			}

			conn.close();
			return media;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

	//ARCHI, data una compagnia, faccio la media dei ritardi di ogni tratta
	//Tratta= media+S+D - ho bisogno dell'oggetto aereoporto quindi uso mappa
	public List<Tratta> getRitardoMedioSuTratte(Airline airline, AirportIdMap airportMap) {
		String sql = "SELECT AVG(ARRIVAL_DELAY) as media, origin_airport_id as o, destination_airport_id as d " + 
				"from flights AS f " + 
				"where f.AIRLINE_ID=? " + 
				"group by origin_airport_id, destination_airport_id";
		List<Tratta> result = new LinkedList<>();
	
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, airline.getId());
			ResultSet rs = st.executeQuery();
		
			double media=0.0;
			while (rs.next()) {
				
				media = rs.getDouble("media");
				Airport origin = airportMap.get(rs.getInt("o"));
				Airport destination = airportMap.get(rs.getInt("d"));
				
				if(origin != null && destination != null)
					result.add(new Tratta(origin, destination, media));
			}
	
			conn.close();
			return result;
	
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

    //CHIAMATO DA SIMULATORE
	//trova primo volo dopo data passata, da questo aereoporto, verso aereoporto della linea
	public Flight findFirstFlight(Airline linea, int idPartenza, LocalDateTime dataPartenza) {
		String sql = "SELECT * " + 
				"FROM flights AS f " + 
				"WHERE f.AIRLINE_ID= ? " + 
				"AND ORIGIN_AIRPORT_ID = ? " + 
				"AND SCHEDULED_DEPARTURE_DATE > ? " + 
				"ORDER BY SCHEDULED_DEPARTURE_DATE ASC " + 
				"LIMIT 1"; 
			
	try {
		Connection conn = ConnectDB.getConnection();
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, linea.getId());
		st.setInt(2, idPartenza);
		st.setString(3, dataPartenza.toString());
		ResultSet rs = st.executeQuery();
		
		Flight flight = null;
		if (rs.next()) {
		
			 flight = new Flight(rs.getInt("id"), rs.getInt("airline_Id"), rs.getInt("flight_number"),rs.getString("tail_number"),
					rs.getInt("origin_airport_id"), rs.getInt("destination_airport_id"),
					rs.getTimestamp("scheduled_departure_date").toLocalDateTime(), rs.getDouble("departure_delay"),
					rs.getDouble("elapsed_time"), rs.getInt("distance"), 
					rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getDouble("arrival_delay"));
		}
		
		conn.close();
		return flight;
		
	} catch (SQLException e) {
	e.printStackTrace();
	System.out.println("Errore connessione al database");
	throw new RuntimeException("Error Connection Database");
	}
	}

	//non usato, trova la prima data disponibile dopo una passata
	public LocalDateTime findFirstDate(Airport airport, LocalDateTime data) {
		String sql = "select SCHEDULED_DEPARTURE_DATE as partenza " + 
				"from flights " + 
				"where ORIGIN_AIRPORT_ID = ? " + 
				"and SCHEDULED_DEPARTURE_DATE > ? " + 
				"order by SCHEDULED_DEPARTURE_DATE asc " + 
				"LIMIT 1";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, airport.getId());
			st.setString(2, data.toString());
			ResultSet rs = st.executeQuery();
			
			LocalDateTime partenza = null;
			if (rs.next()) {
				partenza = rs.getTimestamp("partenza").toLocalDateTime();
			}

			conn.close();
			return partenza;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	 // prendo tutti i ritardi da quell'aeroporto
	public double getDelays(Airline airline, Airport airport) { // prendo tutti i ritardi da quell'aeroporto
		String sql = "SELECT SUM(departure_delay) as dd, SUM(arrival_delay) as ad " + 
				"FROM flights " + 
				"WHERE airline_id = ? AND origin_airport_id = ? " + 
				"GROUP BY id ";
		
		double count = 0.0;
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, airline.getId());
			st.setInt(2, airport.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				count = count + rs.getDouble("dd") + rs.getDouble("ad");
			}

			conn.close();
			return count;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	// carico tutti i voli effettuati dalla compagnia
	public List<Flight> loadAllFlights(Airline a) { 
		String sql = "SELECT * " + 
				"FROM flights " + 
				"WHERE airline_id = ? " + 
				"order by scheduled_departure_date asc ";
		
		List<Flight> result = new ArrayList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getInt("airline_Id"), rs.getInt("flight_number"),rs.getString("tail_number"),
						rs.getInt("origin_airport_id"), rs.getInt("destination_airport_id"),
						rs.getTimestamp("scheduled_departure_date").toLocalDateTime(), rs.getDouble("departure_delay"),
						rs.getDouble("elapsed_time"), rs.getInt("distance"), 
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getDouble("arrival_delay"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}


	// dato un aeroporto, prendo tutti i voli partiti da quell'aeroporto 
	public List<Flight> getFlightsDepartedFromAirport(Airport a) { 
		String sql = "SELECT * FROM flights as f WHERE f.ORIGIN_AIRPORT_ID=? ORDER BY f.SCHEDULED_DEPARTURE_DATE";

		List<Flight> result = new LinkedList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a.getId());
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getInt("airline_Id"), rs.getInt("flight_number"),rs.getString("tail_number"),
						rs.getInt("origin_airport_id"), rs.getInt("destination_airport_id"),
						rs.getTimestamp("scheduled_departure_date").toLocalDateTime(), rs.getDouble("departure_delay"),
						rs.getDouble("elapsed_time"), rs.getInt("distance"), 
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getDouble("arrival_delay"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
}
