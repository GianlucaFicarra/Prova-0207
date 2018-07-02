package prova.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import prova.model.Airline;
import prova.model.Airport;
import prova.model.Model;
import prova.model.Passeggero;
import prova.model.Tratta;

public class FlightDelaysController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private ComboBox<Airline> cmbBoxLineaAerea;

    @FXML
    private Button caricaVoliBtn;

    @FXML
    private TextField numeroPasseggeriTxtInput;

    @FXML
    private TextField numeroVoliTxtInput;

    @FXML
    private Button btnSimula;

    private Model model;
    
    public void setModel(Model model) {
		this.model=model;
		 cmbBoxLineaAerea.getItems().addAll(model.getLinee());
	}
    
    
    @FXML
    void doCaricaVoli(ActionEvent event) {
    	txtResult.clear();
    	
		//salvo linea passato dall'utente
		Airline linea = cmbBoxLineaAerea.getValue();

		//verifico se ogetto linea non sia nulla
		if (linea == null) {
			txtResult.appendText("Scegliere una linea\n");
			return;
		}
		
		
		/* PER TENDINA CON AEREOPORTI
		 Airport airport = cmbBoxLineaAerea.getValue();

		//verifico se ogetto linea non sia nulla
		if (airport == null) {
			txtResult.appendText("Scegliere un aereoporto\n");
			return;
		}
		
		  PER CASELLA CON LUNGHEZZA MASSIMA
		  txtResult.clear();
		String flight= txtDistanzaInput.getText();
		
		try {
			int kmRotte= Integer.parseInt(flight);
			
			model.creaGrafo(kmRotte);
			} catch(NumberFormatException e){
			    txtResult.setText("Inserire numeri validi!!");	
				}
		 */
		
		//creo un grafo per calcolare i confini
		try {
			model.createGraph(linea);    			
    			
    		List<Tratta> list = model.getTrattePeggiori();
    		
    		if(list == null) {
        		txtResult.setText("Nessuna rotta trovata.");
        		return;
        	}
    		
    		txtResult.appendText("\nStampo le 10 tratte peggiori: \n");
    		int cont=1;
    		for(Tratta t: list) {
    			this.txtResult.appendText(String.format(cont+" Peso:"+ t.getPeso()+" Rotta: "+t.toString()));
    			cont++;
    		}
    		this.txtResult.appendText("\n\n");
    			
    		
		//PUNTI AGGIUNTI PER AVERE PIù CASISTICHE:
    		//1 COMPONENTI CONNESSE DA AEREOPORTO CASUALE
    		model.getComponenteConnessa();
    		//2 GRAFO FORTEMENTE CONNESSO?
    		System.out.println("\nNel grafo ottenuto è possibile da ogni aeroporto raggiungere ogni altro aeroporto?");	
			if(model.isStronglyConnected()==true) {
				System.out.println("Tutti gli aereoporti sono raggiungibili (AB BA)");	
			} else {
				System.out.println("Non tutti gli aereoporti sono raggiungibili (AB ma non BA)");	
			}
    		//3 CAMMINO MINIMO TRA DUE AEREOPORTI CASUALI
    		//model.getShortestPath();
    		//4 VISITA IN PROFONDITA: nodo più distante da un casuale
    		model.getpiuDistante();
		
		
		} catch (RuntimeException e) {
			e.printStackTrace();
			txtResult.appendText("Errore\n");
			return;
		}

    }

    
    
    @FXML
    void doSimula(ActionEvent event) {
    	this.txtResult.clear();
    	
		String pas = numeroPasseggeriTxtInput.getText();
		String voli = numeroVoliTxtInput.getText();
		
	    try {
			int numPas = Integer.parseInt(pas);
    		int numVoli = Integer.parseInt(voli);
			
    		//i futuri voli devono stare nella mia linea
    		Airline linea = cmbBoxLineaAerea.getValue();
    	
    		//creo il numero di passeggeri voluto:
    		List <Passeggero> passeggeri = new ArrayList<>();
    	
        	passeggeri= model.simula(numPas, numVoli, linea);  //setto il ritardo del passeggero come risultato
        	
        	txtResult.appendText("\n\nStampo il ritardo complessivo accumulato da ciascun passeggero: \n");
        	for(Passeggero p: passeggeri) {
        		txtResult.appendText(p.toString()+"\n");
        	}
    		
        	
        	/*5 RICORSIONE
        	Map<Passeggero,List<Airport>> cammino =model.getCammino(numPas, numVoli);
      		txtResult.appendText(cammino.toString());*/
      		
      		
		}catch (NumberFormatException e) {
    		this.txtResult.appendText("ERRORE: inserire valori numerici!!!\n");
    	}
		
    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxLineaAerea != null : "fx:id=\"cmbBoxLineaAerea\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert caricaVoliBtn != null : "fx:id=\"caricaVoliBtn\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroPasseggeriTxtInput != null : "fx:id=\"numeroPasseggeriTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'FlightDelays.fxml'.";

    }

	
}
