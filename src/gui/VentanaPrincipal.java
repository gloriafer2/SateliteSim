/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import clases.Proceso;
import datastructura.ListaEnlazada;
import java.util.concurrent.Semaphore;
import javax.swing.table.DefaultTableModel;
import logic.CPUThread;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.BorderLayout;



/**
 *
 * @author Gloria
 */
public class VentanaPrincipal extends javax.swing.JFrame {
// Esta es la cola donde guardo los procesos generados
datastructura.ListaEnlazada colaListos = new datastructura.ListaEnlazada();
datastructura.ListaEnlazada colaBloqueados = new datastructura.ListaEnlazada();
datastructura.ListaEnlazada colaSuspendidos = new datastructura.ListaEnlazada();
private int procesosExitosos = 0;
private int procesosFallidos = 0;
String algoritmoActual = "FCFS";


public void sumarExito() {
        this.procesosExitosos++;
        actualizarGrafica(); 
    }

    public void sumarFallo() {
        this.procesosFallidos++;
        actualizarGrafica();
    }
    
    public void actualizarMonitorMemoria() {
    int totalEnRAM = 0; // Empezamos de cero absoluto

    // 1. Sumar lo que hay en Listos
    datastructura.Nodo actualL = colaListos.getInicio();
    while (actualL != null) {
        totalEnRAM += actualL.getDato().getMemoriaMb();
        actualL = actualL.getSiguiente();
    }

    datastructura.Nodo actualB = colaBloqueados.getInicio();
    while (actualB != null) {
        totalEnRAM += actualB.getDato().getMemoriaMb();
        actualB = actualB.getSiguiente();
    }

    if (cpu != null && cpu.getProcesoEnEjecucion() != null) {
        totalEnRAM += cpu.getProcesoEnEjecucion().getMemoriaMb();
    }

    // Actualizar la variable y la barra visual
    this.memoriaUsadaActual = totalEnRAM;
    prgMemoria.setValue(totalEnRAM);
    prgMemoria.setString(totalEnRAM + " MB / 200 MB");
}
    
    public void refrescarBarraMemoria() {
    int sumaRAM = 0;

    datastructura.Nodo auxL = colaListos.getInicio();
    while (auxL != null) {
        sumaRAM += auxL.getDato().getMemoriaMb();
        auxL = auxL.getSiguiente();
    }

    datastructura.Nodo auxB = colaBloqueados.getInicio();
    while (auxB != null) {
        sumaRAM += auxB.getDato().getMemoriaMb();
        auxB = auxB.getSiguiente();
    }

    if (cpu != null && cpu.getProcesoEnEjecucion() != null) {
        sumaRAM += cpu.getProcesoEnEjecucion().getMemoriaMb();
    }

    this.memoriaUsadaActual = sumaRAM;
    prgMemoria.setValue(sumaRAM);
    prgMemoria.setString(sumaRAM + " MB / 200 MB");
}
    
    public void limpiarProcesosExpirados() {
    int tiempoActual = segundosMision;
    datastructura.Nodo anterior = null;
    datastructura.Nodo actual = colaListos.getInicio();

    while (actual != null) {
        clases.Proceso p = actual.getDato();
        
        if (tiempoActual >= p.getDeadline()) {
            if (anterior == null) {
                colaListos.setInicio(actual.getSiguiente());
            } else {
                anterior.setSiguiente(actual.getSiguiente());
            }
            
            p.setEstado("Fallido");
            sumarFallo(); //
            escribirLog("EXPIRADO: " + p.getNombre() + " salió de RAM por Deadline.");
            
            actual = actual.getSiguiente();
        } else {
            anterior = actual;
            actual = actual.getSiguiente();
        }
    }
    actualizarMonitorMemoria();
    actualizarTablas();
}
    
    
    
    
    
    

int segundosMision = 0;
private int contadorGlobal = 1; 
private Semaphore semaforoGlobal = new Semaphore(1);
private CPUThread cpu;
// Para llevar el control de los 200MB de RAM
clases.Proceso procesoEnEjecucion = null;
    /**
     * Creates new form VentanaPrincipal
     */
// El satélite tiene una RAM limitada
private final int MAX_MEMORIA_RAM = 200; // MB
private int memoriaUsadaActual = 0;

public int getSegundosMision(){
    return segundosMision;
}


