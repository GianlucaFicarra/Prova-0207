package prova.model;


import java.util.ArrayList;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import prova.db.FlightDelaysDAO;



public class Model {

	private List<Airline> listaLinee; //per rendere la linea desiderata
	private AirlineIdMap airlineIdMap;
	
	private List<Airport> listaAereoporti; //i miei nodi
	private AirportIdMap airportIdMap;
	
	private List<Tratta> tratte; //archi
	
	private FlightDelaysDAO dao;
	private Simulatore sim;
	Random random;

	/*
	 Nel grafo vOGLIO AEREPORTI DI ARRIVO E DESTINAZIONE, MA LA CLASSE AIRPORT
	  HA SOLO GLI ID, CREO UNA CLASSE IDMAP per convertire id in oggetti
	*/
	SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo;

	
	//per eventuale ricorsione
	List<Airport> best= new ArrayList<>();
	Passeggero passeggero= new Passeggero();
	
	
	public Model() {
		dao = new FlightDelaysDAO();
		
		airportIdMap = new AirportIdMap();
		airlineIdMap = new AirlineIdMap();
		
		//carico tutte le linee disponibili con mappa
		listaLinee = dao.getAllAirlines(airlineIdMap);
		
		//carico tutti gli aereoporti con mappa(non usata perchè sotto carico quelli di una certa linea)
		//listaAereoporti = dao.getAllAirports(airportIdMap);

		tratte = new LinkedList<>();
		
		sim= new Simulatore();
		random = new Random();
	}

	
	public List<Airline> getLinee() { //popolo la tendina con le linee consentite
		return listaLinee;
	}


	public void createGraph(Airline linea) {
		
		        //creo grafo.... dichiarazione standard
				grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
				
				//carico vertici (uso mappa per avere oggetti aereoporti)
				listaAereoporti = dao.getAirportFromAirline(linea, airportIdMap);
				   
				//creato grafo e vertici li aggiungo
				Graphs.addAllVertices(grafo, this.listaAereoporti);
				
				// caricamento tratte passanti per la mia linea
				tratte= dao.getRitardoMedioSuTratte(linea, airportIdMap);
				
				//calcolo il peso della tratta==Archi
				//tramite la media dei ritardi e le distanze in longitudine e latitudine:
				 for(Tratta t: tratte) {
					
					/*aereoporto sorgente e destinazione 
					 * voglio gli oggetti non gli id quindi uso idmap*/
					Airport sourceAirport = t.getSource();
					Airport destinationAirport = t.getDestination();
					
					//se sono diversi
					if (sourceAirport != null && destinationAirport != null && !sourceAirport.equals(destinationAirport)) {
		
						
					/* caso in cui latitudine e longitudine  potrebbero essere nulle
					LatLng origine = new LatLng(sourceAirport.getLatitude(), sourceAirport.getLongitude());
					LatLng destinazione = new LatLng(destinationAirport.getLatitude(),destinationAirport.getLongitude());
				
					if (origine != null && destinazione != null) {
						
						double distanza = LatLngTool.distance(origine, destinazione, LengthUnit.KILOMETER);
						
						double peso = t.getMedia() / distanza;
						t.setPeso(peso);

						Graphs.addEdge(grafo, sourceAirport, destinationAirport, peso);
						
					}*/
					
						
						
						//1-distanze
						double distanza = LatLngTool.distance(new LatLng(sourceAirport.getLatitude(),
								sourceAirport.getLongitude()), new LatLng(destinationAirport.getLatitude(), destinationAirport.getLongitude()), 
								LengthUnit.KILOMETER);
						
					    //avendo settato e calcolato il valore 
						double peso = t.getMedia()/distanza;
						t.setPeso(peso);
						
						//vado ad inserire l'arco ed il peso appena calcolato
						Graphs.addEdge(grafo, sourceAirport, destinationAirport, peso);
						
						//debug
						//System.out.println("\nVertici: "+sourceAirport+"-"+destinationAirport+" peso:"+peso);
					}
				}
				
				//stampo di default vertici e archi
				System.out.println("\n Numero di vertici per il grafo: "+grafo.vertexSet().size());
				System.out.println("\n Numero di archi per il grafo: "+grafo.edgeSet().size());
				
	}



	//dovo aver creato il grafo e popolato le tratte prendo le pù grosse
	public List<Tratta> getTrattePeggiori() {
		Collections.sort(tratte);  //ordine di peso decrescente
		return tratte.subList(0, 10); //voglio solo le 10 tratte di peso max
	}
	

    //PUNTO 2
	public List<Passeggero>  simula(int numPas, int numVoli, Airline linea) {
		sim.init(numPas, numVoli, listaAereoporti, linea, dao); 
		sim.run();
		
		return sim.getResultati();
	}

	
	
    //PUNTI EXTRA
	
	//COMPONENTE CONESSA
	public void getComponenteConnessa() {
		int x = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
		Airport casualAirport = listaAereoporti.get(x); //prendo aereoporto casuale
		
		ConnectivityInspector<Airport, DefaultWeightedEdge> ci = new ConnectivityInspector(grafo);
		System.out.println("\nNumero componenti connesse grafo: "+ci.connectedSets().size());
		
		if(ci.connectedSets().size()>0) {
		System.out.println("Elenco omponenti connesse grafo: ");
		for (Set<Airport> a : ci.connectedSets()) 
			System.out.println("\n"+a.toString());
		
		
		//delle componenti connesse voglio il set di nodi più grosso tra le varie componenti
		Set<Airport> bestSet = null;
		int bestSize = 0;
		
			for (Set<Airport> s : ci.connectedSets()) {
				if (s.size() > bestSize) {
					bestSet = new HashSet<Airport>(s); //faccio deepcopy
					bestSize = s.size();
				}	
			}
		} else {
			System.out.println("\nNon ho Componenti connesse");
		}
	}


