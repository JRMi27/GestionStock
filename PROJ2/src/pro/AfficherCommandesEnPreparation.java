package pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AfficherCommandesEnPreparation extends JFrame {
    private JTextArea commandesTextArea;
    private JButton refreshButton;
    private Connection connection;

    public AfficherCommandesEnPreparation() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return;
        }

        // Configuration de la fenêtre
        setTitle("Commandes en cours de préparation");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme uniquement cette fenêtre
        setLayout(new BorderLayout());

        // Composants
        commandesTextArea = new JTextArea();
        commandesTextArea.setEditable(false); // Empêche la modification du texte
        JScrollPane scrollPane = new JScrollPane(commandesTextArea);
        add(scrollPane, BorderLayout.CENTER);

        refreshButton = new JButton("Rafraîchir");
        add(refreshButton, BorderLayout.SOUTH);

        // Charger les commandes en cours de préparation
        loadCommandesEnPreparation();

        // Action du bouton Rafraîchir
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCommandesEnPreparation();
            }
        });

        setVisible(true);
    }

    // Méthode pour charger les commandes en cours de préparation
    private void loadCommandesEnPreparation() {
        commandesTextArea.setText(""); // Efface l'ancienne liste
        try {
            String query = "SELECT id, dateCommande FROM cmdeapprodepot WHERE statutCommande = 'En cours de préparation'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder commandes = new StringBuilder();
            while (rs.next()) {
                commandes.append("Commande ")
                         .append(rs.getInt("id"))
                         .append(" - Date : ")
                         .append(rs.getDate("dateCommande"))
                         .append("\n");
            }

            if (commandes.length() == 0) {
                commandesTextArea.setText("Aucune commande en cours de préparation.");
            } else {
                commandesTextArea.setText(commandes.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des commandes !");
        }
    }

    // Point d'entrée pour tester cette classe seule
    public static void main(String[] args) {
        new AfficherCommandesEnPreparation();
    }
}
