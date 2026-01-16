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
public class Nodo {
    private Proceso dato; // El proceso que guardamos
    private Nodo siguiente; // El enlace al siguiente vagón

    public Nodo(Proceso proceso) {
        this.dato = proceso;
        this.siguiente = null;
    }
    public Proceso getDato() { return dato; }
    public void setDato(Proceso dato) { this.dato = dato; }
    public Nodo getSiguiente() { return siguiente; }
    public void setSiguiente(Nodo siguiente) { this.siguiente = siguiente; }
}
