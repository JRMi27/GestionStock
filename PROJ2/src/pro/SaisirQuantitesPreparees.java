package pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SaisirQuantitesPreparees extends JFrame {
    private JComboBox<String> commandesBox;
    private JTextField quantiteField;
    private JButton enregistrerButton;
    private Connection connection;

    public SaisirQuantitesPreparees() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return;
        }

        // Configuration de la fenêtre
        setTitle("Saisir Quantités Préparées");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel commandeLabel = new JLabel("Sélectionner une commande :");
        commandeLabel.setBounds(20, 20, 200, 30);
        add(commandeLabel);

        commandesBox = new JComboBox<>();
        commandesBox.setBounds(220, 20, 150, 30);
        add(commandesBox);

        JLabel quantiteLabel = new JLabel("Quantité préparée :");
        quantiteLabel.setBounds(20, 70, 200, 30);
        add(quantiteLabel);

        quantiteField = new JTextField();
        quantiteField.setBounds(220, 70, 150, 30);
        add(quantiteField);

        enregistrerButton = new JButton("Enregistrer");
        enregistrerButton.setBounds(150, 150, 100, 30);
        add(enregistrerButton);

        // Charger les commandes
        loadCommandes();

        // Action pour enregistrer
        enregistrerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enregistrerQuantitePreparee();
            }
        });

        setVisible(true);
    }

    private void loadCommandes() {
        try {
            String query = "SELECT id, dateCommande FROM cmdeapprodepot WHERE statutCommande = 'En cours de préparation'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String commande = "Commande " + rs.getInt("id") + " - " + rs.getDate("dateCommande");
                commandesBox.addItem(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void enregistrerQuantitePreparee() {
        String selectedCommande = (String) commandesBox.getSelectedItem();
        int idCommande = Integer.parseInt(selectedCommande.split(" ")[1]);
        int quantitePreparee = Integer.parseInt(quantiteField.getText());

        try {
            String query = "UPDATE cmdeapprodepot SET quantitePreparee = ?, statutCommande = 'Prête pour livraison' WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, quantitePreparee);
            pstmt.setInt(2, idCommande);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quantité préparée enregistrée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement !");
        }
    }

    public static void main(String[] args) {
        new SaisirQuantitesPreparees();
    }
}
