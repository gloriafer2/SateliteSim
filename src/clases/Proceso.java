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
    
    // Registros de la CPU (Exigidos por el PDF)
    private int pc;  // Program Counter
    private int mar; // Memory Address Register
    
    // Tiempos y Deadline
    private int instruccionesTotales;
    private int tiempoLimite; 

    //  Gestión de Memoria y Bloqueo 
    private int memoriaMb;       // Tamaño en memoria para el Planificador de Mediano Plazo
    private int ciclosBloqueo;   // Para simular E/S

    public Proceso(String id, String nombre, int instrucciones, int prioridad, int deadline) {
        this.id = id;
        this.nombre = nombre;
        this.instruccionesTotales = instrucciones;
        this.prioridad = prioridad;
        this.tiempoLimite = deadline;
        
        this.estado = "Nuevo"; 
        this.pc = 0; 
        this.mar = (int)(Math.random() * 5000); 
        // Generamos un tamaño de memoria aleatorio (10MB a 60MB)
        this.memoriaMb = (int)(Math.random() * 50) + 10;
        this.ciclosBloqueo = 0;
    }

    /**
     * "El PC y el MAR incrementarán una unidad por cada ciclo del reloj."
     */
    public void ejecutarCiclo() {
        this.pc++;
        this.mar++;
        this.instruccionesTotales--;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public int getPrioridad() { return prioridad; }
    public int getInstruccionesTotales() { return instruccionesTotales; }
    public int getTiempoLimite() { return tiempoLimite; }
    public int getPc() { return pc; }
    public int getMar() { return mar; }
    public int getMemoriaMb() { return memoriaMb; }
    public int getCiclosBloqueo() { return ciclosBloqueo; }

    // --- Setters ---
    public void setEstado(String estado) { this.estado = estado; }
    public void setPc(int pc) { this.pc = pc; }
    public void setMar(int mar) { this.mar = mar; }
    public void setInstruccionesTotales(int instruccionesTotales) { this.instruccionesTotales = instruccionesTotales; }
    public void setCiclosBloqueo(int ciclos) { this.ciclosBloqueo = ciclos; }
}