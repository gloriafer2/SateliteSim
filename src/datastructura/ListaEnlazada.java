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
    private Nodo cabeza;
    private int tamaño;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    /**
     * Inserta un proceso en la lista manteniendo el orden de prioridad de 1 a 5.
     * Los procesos con mayor prioridad quedan al inicio.
     */
    public void agregar(Proceso nuevoProceso) {
    Nodo nuevoNodo = new Nodo(nuevoProceso);

    // Si el número es menor, tiene mas prioridad y va al inicio
    if (cabeza == null || nuevoProceso.getPrioridad() < cabeza.getDato().getPrioridad()) {
        nuevoNodo.setSiguiente(cabeza);
        cabeza = nuevoNodo;
    } else {
        Nodo actual = cabeza;
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

    public Proceso eliminarPrimero() {
        if (cabeza == null) return null;
        Proceso proceso = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamaño--;
        return proceso;
    }

    public int getTamaño() { 
        return tamaño; 
    }

    public Nodo getInicio() {
        return cabeza; 
    }
}