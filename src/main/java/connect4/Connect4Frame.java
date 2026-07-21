 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package connect4;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 *
 * @author MSI
 */
public class Connect4Frame extends javax.swing.JFrame {

    /**
     * Creates new form Connect4Frame.
     *
     * BUGFIX: the original code only initialized the board/turn state inside
     * the "start"/"reset" button handlers, never here. That meant a freshly
     * opened window was unplayable - every column click silently did nothing,
     * because `board` cells default to char 0, not '.', so insert()'s
     * `== '.'` empty-cell check never matched. See README for before/after
     * evidence.
     */
    public Connect4Frame() {
        initComponents();
        newGame();
    }

    
       private int rows = 6;
       private int columns = 7;
       
          
        // BUGFIX: this must be true, not false. `turn == true` means "it's the
        // human's turn to place 'o'"; with it starting false, the very first
        // click of every game fell into the *other* branch of insert() and
        // was silently placed as 'x' (the AI's colour) with no AI reply at
        // all, skewing the whole game's turn order. Verified with a test that
        // showed 1 'x' / 0 'o' after a single human click before the fix.
        boolean turn = true;
        private char[][] board = new char[rows][columns];

        /**
         * Resets the board to empty and hands the first move to the human.
         * Called on construction and whenever "start"/"reset" is clicked, so
         * every game - not just the first - begins from a clean, correct state.
         */
        private void newGame() {
            turn = true;
            generateEmpty();
            displayBoard();
        }
        
        
        // AI player
    private AIPlayer aiPlayer = new AIPlayer();
    
    
    // ------------------------------------------------------------------
    // Board state & rules
    // ------------------------------------------------------------------