    public VentanaPrincipal() {
        initComponents();
        actualizarMonitorMemoria();
        
       
        cpu = new CPUThread(colaListos, colaBloqueados, semaforoGlobal, this);
        generarProcesosIniciales();
        cpu.start(); 
        
        
        
        new javax.swing.Timer(1000, (e) -> {
            segundosMision++;
            restarDeadline(colaListos);
            restarDeadline(colaBloqueados);
            restarDeadline(colaSuspendidos);

          
             
            liberarMemoriaYRevisarSuspendidos(null); 
            actualizarTablas(); 
            lblReloj.setText("Reloj de mision: " + segundosMision + "s");
        }).start();

    }
    
    
            public void actualizarModoSistema(String modo) {
            lblModo.setText("MODO: " + modo);
            if (modo.equals("KERNEL")) {
                lblModo.setForeground(java.awt.Color.RED); // Rojo para alertar que el SO tiene el control
            } else {
                lblModo.setForeground(java.awt.Color.BLUE); // Azul para ejecución normal
            }
        }

        public int getRetrasoSimulacion() {
            return sldVelocidad.getValue();
        }
    
        public void gestionarTransicionesDeColas() {
            datastructura.Nodo auxB = colaBloqueados.getInicio();
            while (auxB != null) {
                clases.Proceso p = auxB.getDato();
                datastructura.Nodo siguiente = auxB.getSiguiente(); // Guardamos el siguiente antes de borrar

                if (p.getEstado().equals("Listo")) {
                    colaBloqueados.eliminarProcesoEspecifico(p);
                    colaListos.agregar(p);
                    escribirLog("TRANSICIÓN: " + p.getNombre() + " volvió a Listos (RAM).");
                }
                auxB = siguiente;
            }
    }
    
    
       private void restarDeadline(datastructura.ListaEnlazada lista) {
    datastructura.Nodo aux = lista.getInicio();
    
    while (aux != null) {
        clases.Proceso p = aux.getDato();
        datastructura.Nodo siguienteNodo = aux.getSiguiente(); 

        // 1. Reducir Deadline de vida siempre
        if (p.getDeadline() > 0) {
            p.setDeadline(p.getDeadline() - 1);
        }

        // --- LÓGICA DE BLOQUEADOS ---
        if (p.getEstado().equals("Bloqueado")) {
            
            if (memoriaUsadaActual > 170) {
                p.setEstado("Bloqueado-Suspendido");
                lista.eliminarProcesoEspecifico(p); 
                colaSuspendidos.agregarAlFinal(p);
                
                memoriaUsadaActual -= p.getMemoriaMb(); 
                escribirLog("SWAP-OUT: " + p.getNombre() + " enviado a disco por falta de espacio (Bloqueado-Suspendido).");
            } 
            else {
                p.setCiclosBloqueo(p.getCiclosBloqueo() - 1);

                if (p.getCiclosBloqueo() <= 0) {
                    lista.eliminarProcesoEspecifico(p);
                    p.setEstado("Listo");
                    colaListos.agregarAlFinal(p);
                    if (!algoritmoActual.equals("FCFS")){
                        reordenarColaSegunAlgoritmo();
                    } 
                }
            }
        }

        else if (p.getEstado().equals("Bloqueado-Suspendido")) {
            p.setCiclosBloqueo(p.getCiclosBloqueo() - 1);
            
            if (p.getCiclosBloqueo() <= 0) {
                p.setEstado("Listo-Suspendido");
                escribirLog("DISCO: " + p.getNombre() + " terminó su E/S en disco. Estado: Listo-Suspendido.");
            }
        }
        
        else if (p.getEstado().equals("Listo-Suspendido")) {
            if ((memoriaUsadaActual + p.getMemoriaMb()) <= MAX_MEMORIA_RAM) {
                lista.eliminarProcesoEspecifico(p);
                p.setEstado("Listo");
                colaListos.agregarAlFinal(p);
                memoriaUsadaActual += p.getMemoriaMb();
                reordenarColaSegunAlgoritmo();
                escribirLog("SWAP-IN: " + p.getNombre() + " vuelve a RAM.");
            }
        }

        aux = siguienteNodo;
    }
}

       
        private int obtenerMejorDeadlineEnSwap() {
            if (colaSuspendidos.estaVacia()) return 999999; 

            int min = 999999;
            datastructura.Nodo temp = colaSuspendidos.getInicio();
            while (temp != null) {
                if (temp.getDato().getDeadline() < min) {
                    min = temp.getDato().getDeadline();
                }
                temp = temp.getSiguiente();
            }
            return min;
        }

       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblReloj = new javax.swing.JLabel();
        tbListos = new javax.swing.JScrollPane();
        tblListos = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblSwapListos = new javax.swing.JTable();
        cbxAlgoritmos = new javax.swing.JComboBox<>();
        txtQuantum = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBloqueados = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSwapBloqueados = new javax.swing.JTable();
        btnInterrupcion = new javax.swing.JButton();
        btnGenerar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCPU = new javax.swing.JTable();
        panelGrafica = new javax.swing.JPanel();
        lblModo = new javax.swing.JLabel();
        sldVelocidad = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        prgMemoria = new javax.swing.JProgressBar();
        lblMemoriaUsada = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblReloj.setFont(new java.awt.Font("OCR A Extended", 1, 14)); // NOI18N
        lblReloj.setForeground(new java.awt.Color(255, 255, 255));
        lblReloj.setText("Reloj de Misión: 00:00");
        jPanel1.add(lblReloj, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        tbListos.setBackground(new java.awt.Color(0, 153, 153));
        tbListos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 5, true));

        tblListos.setBackground(new java.awt.Color(0, 153, 153));
        tblListos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Instrucciones", "Prioridad", "Deadline", "PC", "MAR", "Memoria(MB)", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tbListos.setViewportView(tblListos);

        jPanel1.add(tbListos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 410, 240));

        txtLog.setBackground(new java.awt.Color(0, 51, 51));
        txtLog.setColumns(20);
        txtLog.setRows(5);
        txtLog.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jScrollPane3.setViewportView(txtLog);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 331, 380));

        tblSwapListos.setBackground(new java.awt.Color(0, 153, 153));
        tblSwapListos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Memoria", "Deadline", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblSwapListos);

        jPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 400, 300));

        cbxAlgoritmos.setBackground(new java.awt.Color(153, 204, 255));
        cbxAlgoritmos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SRT", "Prioridad Estatica", "EDF" }));
        cbxAlgoritmos.setAutoscrolls(true);
        cbxAlgoritmos.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 204, 204), null));
        cbxAlgoritmos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAlgoritmosActionPerformed(evt);
            }
        });
        jPanel1.add(cbxAlgoritmos, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        txtQuantum.setForeground(new java.awt.Color(255, 255, 255));
        txtQuantum.setText("Quantum:");
        jPanel1.add(txtQuantum, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 10, 63, -1));

        tblBloqueados.setBackground(new java.awt.Color(0, 102, 102));
        tblBloqueados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Memoria", "Deadline"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblBloqueados);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(745, 100, 320, 230));

        tblSwapBloqueados.setBackground(new java.awt.Color(0, 153, 153));
        tblSwapBloqueados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "MAR", "PC"
            }
        ));
        jScrollPane5.setViewportView(tblSwapBloqueados);
        if (tblSwapBloqueados.getColumnModel().getColumnCount() > 0) {
            tblSwapBloqueados.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanel1.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(753, 361, 320, 230));

        btnInterrupcion.setBackground(new java.awt.Color(255, 51, 51));
        btnInterrupcion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnInterrupcion.setForeground(new java.awt.Color(255, 255, 255));
        btnInterrupcion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pngtree-an-3d-alert-button-icon-design-with-blue-and-red-color-png-image_14430137.png"))); // NOI18N
        btnInterrupcion.setText("SIMULAR IMPACTO");
        btnInterrupcion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnInterrupcion.setDefaultCapable(false);
        btnInterrupcion.setMaximumSize(new java.awt.Dimension(100, 647));
        btnInterrupcion.setMinimumSize(new java.awt.Dimension(90, 647));
        btnInterrupcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInterrupcionActionPerformed(evt);
            }
        });
        jPanel1.add(btnInterrupcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 520, 310, 70));

        btnGenerar.setText("Generar 20 Procesos");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });
        jPanel1.add(btnGenerar, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 620, -1, -1));

        tblCPU.setBackground(new java.awt.Color(51, 204, 0));
        tblCPU.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblCPU.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Estado", "MAR", "PC", "Instrucciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblCPU);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 710, 410, 100));

        panelGrafica.setBackground(new java.awt.Color(0, 0, 0));
        panelGrafica.setLayout(new java.awt.BorderLayout());
        jPanel1.add(panelGrafica, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 620, 280, 190));

        lblModo.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        lblModo.setText("Modo:Usuario");
        jPanel1.add(lblModo, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 240, -1));

        sldVelocidad.setMaximum(2000);
        sldVelocidad.setMinimum(100);
        sldVelocidad.setValue(500);
        jPanel1.add(sldVelocidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Listos");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 200, 60));

        jLabel8.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Procesador");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 680, 90, 20));

        jLabel9.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Suspendidos Bloqueados");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 330, 200, 20));

        jLabel10.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Bloqueados");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 40, 200, 60));

        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Suspendidos");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 200, 60));

        prgMemoria.setStringPainted(true);
        jPanel1.add(prgMemoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 70, -1, 20));

        lblMemoriaUsada.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMemoriaUsada.setForeground(new java.awt.Color(255, 255, 255));
        lblMemoriaUsada.setText("MEMORIA");
        jPanel1.add(lblMemoriaUsada, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, 120, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed

       try {
        semaforoGlobal.acquire();
        
        for (int i = 1; i <= 20; i++) {
            String id = "P" + contadorGlobal;
            String nombre = "Tarea_" + contadorGlobal;
            int instrucciones = (int) (Math.random() * 50) + 10;
            int prioridad = (int) (Math.random() * 5) + 1; 
            int memoriaMB = (int) (Math.random() * 50) + 10; 
            int deadline = segundosMision + (int) (Math.random() * 200) + 100;

            clases.Proceso nuevo = new clases.Proceso(id, nombre, instrucciones, prioridad, deadline, memoriaMB);
            
            if (this.memoriaUsadaActual + memoriaMB <= 200) { 
                nuevo.setEstado("Listo");
                
                if (algoritmoActual.equals("FCFS") || algoritmoActual.equals("RR")) {
                    colaListos.agregarAlFinal(nuevo); 
                } else if (algoritmoActual.equals("EDF")) {
                    colaListos.agregarAlFinal(nuevo);
                    colaListos.ordenarPorDeadline();
                } else {
                    colaListos.agregar(nuevo); 
                }
                
                this.memoriaUsadaActual += memoriaMB;
                escribirLog("ADMITIDO: " + nombre + " (" + memoriaMB + "MB)");
            } else {
                nuevo.setEstado("Suspendido");
                colaSuspendidos.agregarAlFinal(nuevo);
                escribirLog("SWAP: " + nombre + " a disco (RAM Llena)");
            }
            contadorGlobal++;
        }
        
        // --- YA NO necesitas llamar a ordenarColaPorPrioridad(colaListos) aquí ---
        // porque la lógica de arriba ya los puso en su lugar según el algoritmo.

        semaforoGlobal.release();
        actualizarTablas();
        actualizarMonitorMemoria(); 
        refrescarBarraMemoria();
        
    } catch (InterruptedException e) {
        System.out.println("Error en botón generar: " + e.getMessage());
    }
    }//GEN-LAST:event_btnGenerarActionPerformed

    
    
    private void ordenarColaPorPrioridad(datastructura.ListaEnlazada lista) {
    if (lista.estaVacia()) return;
    
    boolean intercambio;
    do {
        intercambio = false;
        datastructura.Nodo actual = lista.getInicio();
        while (actual != null && actual.getSiguiente() != null) {
            if (actual.getDato().getPrioridad() > actual.getSiguiente().getDato().getPrioridad()) {
                clases.Proceso temp = actual.getDato();
                actual.setDato(actual.getSiguiente().getDato());
                actual.getSiguiente().setDato(temp);
                intercambio = true;
            }
            actual = actual.getSiguiente();
        }
    } while (intercambio);
}
    private void btnInterrupcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInterrupcionActionPerformed
      if (cpu.getProcesoEnEjecucion() != null) {
        escribirLog("SOLICITUD: Interrupción externa enviada a la CPU.");
        cpu.activarInterrupcion(); 
    } else {
        escribirLog("SISTEMA: No hay procesos en ejecución para interrumpir.");
    }
    }//GEN-LAST:event_btnInterrupcionActionPerformed

    private void cbxAlgoritmosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxAlgoritmosActionPerformed
        algoritmoActual = cbxAlgoritmos.getSelectedItem().toString();
        escribirLog("Algoritmo cambiado a: " + algoritmoActual);
        
        reordenarColaSegunAlgoritmo();
    }//GEN-LAST:event_cbxAlgoritmosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnInterrupcion;
    private javax.swing.JComboBox<String> cbxAlgoritmos;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblMemoriaUsada;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblReloj;
    private javax.swing.JPanel panelGrafica;
    private javax.swing.JProgressBar prgMemoria;
    private javax.swing.JSlider sldVelocidad;
    private javax.swing.JScrollPane tbListos;
    private javax.swing.JTable tblBloqueados;
    private javax.swing.JTable tblCPU;
    private javax.swing.JTable tblListos;
    private javax.swing.JTable tblSwapBloqueados;
    private javax.swing.JTable tblSwapListos;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JLabel txtQuantum;
    // End of variables declaration//GEN-END:variables

       public void actualizarTablas() {
           java.awt.EventQueue.invokeLater(() -> {
        try {
           
            DefaultTableModel modL = (DefaultTableModel) tblListos.getModel();
            DefaultTableModel modB = (DefaultTableModel) tblBloqueados.getModel();
            DefaultTableModel modSL = (DefaultTableModel) tblSwapListos.getModel();
            DefaultTableModel modSB = (DefaultTableModel) tblSwapBloqueados.getModel();
            DefaultTableModel modCPU = (DefaultTableModel) tblCPU.getModel(); 

            // Limpieza total
            modL.setRowCount(0); modB.setRowCount(0); 
            modSL.setRowCount(0); modSB.setRowCount(0);
            modCPU.setRowCount(0);

            clases.Proceso pEnEjecucion = cpu.getProcesoEnEjecucion();

            
            if (pEnEjecucion != null) {
                modCPU.addRow(new Object[]{pEnEjecucion.getId(),"Ejecucion", pEnEjecucion.getInstruccionesTotales(), pEnEjecucion.getMar(), pEnEjecucion.getPc()});
            }

          
            datastructura.Nodo actual = colaListos.getInicio();
            while (actual != null) {
                clases.Proceso p = actual.getDato();
                    modL.addRow(new Object[]{p.getId(), p.getNombre(), p.getInstruccionesTotales(), p.getPrioridad(), p.getDeadline(), p.getPc(), p.getMar(), p.getMemoriaMb(), p.getEstado()});
                
                actual = actual.getSiguiente();
            }

            // 3. RAM: Bloqueados
            actual = colaBloqueados.getInicio();
            while (actual != null) {
                clases.Proceso p = actual.getDato();
                modB.addRow(new Object[]{p.getId(), p.getNombre(), p.getMemoriaMb(), p.getDeadline()});
                actual = actual.getSiguiente();
            }

            actual = colaSuspendidos.getInicio();
            while (actual != null) {
                clases.Proceso p = actual.getDato();
                Object[] fila = {p.getId(), p.getNombre(), p.getMemoriaMb(), p.getDeadline(), p.getEstado()};
                if (p.getEstado().equals("Bloqueado-Suspendido")) { modSB.addRow(fila); } 
                else { modSL.addRow(fila); }
                actual = actual.getSiguiente();
            }
            } catch (Exception e) {
          
            System.out.println("Sincronización visual recuperada.");
        }
    });
}
        

      public void actualizarPanelCPU(clases.Proceso p) {
        }
    
    public void reordenarColaSegunAlgoritmo() {
    
    if (algoritmoActual.equalsIgnoreCase("FCFS")) {
        return; 
    }

    if (algoritmoActual.equalsIgnoreCase("EDF")) {
        colaListos.ordenarPorDeadline();
    } 
    else if (algoritmoActual.equalsIgnoreCase("Prioridad Estatica")) {
        colaListos.ordenarPorPrioridad();
    }
    
    else if (algoritmoActual.equalsIgnoreCase("SRT")) {
        colaListos.ordenarPorTiempoRestante();
    }
    else if (algoritmoActual.equalsIgnoreCase("RR")) {
        return;
    }
    actualizarTablas(); 
}
        
    
    
            public String getAlgoritmoActual() {
            return algoritmoActual;
        }

        public int getQuantumGlobal() {
                try {
                return Integer.parseInt(txtQuantum.getText());
            } catch (NumberFormatException e) {
                return 3; 
              }
        }

        public ListaEnlazada getColaListos() {
            return colaListos;
        }
    
    public void escribirLog(String mensaje) {
   txtLog.append("[" + segundosMision + "s] " + mensaje + "\n");
     txtLog.setCaretPosition( txtLog.getDocument().getLength());
}
    
        public void liberarMemoriaYRevisarSuspendidos(clases.Proceso pTerminado) {
        if (pTerminado != null) {
            this.memoriaUsadaActual -= pTerminado.getMemoriaMb();
            escribirLog("RAM: Liberado " + pTerminado.getNombre());
        }

        
        synchronized(colaSuspendidos) {
            if (colaSuspendidos.estaVacia()) return;

            datastructura.Nodo aux = colaSuspendidos.getInicio();
            clases.Proceso urgente = null;

            while (aux != null) {
                clases.Proceso p = aux.getDato();

                
                if (urgente == null || p.getDeadline() < urgente.getDeadline()) {
                    urgente = p;
                }
                aux = aux.getSiguiente();
            }

            if (urgente != null && (memoriaUsadaActual + urgente.getMemoriaMb()) <= 200) {
                colaSuspendidos.eliminarProcesoEspecifico(urgente);
                memoriaUsadaActual += urgente.getMemoriaMb();

                urgente.setEstado("Listo"); 
                colaListos.agregarAlFinal(urgente);

                escribirLog("SWAP-IN: " + urgente.getNombre() + " subió por urgencia (Deadline: " + urgente.getDeadline() + ")");
                reordenarColaSegunAlgoritmo();
            }
        }
        actualizarTablas();
    }
        
    
    public void actualizarGrafica() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Misión Exitosa", procesosExitosos);
        dataset.setValue("Fallo de Deadline", procesosFallidos);

        // 2. Crear Gráfica
        JFreeChart chart = ChartFactory.createPieChart(
                "RENDIMIENTO DEL SATÉLITE", 
                dataset, 
                true, true, false);

        // 3. Mostrar en el panel
        ChartPanel chartPanel = new ChartPanel(chart);
        panelGrafica.removeAll();
        panelGrafica.add(chartPanel, java.awt.BorderLayout.CENTER);

        // 4. Refrescar
        panelGrafica.revalidate();
        panelGrafica.repaint();
    }
    
    public void generarProcesosIniciales() {
        for (int i = 0; i < 5; i++) {
            int inst = (int) (Math.random() * 10) + 5; 
            int prio = (int) (Math.random() * 5) + 1; 
            int dead = (int) (Math.random() * 50) + 20;
            int memoriaMb = 20;

            Proceso nuevo = new Proceso("P-INI" + i, "Sat-Inicial-" + i, inst, prio, dead, memoriaMb);
            nuevo.setEstado("Listo");
            
            

            this.colaListos.agregar(nuevo);
            
            this.memoriaUsadaActual += memoriaMb;
        }
        this.escribirLog("[SO] Configuración inicial cargada: 5 procesos listos.");
        this.actualizarTablas();
    }
    
    
    
    
}


