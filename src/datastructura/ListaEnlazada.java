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
    // Método para encolar un proceso (ponerlo al final)
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

   
    public Proceso eliminarPrimero() {
        if (cabeza == null) return null;
        Proceso proceso = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamaño--;
        return proceso;
    }

    public int getTamaño() { return tamaño; }
}
