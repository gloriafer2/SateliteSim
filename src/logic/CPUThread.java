/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import clases.Proceso;
import datastructura.ListaEnlazada;
import java.util.concurrent.Semaphore; // Esta librería SÍ está permitida

public class CPUThread extends Thread {
    private ListaEnlazada colaListos;
    private ListaEnlazada colaBloqueados;
    private Proceso procesoEnEjecucion;
    private Semaphore semaforoCPU; // Para exclusión mutua y control de interrupciones
    private boolean interrupcionActiva = false;

    public CPUThread(ListaEnlazada listos, ListaEnlazada bloqueados, Semaphore sem) {
        this.colaListos = listos;
        this.colaBloqueados = bloqueados;
        this.semaforoCPU = sem;
    }
    
    

    @Override
    public void run() {
        while (true) {
            try {
                // El semáforo garantiza que solo un hilo acceda a las colas a la vez
                semaforoCPU.acquire();

                if (interrupcionActiva) {
                    manejarInterrupcion();
                } else if (procesoEnEjecucion == null && !colaListos.estaVacia()) {
                    // Sacamos el proceso de tu lista manual
                    procesoEnEjecucion = colaListos.eliminarPrimero();
                    procesoEnEjecucion.setEstado("Ejecucion");
                }

                if (procesoEnEjecucion != null) {
                    // Ejecuta un ciclo (incrementa PC, MAR y baja instrucciones/deadline)
                    procesoEnEjecucion.ejecutarCiclo();
                    
                    if (procesoEnEjecucion.getInstruccionesTotales() <= 0) {
                        procesoEnEjecucion.setEstado("Terminado");
                        procesoEnEjecucion = null;
                    }
                }

                semaforoCPU.release();
                Thread.sleep(1000); // Simula el ciclo de reloj del satélite

            } catch (InterruptedException e) {
                System.out.println("Error en el CPU: " + e.getMessage());
            }
        }
    }

    private void manejarInterrupcion() {
        if (procesoEnEjecucion != null) {
            procesoEnEjecucion.setEstado("Bloqueado");
            colaBloqueados.agregar(procesoEnEjecucion); // Lo movemos a tu lista manual de bloqueados
            procesoEnEjecucion = null;
        }
        interrupcionActiva = false;
    }

    public void activarInterrupcion() {
        this.interrupcionActiva = true;
    }
    
    public Proceso getProcesoEnEjecucion(){
        return this.procesoEnEjecucion;
    }
}