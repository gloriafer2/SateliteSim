/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datastructura;
import clases.Proceso;
/**
 *
 * @author Gloria
 */
public class ListaEnlazada {
    private Nodo inicio;
    private int tamaño;

    public ListaEnlazada() {
        this.inicio = null;
        this.tamaño = 0;
    }
    
    public boolean estaVacia() {
    return inicio == null;
}

    /**
     * Inserta un proceso en la lista manteniendo el orden de prioridad de 1 a 5.
     * Los procesos con mayor prioridad quedan al inicio.
     */
    public void agregar(Proceso nuevoProceso) {
    Nodo nuevoNodo = new Nodo(nuevoProceso);

    // Si el número es menor, tiene mas prioridad y va al inicio
    if (inicio == null || nuevoProceso.getPrioridad() < inicio.getDato().getPrioridad()) {
        nuevoNodo.setSiguiente(inicio);
        inicio = nuevoNodo;
    } else {
        Nodo actual = inicio;
        // Avanza mientras el número sea menor o igual al nuevo
        while (actual.getSiguiente() != null && 
               actual.getSiguiente().getDato().getPrioridad() <= nuevoProceso.getPrioridad()) {
            actual = actual.getSiguiente();
        }
        nuevoNodo.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(nuevoNodo);
    }
    tamaño++;
}

    
    public void eliminarProceso(clases.Proceso p) {
    if (inicio == null) return;
    if (inicio.getDato() == p) {
        inicio = inicio.getSiguiente();
        return;
    }
    datastructura.Nodo actual = inicio;
    while (actual.getSiguiente() != null) {
        if (actual.getSiguiente().getDato() == p) {
            actual.setSiguiente(actual.getSiguiente().getSiguiente());
            return;
        }
        actual = actual.getSiguiente();
    }
}
    public Proceso eliminarPrimero() {
        if (inicio == null) return null;
        Proceso proceso = inicio.getDato();
        inicio = inicio.getSiguiente();
        tamaño--;
        return proceso;
    }

    public int getTamaño() { 
        return tamaño; 
    }

    public Nodo getInicio() {
        return inicio; 
    }
}