	//FORTEMENTE CONESSO
	public boolean isStronglyConnected() {
		KosarajuStrongConnectivityInspector<Airport, DefaultWeightedEdge> ksci = new KosarajuStrongConnectivityInspector<Airport, DefaultWeightedEdge>(grafo);
		return ksci.isStronglyConnected();
	}


	//	Dijkstra
	public void getShortestPath() {
		
	   /* I dikstra
	    int x1 = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
		Airport casualAirport1 = listaAereoporti.get(x1);
		
		int x2 = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
		Airport casualAirport2 = listaAereoporti.get(x2);
		
		if(!casualAirport1.equals(casualAirport2)) {
			System.out.println("\nCammino minimo Dijkstra tra: "+casualAirport1+" e "+casualAirport2);
		
			AirportDistance a=null;
				DijkstraShortestPath<Airport, DefaultWeightedEdge> dsp = new DijkstraShortestPath<>(grafo);
				GraphPath<Airport, DefaultWeightedEdge> p = dsp.getPath(casualAirport2, casualAirport2);
				if (p != null) {
					 a=new AirportDistance(casualAirport2, p.getWeight(), p.getEdgeList().size());
				}
			

			System.out.println(a.getAirport()+"dista "+a.getTratte()+" dall'origine");
				
			
			
		}else {
			System.out.println("\nCasualmente è capitato lo sesso aereoporto");
		}
	*/
		
		/* II dikstra
		int x1 = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
		Airport start = listaAereoporti.get(x1);
		
		int x2 = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
		Airport end = listaAereoporti.get(x2);
		
		 List<AirportDistance> list = new ArrayList<>();

		  
		    DijkstraShortestPath<Airport, DefaultWeightedEdge> dsp = new DijkstraShortestPath<Airport, DefaultWeightedEdge>(grafo);
		    GraphPath<Airport, DefaultWeightedEdge> p = dsp.getPath(start, end);
		    if (p != null) {
		      list.add(new AirportDistance(end, p.getWeight(), p.getEdgeList().size()));
		    }
		  

		  list.sort(new Comparator<AirportDistance>() {
		    @Override
		    public int compare(AirportDistance o1, AirportDistance o2) {
		      return Double.compare(o1.getDistance(), o2.getDistance());
		    }
		  });

		  System.out.println(list.toString());
		 */
	}


	//VISITA IN PROFONDITA
	public void getpiuDistante() {
		int x1 = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
		Airport casualAirport1 = listaAereoporti.get(x1);
		
		System.out.println("\nVisita in profontita partendo da "+casualAirport1);
		// visita il grafo, creo iteratore che restituisce i vertiti che visita
					//e li raccolgo in una collection (scelgo list perchè devo prenderne l'ultima componente)
					List<Airport> visitati = new LinkedList<>();
					
					DepthFirstIterator<Airport, DefaultWeightedEdge> dfv = new DepthFirstIterator<>(this.grafo, casualAirport1);
					//iteratore che uso per iterare grafo partendo da start chiamando next
					
					while (dfv.hasNext()) //finche cè un elemento successivo per l'iteratore
						visitati.add(dfv.next()); //aggiungo il prox elemento, se diventa falso esco

					
					System.out.println("Componente più distante"+ visitati.get(visitati.size()-1));
					System.out.println("Componenti visitate"+ visitati.size());
					System.out.println("Elenco Componenti visitate: ");
					for(Airport a: visitati) {
						System.out.println(a.toString());
					}
		
	}


	//RICORSIONE
	public Map<Passeggero,List<Airport>>getCammino(int numPas, int numVoli) {
		
		
		
		 for(int i=0; i<numPas; i++) {
			List<Airport> parziale= new ArrayList<>();
			
				int x = random.nextInt(listaAereoporti.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
				Airport casualAirport = listaAereoporti.get(x); //prima destinazione casuale
				
				Passeggero p = new Passeggero(i+1, numVoli);
		        
				parziale.add(casualAirport);
		        cerca(parziale, numVoli,p);
		        
		        
		 }
		 Map<Passeggero,List<Airport>> map= new HashMap<>();
		 map.put(this.passeggero,best);
		 
		 return map;
	}


	private void cerca(List<Airport> parziale, int numVoli,Passeggero p) {
		double distMax= Integer.MIN_VALUE;
	
		if(numVoli==0 || (grafo.outgoingEdgesOf(parziale.get(parziale.size()-1))).size()==0) { //se ho raggiunto i voli max o non ho più uscenti
			if(calcolaDistanza(parziale)>distMax) {
				distMax=calcolaDistanza(parziale);
				best= new ArrayList<>(parziale);
				this.passeggero=p;
				
			}
		}
		
		
		
		Set<DefaultWeightedEdge> archisuccessivi= grafo.outgoingEdgesOf(parziale.get(parziale.size()-1));
		
		for(DefaultWeightedEdge d : archisuccessivi) {
			
			Airport destination = grafo.getEdgeTarget(d);
			
			if(!parziale.contains(destination)) {
				parziale.add(destination);
				cerca(parziale,numVoli-1,p);
				parziale.remove(destination);
				
			}
			
		}
		 
		
		
	}


	private double calcolaDistanza(List<Airport> parziale) {
		
			double distanza = LatLngTool.distance(new LatLng(parziale.get(0).getLatitude(),
					parziale.get(0).getLongitude()), new LatLng(parziale.get(parziale.size()-1).getLatitude(), parziale.get(parziale.size()-1).getLongitude()), 
					LengthUnit.KILOMETER);
			return distanza;
	
	}





}