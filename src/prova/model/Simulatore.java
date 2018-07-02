package prova.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import prova.db.FlightDelaysDAO;


public class Simulatore {

	//PARAMETRI (variabili per modificare la simulazione)
	private int numPas;
	private int numVoli;
	private Airline linea;
	private List<Airport> airports;
	
	private FlightDelaysDAO dao;
	Random random;
	
	//MODELLO DEL MONDO (fotografia del sistema)
	private List<Passeggero> passeggeri;
	
	//CODA DEGLI EVENTI
	LinkedList<Event> queue;
	
	//VALORI OUTPUT
	//è la mia stessa lista di passeggeri, dove vado a memorizzare i ritardi, voglio quelli
	
	
	public void init(int numPas, int numVoli, List<Airport> listaAereoporti, Airline linea, FlightDelaysDAO dao) {
		this.numPas=numPas;
		this.numVoli=numVoli;
		this.airports=listaAereoporti;
		this.linea=linea;
		this.dao = dao;
		 
		 queue = new LinkedList();
		 random = new Random();
		 
		 
		 //creo numPas passeggeri e li assegno ogni volta ad un aereoporto casuale
		 passeggeri = new ArrayList<>(); //li salvo per poi stamparne il ritardo
		 for(int i=0; i<numPas; i++) {
				
				int x = random.nextInt(airports.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
				Airport casualAirport = airports.get(x); //prima destinazione casuale
				
				Passeggero p = new Passeggero(i+1, numVoli);
				passeggeri.add(p);
			    
				//prendo primo volo(dopo data passata) di quel aereoporto (sorgente) verso un aereoporto della mia linea
				//e ci metto su il passeggero corrente, devo cercare il primo e creo evento
				                                                                            //01/01/2015 0h 0min 0sec
				Flight volo = dao.findFirstFlight(linea, casualAirport.getId(), LocalDateTime.of(2015, 1, 1, 0, 0, 0));
			
				if(volo != null) {
				
					
				   // Event e = new Event(p, casualAirport, volo.getScheduledDepartureDate());
					Event e = new Event(p,volo);
					System.out.println(e.getFlight().getDestinationAirportId());
				    queue.add(e);
				    
				    //decremento voo ed aumento ritardo alla creazione di un nuovo volo
				    p.accumuloRitardo(volo.getArrivalDelay()); //accumulo ritardo
					p.voloEffettuato();
				    
				    
				    
				}
			}
	}

	public void run() {
		
		Event e;
		while((e = this.queue.poll()) != null) {
			processEvent(e);
		}
		
	}
	
	private void processEvent(Event e) {

		//ARRIVO PASSEGGERO==PARTENZA STESSO PASSEGGERO
		Passeggero passeggero = e.getPasseggero();
		
		//se ancora può fare voli lo faccio imbarcare
		if(passeggero.getVoli() > 0 ) {  //con >0 ho 25 destinazioni per 25 viaggi
			
			Flight vecchioVolo = e.getFlight(); //prendo il volo preso
			
			//destinazione del vecchio == sorcente nuovo
			int source = vecchioVolo.getDestinationAirportId();
			
			//trova primo volo dopo data passata, da questo aereoporto, verso aereoporto della linea
			Flight volo = dao.findFirstFlight(linea, source, vecchioVolo.getArrivalDate());
			
			if(volo != null) {
			    Event e2 = new Event(e.getPasseggero(), volo);
			    queue.add(e2);
			    System.out.println(e.getFlight().getDestinationAirportId());
			   
			    //decremento voo ed aumento ritardo alla creazione di un nuovo volo
			    passeggero.accumuloRitardo(volo.getArrivalDelay()); //accumulo ritardo
				passeggero.voloEffettuato();//diminuisco il numero di voli
			   
			}
				
		}
	}


	public List<Passeggero> getResultati() {
		return passeggeri;
	}


}

