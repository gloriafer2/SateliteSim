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



/**
 *
 * @author Gloria
 */
public class VentanaPrincipal extends javax.swing.JFrame {
// Esta es la cola donde guardo los procesos generados
datastructura.ListaEnlazada colaListos = new datastructura.ListaEnlazada();
datastructura.ListaEnlazada colaBloqueados = new datastructura.ListaEnlazada();
datastructura.ListaEnlazada colaSuspendidos = new datastructura.ListaEnlazada();
String algoritmoActual = "FCFS";



int segundosMision = 0;
private int contadorGlobal = 1; // Para que P1, P2... funcionen
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
       
        cpu = new CPUThread(colaListos, colaBloqueados, semaforoGlobal, this);
        cpu.start(); 
        
        new javax.swing.Timer(1000, (e) -> {
            segundosMision++;
            // --- CUENTA REGRESIVA DE DEADLINES (RTOS) ---
            restarDeadline(colaListos);
            restarDeadline(colaBloqueados);
            restarDeadline(colaSuspendidos);

            // --- SWAP OUT: De RAM a DISCO ---
            // --- DESBLOQUEO DIRECTO EN RAM ---
             
                        // --- DESBLOQUEO EN DISCO (Cambio de estado abajo) ---
            liberarMemoriaYRevisarSuspendidos(null); 
            actualizarTablas(); 
            lblReloj.setText("Reloj de mision: " + segundosMision + "s");
        }).start();

    }
    
        public void gestionarTransicionesDeColas() {
        // CASO RAM: Revisar si alguien en Bloqueados ya pasó a "Listo"
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

                    if (p.getDeadline() > 0) {
                        p.setDeadline(p.getDeadline() - 1);
                    }

                    if (p.getEstado().equals("Bloqueado")) {
                        if (Math.random() > 0.90) { 

                            // 1. COMPARACIÓN DE DEADLINE
                            int mejorEnSwap = obtenerMejorDeadlineEnSwap();

                            if (p.getDeadline() <= mejorEnSwap) {
                                // CASO: Prioridad para volver a RAM
                                p.setEstado("Listo");

                                // ELIMINACIÓN FÍSICA: Usamos 'lista' que es la que estamos recorriendo
                                lista.eliminarProcesoEspecifico(p); 

                                if (!colaListos.contiene(p)) {
                                    colaListos.agregar(p);
                                }
                                escribirLog("ADMITIDO: " + p.getNombre() + " vuelve a RAM por urgencia.");
                            } else {
                                // CASO: Hay procesos más urgentes en disco, este se va a SWAP
                                p.setEstado("Suspendido-Listo");

                                lista.eliminarProcesoEspecifico(p);
                                colaSuspendidos.agregar(p);

                                // Liberar memoria RAM (importante para el límite de 200MB)
                                memoriaUsadaActual -= p.getMemoriaMb();
                                escribirLog("SWAP-OUT: " + p.getNombre() + " enviado a disco.");

                                liberarMemoriaYRevisarSuspendidos(null); 
                            }
                        }
                    }
                    aux = siguienteNodo;
                }
}

