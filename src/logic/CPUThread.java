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
    private Semaphore semaforoCPU; 
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
                        manejarInterrupcion(); 
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
                        // Aquí sumamos un FALLO a la gráfica
                        if (procesoEnEjecucion.getDeadline()<= 0){
                            procesoEnEjecucion.setEstado("FALLIDO");
                            ventana.sumarFallo(); // <--- NUEVO: Actualiza la gráfica
                            ventana.escribirLog("ALERTA: "+ procesoEnEjecucion.getNombre() + " abortado por Deadline.");
                            ventana.liberarMemoriaYRevisarSuspendidos(procesoEnEjecucion);
                            procesoEnEjecucion = null;
                        } 
                        // --- Validar Finalización ---
                        // Aquí sumamos un ÉXITO a la gráfica
                        else if (procesoEnEjecucion.getInstruccionesTotales() <= 0){
                            procesoEnEjecucion.setEstado("Terminado");
                            ventana.sumarExito(); // <--- NUEVO: Actualiza la gráfica
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
            if (procesoEnEjecucion.getEstado().equals("Bloqueado")) {
                procesoEnEjecucion = null; 
            } else {
                procesoEnEjecucion.setEstado("Listo");
                colaListos.agregar(procesoEnEjecucion);
                colaListos.ordenarPorDeadline();
                procesoEnEjecucion = null;
            }
        }
        this.interrupcionActiva = false; 
        ventana.actualizarTablas();
    }

    public void activarInterrupcion() {
        this.interrupcionActiva = true;
    }
    
    public Proceso getProcesoEnEjecucion(){
        return this.procesoEnEjecucion;
    }

    public void setProcesoEnEjecucion(Object object) {
        // Mantenemos esto por compatibilidad, aunque no se use
    }

    public void detenerProcesoInmediatamente() {
        if (this.procesoEnEjecucion != null) {
            this.procesoEnEjecucion = null;
            this.interrupcionActiva = false; 
        }
    }
}