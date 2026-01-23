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
     * Método para encolar un proceso (ponerlo al final de la lista).
     * @param proceso El objeto Proceso a agregar.
     */
    public void agregar(Proceso proceso) {
        Nodo nuevoNodo = new Nodo(proceso);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamaño++;
    }

    /**
     * Método para extraer el primer proceso de la lista (Simulación de despacho).
     * @return El proceso al inicio de la lista.
     */
    public Proceso eliminarPrimero() {
        if (cabeza == null) return null;
        Proceso proceso = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamaño--;
        return proceso;
    }

    // --- Getters necesarios para la lógica y la interfaz ---

    public int getTamaño() { 
        return tamaño; 
    }

    /**
     * Retorna el primer nodo de la lista. 
     * Es fundamental para que el método actualizarTabla() pueda recorrer los procesos.
     */
    public Nodo getInicio() {
        return cabeza; //
    }
}