    private char[][] cloneBoard(char[][] board) {
        char[][] copy = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, columns);
        }
        return copy;
    }
    
    private int findAvailableRow(int col, char[][] board) {
        for (int row = rows - 1; row >= 0; row--) {
            if (board[row][col] == '.') {
                return row;
            } 
        }
        return -1; 
    }
    
    
    private boolean checkWin(char player, char[][] board) {
        // Check rows
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns - 3; j++) {
                if (board[i][j] == player && board[i][j + 1] == player && board[i][j + 2] == player && board[i][j + 3] == player) {
                    return true;
                }
            }
        }

        // Check columns
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows - 3; i++) {
                if (board[i][j] == player && board[i + 1][j] == player && board[i + 2][j] == player && board[i + 3][j] == player) {
                    return true;
                }
            }
        }

        // Check diagonals
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < columns - 3; j++) {
                if (board[i][j] == player && board[i + 1][j + 1] == player && board[i + 2][j + 2] == player && board[i + 3][j + 3] == player) {
                    return true;
                }
            }
        }

        for (int i = 0; i < rows - 3; i++) {
            for (int j = 3; j < columns; j++) {
                if (board[i][j] == player && board[i + 1][j - 1] == player && board[i + 2][j - 2] == player && board[i + 3][j - 3] == player) {
                    return true;
                }
            }
        }

        return false;
    }
    
    
    
    private void testDraw() {
        if (isBoardFull(board)) {
            JOptionPane.showMessageDialog(rootPane, "The game is a draw!");
        }
    }


    private boolean isBoardFull(char[][] board) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (board[row][col] == '.') {
                    return false; // Found an empty cell
                }
            }
        }
        return true; // All cells are filled
    }

    

    private void generateEmpty(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                    board[i][j] = '.';
                }
            }
    }        
    
    
    
    
    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    private void displayBoard() {
        jPanel1.removeAll();
        jPanel1.setLayout(new GridLayout(rows, columns));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JLabel cell = new JLabel();
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if (board[i][j] == 'x') {
                    cell.setOpaque(true);
                    cell.setBackground(Color.blue);

                }
                if (board[i][j] == 'o') {
                    cell.setOpaque(true);
                    cell.setBackground(Color.red);

                }
                
                jPanel1.add(cell);
            }
        }
        jPanel1.revalidate();
        jPanel1.repaint();
    }
    
    
    
    
    
    
    private void insert(int col){
        for (int i = rows - 1; i >= 0; i--) {
            if (i >= 0 && board[i][col] == '.' && turn) {
                board[i][col] = 'o';
                turn = false;
                break;
            } else if (i >= 0 && board[i][col] == '.' && !turn) {
                board[i][col] = 'x';
                turn = true;
                break;
            }
        }
        displayBoard();
        testWin();
        testDraw();

      
        if (!turn && !isBoardFull(board)) {
            Integer aiMove = aiPlayer.makeMove(board);
            if (aiMove != null) {
                for (int i = rows - 1; i >= 0; i--) {
                    if (board[i][aiMove] == '.') {
                        board[i][aiMove] = 'x';
                        turn = true;
                        break;
                    }
                }
                displayBoard();
                testWin();
                testDraw();
            } else {
                System.out.println("AI cannot make a move.");
            }
        }
    }
    
    
    
    
    
    
    private void testWin(){
        // Test rows
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns - 3; j++) {
                if (board[i][j] == 'o' && board[i][j + 1] == 'o' && board[i][j + 2] == 'o' && board[i][j + 3] == 'o') {
//                    playSound("win.wav");
                    JOptionPane.showMessageDialog(rootPane, "you have won the game!!!");
                    return;
                }
                if (board[i][j] == 'x' && board[i][j + 1] == 'x' && board[i][j + 2] == 'x' && board[i][j + 3] == 'x') {
                    JOptionPane.showMessageDialog(rootPane, "the computer has won the game...");
                    return;
                }
            }
        }

        // Test columns
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows - 3; i++) {
                if (board[i][j] == 'o' && board[i + 1][j] == 'o' && board[i + 2][j] == 'o' && board[i + 3][j] == 'o') {
                    JOptionPane.showMessageDialog(rootPane, "you have won the game!!!");
                    return;
                }
                if (board[i][j] == 'x' && board[i + 1][j] == 'x' && board[i + 2][j] == 'x' && board[i + 3][j] == 'x') {
                    JOptionPane.showMessageDialog(rootPane, "the computer has won the game...");
                    return;
                }
            }
        }

        // Test diagonals from top-left to bottom-right
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 0; j < columns - 3; j++) {
                if (board[i][j] == 'o' && board[i + 1][j + 1] == 'o' && board[i + 2][j + 2] == 'o' && board[i + 3][j + 3] == 'o') {
                    JOptionPane.showMessageDialog(rootPane, "you have won the game!!!");
                    return;
                }
                if (board[i][j] == 'x' && board[i + 1][j + 1] == 'x' && board[i + 2][j + 2] == 'x' && board[i + 3][j + 3] == 'x') {
                    JOptionPane.showMessageDialog(rootPane, "the computer has won the game...");
                    return;
                }
            }
        }

        // Test diagonals from top-right to bottom-left
        for (int i = 0; i < rows - 3; i++) {
            for (int j = 3; j < columns; j++) {
                if (board[i][j] == 'o' && board[i + 1][j - 1] == 'o' && board[i + 2][j - 2] == 'o' && board[i + 3][j - 3] == 'o') {
                    JOptionPane.showMessageDialog(rootPane, "you have won the game!!!");
                    return;
                }
                if (board[i][j] == 'x' && board[i + 1][j - 1] == 'x' && board[i + 2][j - 2] == 'x' && board[i + 3][j - 3] == 'x') {
                    JOptionPane.showMessageDialog(rootPane, "the computer has won the game...");
                    return;
                }
            }
        }
    }
    
    
    // ------------------------------------------------------------------
    // AI opponent: depth-3 minimax with alpha-beta pruning over a
    // line-scoring heuristic (see evaluate()/evaluateLine()). Plays as 'x'.
    // ------------------------------------------------------------------
    private class AIPlayer {
        private int[] minimax(char[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
            int bestColumn = -1;
            int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            if (depth == 0 || isBoardFull(board) || checkWin('x', board) || checkWin('o', board)) {
                bestScore = evaluate(board);
            } else {
                for (int col = 0; col < columns; col++) {
                    if (isValidMove(col, board)) {
                        char[][] newBoard = cloneBoard(board);
                        int row = findAvailableRow(col, newBoard);
                        newBoard[row][col] = maximizingPlayer ? 'o' : 'x';
                        int[] currentMove = minimax(newBoard, depth - 1, alpha, beta, !maximizingPlayer);

                        if (maximizingPlayer) {
                            if (currentMove[0] > bestScore) {
                                bestScore = currentMove[0];
                                bestColumn = col;
                            }
                            alpha = Math.max(alpha, bestScore);
                        } else {
                            if (currentMove[0] < bestScore) {
                                bestScore = currentMove[0];
                                bestColumn = col;
                            }
                            beta = Math.min(beta, bestScore);
                        }

                        if (beta <= alpha) {
                            break; // Pruning
                        }
                    }
                }
            }
            return new int[]{bestScore, bestColumn};
        }
            private Integer makeMove(char[][] board) {
            int[] result = minimax(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            return result[1];
        }

        private boolean isValidMove(int col, char[][] board) {
            return board[0][col] == '.';
        }

        private int evaluate(char[][] board) {
            int score = 0;

            // Check rows
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns - 3; col++) {
                    score += evaluateLine(board[row][col], board[row][col + 1], board[row][col + 2], board[row][col + 3]);
                }
            }

            // Check columns
           
            
            for (int col = 0; col < columns; col++) {
                for (int row = 0; row < rows - 3; row++) {
                    score += evaluateLine(board[row][col], board[row + 1][col], board[row + 2][col], board[row + 3][col]);
                }
            }

            // Check diagonals
            for (int row = 0; row < rows - 3; row++) {
                for (int col = 0; col < columns - 3; col++) {
                    score += evaluateLine(board[row][col], board[row + 1][col + 1], board[row + 2][col + 2], board[row + 3][col + 3]);
                }
            }

            for (int row = 0; row < rows - 3; row++) {
                for (int col = 3; col < columns; col++) {
                    score += evaluateLine(board[row][col], board[row + 1][col - 1], board[row + 2][col - 2], board[row + 3][col - 3]);
                }
            }

            return score;
        }

    private int evaluateLine(char cell1, char cell2, char cell3, char cell4) {
        int score = 0;
        int aiCount = 0;
        int opponentCount = 0;

        // Count AI and opponent pieces in the line
        if (cell1 == 'o') aiCount++;
        if (cell2 == 'o') aiCount++;
        if (cell3 == 'o') aiCount++;
        if (cell4 == 'o') aiCount++;
        if (cell1 == 'x') opponentCount++;
        if (cell2 == 'x') opponentCount++;
        if (cell3 == 'x') opponentCount++;
        if (cell4 == 'x') opponentCount++;

        // Assign score based on counts
        if (aiCount == 4) {
            score += 100;
        } else if (aiCount == 3 && opponentCount == 0) {
            score += 10;
        } else if (aiCount == 2 && opponentCount == 0) {
            score += 1;
        }

        if (opponentCount == 4) {
            score -= 100;
        } else if (opponentCount == 3 && aiCount == 0) {
            score -= 10;
        } else if (opponentCount == 2 && aiCount == 0) {
            score -= 1;
        }

        return score;
    }

    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // ------------------------------------------------------------------
    // GUI wiring (NetBeans GUI Builder generated) - left mostly as-is;
    // safe to regenerate from a designer, not hand-edited beyond button
    // handler bodies above.
    // ------------------------------------------------------------------
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(430, 208, 550, 450);

        jButton1.setText("1st");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(430, 130, 52, 80);

        jButton2.setText("2nd");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(510, 130, 55, 80);

        jButton3.setText("3rd");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(590, 130, 53, 80);

        jButton4.setText("4th");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(670, 130, 52, 80);

        jButton5.setText("5th");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(750, 130, 52, 80);

        jButton6.setText("6th");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(830, 130, 52, 80);

        jButton7.setText("7th");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7);
        jButton7.setBounds(910, 130, 52, 80);

        jButton8.setBackground(new java.awt.Color(51, 51, 255));
        jButton8.setFont(new java.awt.Font("STKaiti", 0, 48)); // NOI18N
        jButton8.setText("start");
        jButton8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8);
        jButton8.setBounds(117, 197, 169, 122);

        jButton9.setBackground(new java.awt.Color(255, 102, 102));
        jButton9.setFont(new java.awt.Font("STKaiti", 0, 48)); // NOI18N
        jButton9.setText("reset");
        jButton9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton9);
        jButton9.setBounds(132, 425, 137, 118);

        jLabel1.setFont(new java.awt.Font("Yu Gothic Light", 1, 18)); // NOI18N
        jLabel1.setText("yaman alfarouh ");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(60, 70, 176, 31);

        jLabel3.setFont(new java.awt.Font("Yu Gothic Light", 1, 18)); // NOI18N
        jLabel3.setText("202110258");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(300, 70, 167, 30);

        jLabel5.setFont(new java.awt.Font("Yu Gothic Light", 1, 18)); // NOI18N
        jLabel5.setText("Connect 4 game for AI lab project - built by Yaman Al Farouh");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(10, 30, 480, 30);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("اذا بتفوز بتربح غسالة");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(750, 30, 220, 50);

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel4.setText("Rules: connect 4 in a row (horizontally, vertically, or diagonally) before the AI does. Each square is dropped into the lowest open row of the chosen column, just like the real game.");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(10, 660, 1080, 40);

        jLabel6.setBackground(new java.awt.Color(45, 238, 34));
        jLabel6.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel6.setText("didn't know how to make circules) and you can connect them in any order even in diagonals..GOOD LUCK ");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(10, 690, 1240, 50);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel7.setText("omar tantawi");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(60, 110, 150, 20);

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel8.setText("202010489");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(290, 110, 120, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        insert(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        insert(1);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        insert(2);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        insert(3);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        insert(4);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        insert(5);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        insert(6);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // "start" button
        newGame();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // "reset" button
        newGame();
    }//GEN-LAST:event_jButton9ActionPerformed

    // ------------------------------------------------------------------
    // Entry point
    // ------------------------------------------------------------------
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
            java.util.logging.Logger.getLogger(Connect4Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Connect4Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Connect4Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Connect4Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Connect4Frame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
