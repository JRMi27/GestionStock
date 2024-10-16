package pro;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class GestionReception extends JFrame {
    private JComboBox<String> commandesBox;
    private JTextField quantiteField;
    private JButton enregistrerButton;
    private Connection connection;

    public GestionReception() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return; // Si la connexion échoue, on arrête l'initialisation
        }

        // Interface graphique
        setTitle("Gestion de Réception des Commandes");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel commandeLabel = new JLabel("Sélectionner une commande :");
        commandeLabel.setBounds(20, 20, 200, 30);
        add(commandeLabel);

        commandesBox = new JComboBox<>();
        commandesBox.setBounds(220, 20, 150, 30);
        add(commandesBox);

        JLabel quantiteLabel = new JLabel("Quantité reçue :");
        quantiteLabel.setBounds(20, 70, 200, 30);
        add(quantiteLabel);

        quantiteField = new JTextField();
        quantiteField.setBounds(220, 70, 150, 30);
        add(quantiteField);

        enregistrerButton = new JButton("Enregistrer");
        enregistrerButton.setBounds(150, 150, 100, 30);
        add(enregistrerButton);

        // Charger les commandes en attente
        loadCommandes();

        // Action sur le bouton enregistrer
        enregistrerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enregistrerQuantite();
            }
        });

        setVisible(true);
    }

    // Méthode pour charger les commandes dans la comboBox
    private void loadCommandes() {
        try {
            String query = "SELECT id, dateCommande FROM cmdeapprodepot WHERE statutCommande = 'En attente'";
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

    // Méthode pour enregistrer la quantité reçue
    private void enregistrerQuantite() {
        String selectedCommande = (String) commandesBox.getSelectedItem();
        int idCommande = Integer.parseInt(selectedCommande.split(" ")[1]); // Récupérer l'ID de la commande
        int quantiteReçue = Integer.parseInt(quantiteField.getText());

        try {
            String query = "UPDATE cmdeapprodepot SET quantiteRecue = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, quantiteReçue);
            pstmt.setInt(2, idCommande);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quantité enregistrée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de la quantité !");
        }
    }

    public static void main(String[] args) {
        new GestionReception();
    }
}
