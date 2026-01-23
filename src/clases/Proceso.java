/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Gloria
 */
public class Proceso {
    // Datos de identificación 
    private String id;
    private String nombre;
    private String estado;  
    private int prioridad;
    
    // Registros de la CPU 
    private int pc;  // Program Counter
    private int mar; // Memory Address Register
    
    // Tiempos y Deadline (Tiempo Límite)
    private int instruccionesTotales;
    private int tiempoLimite; 

    // Constructor con los 5 parámetros necesarios
    public Proceso(String id, String nombre, int instrucciones, int prioridad, int deadline) {
        this.id = id;
        this.nombre = nombre;
        this.instruccionesTotales = instrucciones;
        this.prioridad = prioridad;
        this.tiempoLimite = deadline;
        
        this.estado = "Nuevo"; 
        this.pc = 0; 
        this.mar = 0;
    }
    
    // --- Métodos Getters (Necesarios para la JTable) ---
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public int getPrioridad() { return prioridad; }
    public int getInstruccionesTotales() { return instruccionesTotales; }
    public int getTiempoLimite() { return tiempoLimite; }
    public int getPc() { return pc; }
    public int getMar() { return mar; }

    // --- Métodos Setters (Para actualizar el proceso durante la simulación) ---
    public void setEstado(String estado) { this.estado = estado; }
    public void setPc(int pc) { this.pc = pc; }
    public void setMar(int mar) { this.mar = mar; }
}