// METODO AUXILIAR: Necesario para que restarDeadline sepa contra qué comparar
       
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

        // Método auxiliar para la comparación de prioridad
       
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
        jLabel1 = new javax.swing.JLabel();
        cbxAlgoritmos = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtQuantum = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBloqueados = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSwapBloqueados = new javax.swing.JTable();
        btnInterrupcion = new javax.swing.JButton();
        btnGenerar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCPU = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblReloj.setText("Reloj de Misión: 00:00");

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

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane3.setViewportView(txtLog);

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

        jLabel1.setText("Suspendidos");

        cbxAlgoritmos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SRT", "Prioridad Estatica", "EDF" }));
        cbxAlgoritmos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAlgoritmosActionPerformed(evt);
            }
        });

        jLabel2.setText("Quantum:");

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

        btnInterrupcion.setText("SIMULAR IMPACTO");
        btnInterrupcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInterrupcionActionPerformed(evt);
            }
        });
        jScrollPane4.setViewportView(tblSwap);

        jLabel1.setText("Procesos en Disco");

        btnGenerar.setText("Generar 20 Procesos");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        tblCPU.setBackground(new java.awt.Color(102, 255, 102));
        tblCPU.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblCPU.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Estado", "MAR", "PC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblCPU);
        if (tblCPU.getColumnModel().getColumnCount() > 0) {
            tblCPU.getColumnModel().getColumn(1).setResizable(false);
        }

        jLabel3.setText("PROCESADOR(PCB)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(cbxAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtQuantum, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(lblReloj))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(tbListos, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnInterrupcion)
                                                .addGap(39, 39, 39)
                                                .addComponent(btnGenerar))
                                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(51, 51, 51)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtQuantum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addComponent(lblReloj)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbListos, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnInterrupcion)
                                    .addComponent(btnGenerar))
                                .addGap(63, 63, 63)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(409, Short.MAX_VALUE))
        );

        btnInterrupcion.setText("SIMULAR IMPACTO");
        btnInterrupcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInterrupcionActionPerformed(evt);
            }
        });

        btnGenerar.setText("Generar 20 Procesos");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
      
        try{
            semaforoGlobal.acquire();
                memoriaUsadaActual = 0; 
                colaListos.vaciar();      // Asegúrate de tener este método en tu lista
                colaBloqueados.vaciar();
                colaSuspendidos.vaciar();
                contadorGlobal = 1;
                
               
                escribirLog("--- SIMULACIÓN REINICIADA ---");
                
                for (int i = 1; i <= 20; i++) {
                    String id = "P" + contadorGlobal;
                    String nombre = "Tarea_" + contadorGlobal;
                    int instrucciones = (int) (Math.random() * 50) + 10;
                    int prioridad = (int) (Math.random() * 5) + 1; // Prioridad del 1 al 5
                    int memoriaMB = (int) (Math.random() * 50) + 10; 

                        // Memoria aleatoria
                    int deadline = segundosMision + 150 + (instrucciones * 2) + (int) (Math.random()*200);

                    clases.Proceso nuevo = new clases.Proceso(id, nombre, instrucciones, prioridad, deadline);
                    
                // cabe en ram
                if (memoriaUsadaActual + memoriaMB <= 200) { 
                    nuevo.setEstado("Listo");
                    colaListos.agregar(nuevo); 
                    memoriaUsadaActual += memoriaMB;
                    escribirLog("ADMITIDO: " + nombre + " en RAM (" + memoriaMB + "MB)");
                } else {
                    // Si no cabe, se va a la cola de Suspendidos
                    nuevo.setEstado("Suspendido");
                    colaSuspendidos.agregar(nuevo);
                    escribirLog("SWAP: " + nombre + " enviado a disco por falta de RAM");
                }
                contadorGlobal++;
            }
            ordenarColaPorPrioridad(colaListos); 
            semaforoGlobal.release();
            actualizarTablas();
        }catch (InterruptedException e) {}
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
       clases.Proceso p = cpu.getProcesoEnEjecucion(); 

        if (p != null) {
            // 1. Forzamos al CPU a soltarlo ANTES de moverlo a la lista
            cpu.detenerProcesoInmediatamente(); 

            p.setEstado("Bloqueado");

            // 2. Movimiento atómico
            if (!colaBloqueados.contiene(p)) {
                colaBloqueados.agregar(p);
            }

            escribirLog("EXTRAÍDO: " + p.getNombre() + " movido a Bloqueados.");

            // 3. Limpiamos la referencia local y refrescamos
            this.procesoEnEjecucion = null; 
            actualizarTablas();
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblReloj;
    private javax.swing.JScrollPane tbListos;
    private javax.swing.JTable tblBloqueados;
    private javax.swing.JTable tblCPU;
    private javax.swing.JTable tblListos;
    private javax.swing.JTable tblSwapBloqueados;
    private javax.swing.JTable tblSwapListos;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtQuantum;
    // End of variables declaration//GEN-END:variables

       public void actualizarTablas() {
    // 1. Obtener modelos
            DefaultTableModel modL = (DefaultTableModel) tblListos.getModel();
            DefaultTableModel modB = (DefaultTableModel) tblBloqueados.getModel();
            DefaultTableModel modSL = (DefaultTableModel) tblSwapListos.getModel();
            DefaultTableModel modSB = (DefaultTableModel) tblSwapBloqueados.getModel();
            DefaultTableModel modCPU = (DefaultTableModel) tblCPU.getModel(); 

            // 2. Limpiar todo
            modL.setRowCount(0);
            modB.setRowCount(0);
            modSL.setRowCount(0);
            modSB.setRowCount(0);
            modCPU.setRowCount(0);

            // 3. MONITOR DE CPU (PCB VIVO)
            clases.Proceso pEnEjecucion = cpu.getProcesoEnEjecucion();
            if (pEnEjecucion != null) {
                // Mostramos los datos que ya lograste que cambien
                Object[] filaCPU = {
                    pEnEjecucion.getId(), 
                    "Ejecucion", 
                    pEnEjecucion.getMar(), 
                    pEnEjecucion.getPc()
                };
                modCPU.addRow(filaCPU);
            }

            // 4. TABLA LISTOS (Filtro para no duplicar el que está en CPU)
            datastructura.Nodo actual = colaListos.getInicio();
            while (actual != null) {
                clases.Proceso p = actual.getDato();
                if (pEnEjecucion == null || !p.getId().equals(pEnEjecucion.getId())) {
                    Object[] fila = {p.getId(), p.getNombre(), p.getInstruccionesTotales(), 
                                     p.getPrioridad(), p.getDeadline(), p.getPc(), 
                                     p.getMar(), p.getMemoriaMb(), p.getEstado()};
                    modL.addRow(fila);
                }
                actual = actual.getSiguiente();
            }

            // 5. TABLA BLOQUEADOS (RAM)
            actual = colaBloqueados.getInicio();
            while (actual != null) {
                clases.Proceso p = actual.getDato();
                Object[] fila = {p.getId(), p.getNombre(), p.getMemoriaMb(), p.getDeadline()};
                modB.addRow(fila);
                actual = actual.getSiguiente();
            }

            // 6. TABLAS DE SWAP (DISCO)
            actual = colaSuspendidos.getInicio();
            while (actual != null) {
                clases.Proceso p = actual.getDato();
                Object[] fila = {p.getId(), p.getNombre(), p.getMemoriaMb(), p.getDeadline(), p.getEstado()};

                if (p.getEstado().equals("Bloqueado-Suspendido")) {
                    modSB.addRow(fila); // Derecha Abajo
                } else {
                    modSL.addRow(fila); // Izquierda Abajo
                }
                actual = actual.getSiguiente();
    }
}

// ESTO QUITA EL ERROR ROJO: Crea el método que falta
      public void actualizarPanelCPU(clases.Proceso p) {
            // Aquí pondremos luego el código para mostrar el proceso en el centro
        }
    
    public void reordenarColaSegunAlgoritmo() {
        if (colaListos == null || colaListos.estaVacia()) return;

        switch (algoritmoActual) {
            case "FCFS":
                // No necesita reordenar, es el orden de llegada
                break;
            case "SRT":
                colaListos.ordenarPorTiempoRestante(); // Método que crearemos en la lista
                break;
            case "Prioridad Estática":
                colaListos.ordenarPorPrioridad(); // Ya deberías tener algo similar
                break;
            case "EDF":
                colaListos.ordenarPorDeadline(); // Por tiempo límite
                break;
            case "Round Robin":
                // Se mantiene el orden de llegada
                break;
        }
        actualizarTablas(); // Refrescamos la interfaz para que se vea el nuevo orden
    }
    
            public String getAlgoritmoActual() {
            return algoritmoActual;
        }

        public int getQuantumGlobal() {
            // Si lo tienes en un Spinner o TextField, conviértelo a int
            return 3; 
        }

        public ListaEnlazada getColaListos() {
            return colaListos;
        }
    
    public void escribirLog(String mensaje) {
   txtLog.append("[" + segundosMision + "s] " + mensaje + "\n");
     txtLog.setCaretPosition( txtLog.getDocument().getLength());
}
    // ESTA ES LA ÚNICA VERSIÓN QUE DEBE EXISTIR
    
        public void liberarMemoriaYRevisarSuspendidos(clases.Proceso pTerminado) {
    // 1. Liberar memoria si alguien terminó
        if (pTerminado != null) {
            this.memoriaUsadaActual -= pTerminado.getMemoriaMb();
            escribirLog("RAM: Liberado " + pTerminado.getNombre());
        }

        // 2. BUSCAR AL MÁS URGENTE EN DISCO (EDF)
        // Dentro de liberarMemoriaYRevisarSuspendidos en VentanaPrincipal.java
        synchronized(colaSuspendidos) {
            if (colaSuspendidos.estaVacia()) return;

            datastructura.Nodo aux = colaSuspendidos.getInicio();
            clases.Proceso urgente = null;

            while (aux != null) {
                clases.Proceso p = aux.getDato();

                // CAMBIO CLAVE: Comparamos TODOS los procesos en disco por su Deadline
                // No importa si dice "Suspendido", "Bloqueado-Suspendido" o "Suspendido-Listo"
                if (urgente == null || p.getDeadline() < urgente.getDeadline()) {
                    urgente = p;
                }
                aux = aux.getSiguiente();
            }

            // Si el más urgente cabe en los 200MB, lo subimos
            if (urgente != null && (memoriaUsadaActual + urgente.getMemoriaMb()) <= 200) {
                colaSuspendidos.eliminarProcesoEspecifico(urgente);
                memoriaUsadaActual += urgente.getMemoriaMb();

                // Al subir a RAM, siempre vuelve como "Listo" para que el CPU lo tome
                urgente.setEstado("Listo"); 
                colaListos.agregar(urgente);

                escribirLog("SWAP-IN: " + urgente.getNombre() + " subió por urgencia (Deadline: " + urgente.getDeadline() + ")");
                reordenarColaSegunAlgoritmo();
            }
        }
        actualizarTablas();
    }

    
    
    
    
}


