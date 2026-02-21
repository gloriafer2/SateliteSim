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
    
        public void vaciar() {
        this.inicio = null; 
    }
        
        
      public void eliminarProcesoEspecifico(clases.Proceso procesoABuscar) {
    if (inicio == null) return;

    // Si es el primero
    if (inicio.getDato().equals(procesoABuscar)) {
        inicio = inicio.getSiguiente();
        return;
    }

    datastructura.Nodo anterior = inicio;
    datastructura.Nodo actual = inicio.getSiguiente();

    while (actual != null) {
        if (actual.getDato().equals(procesoABuscar)) {
            anterior.setSiguiente(actual.getSiguiente());
            return;
        }
        anterior = actual;
        actual = actual.getSiguiente();
    }
}
      
      
      public boolean contiene(clases.Proceso p) {
        Nodo aux = inicio;
        while (aux != null) {
            // Comparamos por el nombre o el ID único del proceso
            if (aux.getDato().getNombre().equals(p.getNombre())) {
                return true; // Si lo encuentra, devuelve verdadero
            }
            aux = aux.getSiguiente();
        }
        return false; // Si termina el ciclo y no lo vio, es falso
}

    /**
     * Inserta un proceso en la lista manteniendo el orden de prioridad de 1 a 5.
     * Los procesos con mayor prioridad quedan al inicio.
     */
    public void agregar(Proceso nuevoProceso) {
    Nodo nuevoNodo = new Nodo(nuevoProceso);

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
    public void setInicio(Nodo inicio) {
    this.inicio = inicio;
}
    
    
    

    public void ordenarPorTiempoRestante() { // Para SRT
        if (inicio == null || inicio.getSiguiente() == null) return;
        boolean huboCambio;
        do {
            huboCambio = false;
            Nodo actual = inicio;
            while (actual.getSiguiente() != null) {
                // Comparamos instrucciones restantes
                if (actual.getDato().getInstruccionesTotales() > actual.getSiguiente().getDato().getInstruccionesTotales()) {
                    Proceso temp = actual.getDato();
                    actual.setDato(actual.getSiguiente().getDato());
                    actual.getSiguiente().setDato(temp);
                    huboCambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (huboCambio);
    }

    public void ordenarPorPrioridad() { // Para Prioridad Estática
        if (inicio == null || inicio.getSiguiente() == null) return;
        boolean huboCambio;
        do {
            huboCambio = false;
            Nodo actual = inicio;
            while (actual.getSiguiente() != null) {
                // 1 es mayor prioridad que 2, por eso ordeno de menor a mayor
                if (actual.getDato().getPrioridad() > actual.getSiguiente().getDato().getPrioridad()) {
                    Proceso temp = actual.getDato();
                    actual.setDato(actual.getSiguiente().getDato());
                    actual.getSiguiente().setDato(temp);
                    huboCambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (huboCambio);
    }

    public void ordenarPorDeadline() { // Para EDF
        if (inicio == null || inicio.getSiguiente() == null) return;
        boolean huboCambio;
        do {
            huboCambio = false;
            Nodo actual = inicio;
            while (actual.getSiguiente() != null) {
                // El que tiene el tiempo limite más pequeño va de primero
                if (actual.getDato().getTiempoLimite() > actual.getSiguiente().getDato().getTiempoLimite()) {
                    Proceso temp = actual.getDato();
                    actual.setDato(actual.getSiguiente().getDato());
                    actual.getSiguiente().setDato(temp);
                    huboCambio = true;
                }
                actual = actual.getSiguiente();
            }
        } while (huboCambio);
    }
    
    
        public void agregarAlFinal(Proceso nuevoProceso) {
        datastructura.Nodo nuevoNodo = new datastructura.Nodo(nuevoProceso);

        // Si la lista está vacía, el nuevo nodo es el inicio
        if (inicio == null) {
            inicio = nuevoNodo;
        } else {
            datastructura.Nodo actual = inicio;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamaño++;
    }

}