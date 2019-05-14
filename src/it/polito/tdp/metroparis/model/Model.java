package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	//implementazione con archi pesati su github del corso (nuovo branch)---> cammini minimi
	
	private class EdgeTraversedGraphListener implements TraversalListener<Fermata,DefaultEdge>{

		//per averla privata direttamente dentro al model senza dover passar nulla
		
		
		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {
			Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge());
			Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge());
			
			/*
			 * se il grafo è orientato(come in questo caso):
			 * 	source == padre , target == figlio
			 * 
			 * se il grafo NON è orientato:
			 * 	potrebbe anche essere il contrario
			 */
			if(!backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
				backVisit.put(targetVertex, sourceVertex);
			}else if(!backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
				backVisit.put(sourceVertex, targetVertex);
			}
				
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	private Graph<Fermata,DefaultEdge> grafo;
	

	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	private Map<Fermata,Fermata> backVisit;
	
	
	public void creaGrafo() {
		
		//Creo il grafo
		this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		//Aggiungo i vertici
		MetroDAO dao= new MetroDAO();
		this.fermate= dao.getAllFermate();
		
		//creoIdMap
		this.fermateIdMap= new HashMap<>();
		for(Fermata f: this.fermate) {
			fermateIdMap.put(f.getIdFermata(),f);
		}
		Graphs.addAllVertices(this.grafo, this.fermate);
		
		//Aggiungo gli archi
		
		/*for(Fermata partenza: this.grafo.vertexSet()) {
			for(Fermata arrivo: this.grafo.vertexSet()) {
				
			if(dao.esisteConnessione(partenza,arrivo)) {
				this.grafo.addEdge(partenza, arrivo);
				}
			}
		}*/
		
		//Aggiungo gli archi(opzione 2)
		for(Fermata partenza: this.grafo.vertexSet()) {
			
			List<Fermata>arrivi= dao.stazioniArrivo(partenza,fermateIdMap);
			
				for(Fermata arrivo: arrivi) 
					this.grafo.addEdge(partenza, arrivo);
		}
		//Aggiungo gli archi(opzione 3)
		
		
	}

	public List<Fermata> fermateRaggiungibili(Fermata source){
		
		List<Fermata> result= new ArrayList<Fermata>();
		backVisit= new HashMap<>();
		
//		GraphIterator<Fermata, DefaultEdge> it= new BreadthFirstIterator<>(this.grafo, source);
		GraphIterator<Fermata, DefaultEdge> it= new DepthFirstIterator<>(this.grafo, source);
		
		it.addTraversalListener(new Model.EdgeTraversedGraphListener());
		//EdgeTraversedGraphListener può essere utilizzata solo qui
		
		/*si può fare anche direttamente inline (sconsigliato se ha troppi metodi come in questo caso)


		 * it.addTraversalListener(new TraversalListener<Fermata,DefaultEdge>() {
		 * 
		 * @Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {
			Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge());
			Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge());
			
			/*
			 * se il grafo è orientato(come in questo caso):
			 * 	source == padre , target == figlio
			 * 
			 * se il grafo NON è orientato:
			 * 	potrebbe anche essere il contrario
			 
			if(!backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
				backVisit.put(targetVertex, sourceVertex);
			}else if(!backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
				backVisit.put(sourceVertex, targetVertex);
			}
				
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
			// TODO Auto-generated method stub
			
		}
		 * 
		 * 
		 * });
		
		 */
		
		
		
		backVisit.put(source, null);
		
		while(it.hasNext()) {
			result.add(it.next());
		}
		System.out.println(backVisit);
		return result;
	}
	
	public List<Fermata> percorsoFinoA(Fermata target){
		
		
		if(!backVisit.containsKey(target)) {
			// target non raggiungibile dala source
			return null;
		}
		
		List<Fermata> percorso= new LinkedList<>();
		
		Fermata f= target;
		while(f!=null) {
			//in ordine corretto
			percorso.add(0, f);
			f=backVisit.get(f);
		}
		
		return percorso;
		
		
	}
	
	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}



	public void setGrafo(Graph<Fermata, DefaultEdge> grafo) {
		this.grafo = grafo;
	}



	public List<Fermata> getFermate() {
		return fermate;
	}



}
