package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraversedGraphListener implements TraversalListener<Fermata,DefaultEdge> {

	Graph<Fermata,DefaultEdge> grafo;
	Map<Fermata,Fermata> back;
	
	public EdgeTraversedGraphListener(Graph<Fermata,DefaultEdge> grafo,Map<Fermata, Fermata> back) {
		super();
		this.grafo = grafo;
		this.back = back;
	}

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
		/*
		 * back codifica relazioni del tipo figlio->padre 
		 * 
		 * per un nuovo vertice figlio scoperto devo avere che:
		 * - il figlio è ancora sconosciuto (non ancora trovato)
		 * - il padre è già stato visitato
		 * 
		 * idea:
		 * 	back.put(ev.getEdge().destinationVertex(),ev.getEdge().sourceVertex() ); 
		 */
		Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge());
		Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge());
		
		/*
		 * se il grafo è orientato(come in questo caso):
		 * 	source == padre , target == figlio
		 * 
		 * se il grafo NON è orientato:
		 * 	potrebbe anche essere il contrario
		 */
		if(!back.containsKey(targetVertex) && back.containsKey(sourceVertex)) {
			back.put(targetVertex, sourceVertex);
		}else if(!back.containsKey(sourceVertex) && back.containsKey(targetVertex)) {
			back.put(sourceVertex, targetVertex);
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
