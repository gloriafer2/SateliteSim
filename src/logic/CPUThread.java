/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import clases.Proceso;
import datastructura.ListaEnlazada;
import java.util.concurrent.Semaphore; 

public class CPUThread extends Thread {
    private ListaEnlazada colaListos;
    private ListaEnlazada colaBloqueados;
    private Proceso procesoEnEjecucion;
    private Semaphore semaforoCPU; // Para exclusión mutua y control de interrupciones
    private boolean interrupcionActiva = false;
    private gui.VentanaPrincipal ventana;
    private int quantumRestante;
    
    public CPUThread(ListaEnlazada listos, ListaEnlazada bloqueados, Semaphore sem, gui.VentanaPrincipal ventana) {
        this.colaListos = listos;
        this.colaBloqueados = bloqueados;
        this.semaforoCPU = sem;
        this.ventana = ventana;
    }
    
    

    @Override
    public void run() {
        while (true) {
                try {
                    semaforoCPU.acquire();

                    // 1. GESTIÓN DE INTERRUPCIÓN (Botón Impacto)
                    if (interrupcionActiva) {
                        manejarInterrupcion(); // Este método ahora será inteligente
                    } 

                    // 2. ASIGNACIÓN DE NUEVO PROCESO
                    else if (procesoEnEjecucion == null && !colaListos.estaVacia()) {
                        procesoEnEjecucion = colaListos.eliminarPrimero();
                        procesoEnEjecucion.setEstado("Ejecucion");
                        this.quantumRestante = ventana.getQuantumGlobal();

                        ventana.escribirLog("CPU: " + procesoEnEjecucion.getNombre() + 
                           " inicia ejecución (Algoritmo: " + ventana.getAlgoritmoActual() + ")");
                    }

                    // 3. EJECUCIÓN DEL CICLO
                    if (procesoEnEjecucion != null) {
                        procesoEnEjecucion.ejecutarCiclo();

                        // --- Validar Deadline (Aborto) ---
                        if (ventana.getSegundosMision() > procesoEnEjecucion.getDeadline() + 10){
                            procesoEnEjecucion.setEstado("FALLIDO");
                            ventana.escribirLog("ALERTA: "+ procesoEnEjecucion.getNombre() + " abortado por Deadline.");
                            ventana.liberarMemoriaYRevisarSuspendidos(procesoEnEjecucion);
                            procesoEnEjecucion = null;
                        } 
                        // --- Validar Finalización ---
                        else if (procesoEnEjecucion.getInstruccionesTotales() <= 0){
                            procesoEnEjecucion.setEstado("Terminado");
                            ventana.escribirLog("Exito: " + procesoEnEjecucion.getNombre() + " completó su misión.");
                            ventana.liberarMemoriaYRevisarSuspendidos(procesoEnEjecucion);
                            procesoEnEjecucion = null;
                        }
                        // --- Validar Round Robin ---
                        else if (ventana.getAlgoritmoActual().equals("Round Robin")){
                            quantumRestante--;
                            if(quantumRestante <= 0){
                                procesoEnEjecucion.setEstado("Listo");
                                colaListos.agregar(procesoEnEjecucion);
                                ventana.escribirLog("Round Robin: " + procesoEnEjecucion.getNombre() + " vuelve a la cola por Quantum.");
                                procesoEnEjecucion = null;
                            }
                        }
                    }

                    semaforoCPU.release();
                    Thread.sleep(200); 

                } catch (InterruptedException e) {
                    System.out.println("Error en el CPU: " + e.getMessage());
                }
            }
    }

    private void manejarInterrupcion() {
        if (procesoEnEjecucion != null) {
            // Si el botón ya lo puso en "Bloqueado", el CPU NO lo agrega a ninguna cola.
            // Solo limpia la referencia para que el CPU quede libre.
            if (procesoEnEjecucion.getEstado().equals("Bloqueado")) {
                procesoEnEjecucion = null; 
            } else {
                // Si la interrupción fue por otra cosa, se guarda normalmente
                procesoEnEjecucion.setEstado("Listo");
                colaListos.agregar(procesoEnEjecucion);
                procesoEnEjecucion = null;
            }
        }
        this.interrupcionActiva = false; // Resetear la bandera
        ventana.actualizarTablas();
}
    public void activarInterrupcion() {
        this.interrupcionActiva = true;
    }
    
    public Proceso getProcesoEnEjecucion(){
        return this.procesoEnEjecucion;
    }

    public void setProcesoEnEjecucion(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public void detenerProcesoInmediatamente() {
    if (this.procesoEnEjecucion != null) {
        // Al ponerlo en null aquí, el if (procesoEnEjecucion != null) 
        // del método run() dejará de ejecutarlo al instante.
        this.procesoEnEjecucion = null;
        this.interrupcionActiva = false; // Reset de bandera
    }
}
    
}
