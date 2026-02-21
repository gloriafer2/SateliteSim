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
            ventana.limpiarProcesosExpirados();
            ventana.actualizarTablas();
            
                try {
                    semaforoCPU.acquire();
                    if (interrupcionActiva || procesoEnEjecucion == null) {
                        // Si hay interrupción o el CPU está buscando procesos, el SO tiene el control
                        ventana.actualizarModoSistema("KERNEL"); 
                    } else {
                        // Si hay un proceso corriendo, es tiempo del usuario
                        ventana.actualizarModoSistema("USUARIO");
                    }
                    
                    
                    
                    

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
                        comprobarExpropiacion();
                        procesoEnEjecucion.ejecutarCiclo();
                        ventana.actualizarTablas();
                        

                        // Aquí sumo un FALLO a la gráfica
                        if (procesoEnEjecucion.getDeadline()<= 0){
                            procesoEnEjecucion.setEstado("FALLIDO");
                            ventana.sumarFallo(); // <--- NUEVO: Actualiza la gráfica
                            ventana.escribirLog("ALERTA: "+ procesoEnEjecucion.getNombre() + " abortado por Deadline.");
                            ventana.liberarMemoriaYRevisarSuspendidos(procesoEnEjecucion);
                            procesoEnEjecucion = null;
                            ventana.refrescarBarraMemoria();
                        } 
                        // eXITO a la gráfica
                        else if (procesoEnEjecucion.getInstruccionesTotales() <= 0){
                            procesoEnEjecucion.setEstado("Terminado");
                            ventana.sumarExito(); // <--- NUEVO: Actualiza la gráfica
                            ventana.escribirLog("Exito: " + procesoEnEjecucion.getNombre() + " completó su misión.");
                            ventana.liberarMemoriaYRevisarSuspendidos(procesoEnEjecucion);
                            procesoEnEjecucion = null;
                            ventana.actualizarMonitorMemoria();
                            
                        }
                        //  Round Robin ---
                        //  para evitar errores de mayúsculas/minúsculas
                            String algo = ventana.getAlgoritmoActual();
                            if (algo.equalsIgnoreCase("RR") || algo.equalsIgnoreCase("Round Robin")) {
                                quantumRestante--; // Restamos uno en cada ciclo de reloj
                                if (quantumRestante <= 0) {
                                    procesoEnEjecucion.setEstado("Listo");
                                    
                                    colaListos.agregarAlFinal(procesoEnEjecucion); 
                                    ventana.escribirLog("TIMEOUT: " + procesoEnEjecucion.getNombre() + " va al final de la fila.");
                                    procesoEnEjecucion = null; // Liberamos el CPU para el siguiente
                                }
                            }
                    }

                    semaforoCPU.release();
                    Thread.sleep(500); 

                } catch (InterruptedException e) {
                    System.out.println("Error en el CPU: " + e.getMessage());
                }
            }
        
        
    }
    
    
    private void comprobarExpropiacion() {
    // Si la cola está vacía, no hay con quién pelear el puesto
    if (ventana.getColaListos().estaVacia()) return;

    String algoritmo = ventana.getAlgoritmoActual();
    clases.Proceso candidato = ventana.getColaListos().getInicio().getDato();
    boolean expropiar = false;

    // Evaluamos según las reglas de cada algoritmo preemptivo
    if (algoritmo.equals("EDF")) {
        // En EDF, importa quién está más cerca de morir
        if (candidato.getDeadline() < procesoEnEjecucion.getDeadline()) {
            expropiar = true;
        }
    } else if (algoritmo.equals("Prioridad Estatica")) { 
        // En Prioridad, 1 es más urgente que 5
        if (candidato.getPrioridad() < procesoEnEjecucion.getPrioridad()) {
            expropiar = true;
        }
    } else if (algoritmo.equals("SRT")) {
        // En SRT, importa quién tiene menos instrucciones restantes
        if (candidato.getInstruccionesTotales() < procesoEnEjecucion.getInstruccionesTotales()) {
            expropiar = true;
        }
    }

    // Si el candidato es mejor, hacemos el cambio de contexto (Preemption)
    if (expropiar) {
        ventana.escribirLog("EXPROPIACIÓN: " + candidato.getNombre() + " interrumpió a " + procesoEnEjecucion.getNombre());
        
        // 1. Devolvemos el proceso actual a la RAM
        procesoEnEjecucion.setEstado("Listo");
        ventana.getColaListos().agregarAlFinal(procesoEnEjecucion);
        
        // 2. Subimos al candidato al CPU
        procesoEnEjecucion = ventana.getColaListos().eliminarPrimero();
        procesoEnEjecucion.setEstado("Ejecucion");
        
        // 3. Reordenamos la cola para que el expropiado quede en su lugar correcto
        ventana.reordenarColaSegunAlgoritmo();
    }
}

    private void manejarInterrupcion() {
    ventana.actualizarModoSistema("KERNEL");
    
    if (procesoEnEjecucion != null) {
       
        procesoEnEjecucion.setEstado("Bloqueado");
        procesoEnEjecucion.setCiclosBloqueo(5); 
        
        if (!colaBloqueados.contiene(procesoEnEjecucion)) {
            colaBloqueados.agregarAlFinal(procesoEnEjecucion);
        }
        
        ventana.escribirLog("INTERRUPCIÓN: " + procesoEnEjecucion.getNombre() + " movido a BLOQUEADOS.");
        procesoEnEjecucion = null;
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
    }

    public void detenerProcesoInmediatamente() {
        if (this.procesoEnEjecucion != null) {
            this.procesoEnEjecucion = null;
            this.interrupcionActiva = false; 
        }
        
        
    }
    
}