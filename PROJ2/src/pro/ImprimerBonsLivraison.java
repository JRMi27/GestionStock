package pro;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ImprimerBonsLivraison extends JFrame {
    private JTextArea bonsTextArea;
    private JButton refreshButton;
    private Connection connection;

    public ImprimerBonsLivraison() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return;
        }

        // Configuration de la fenêtre
        setTitle("Bons de Livraison");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        bonsTextArea = new JTextArea();
        bonsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(bonsTextArea);
        add(scrollPane, BorderLayout.CENTER);

        refreshButton = new JButton("Rafraîchir");
        add(refreshButton, BorderLayout.SOUTH);

        // Charger les bons de livraison
        loadBonsLivraison();

        // Action pour rafraîchir
        refreshButton.addActionListener(e -> loadBonsLivraison());

        setVisible(true);
    }

    private void loadBonsLivraison() {
        bonsTextArea.setText("");
        try {
            String query = "SELECT id, dateCommande, quantitePreparee FROM cmdeapprodepot WHERE statutCommande = 'Prête pour livraison'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder bons = new StringBuilder();
            while (rs.next()) {
                bons.append("Commande ").append(rs.getInt("id"))
                    .append(" - Date : ").append(rs.getDate("dateCommande"))
                    .append(" - Quantité Préparée : ").append(rs.getInt("quantitePreparee"))
                    .append("\n");
            }

            if (bons.length() == 0) {
                bonsTextArea.setText("Aucun bon de livraison disponible.");
            } else {
                bonsTextArea.setText(bons.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des bons !");
        }
    }

    public static void main(String[] args) {
        new ImprimerBonsLivraison();
    }
}
