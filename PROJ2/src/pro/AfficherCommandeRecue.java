package pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AfficherCommandeRecue extends JFrame {
    private JComboBox<String> commandesBox;
    private JTextArea detailsTextArea;
    private JButton afficherButton;
    private Connection connection;

    public AfficherCommandeRecue() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return;
        }

        // Configuration de la fenêtre
        setTitle("Afficher Commande Reçue");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel commandeLabel = new JLabel("Sélectionner une commande reçue :");
        commandeLabel.setBounds(20, 20, 200, 30);
        add(commandeLabel);

        commandesBox = new JComboBox<>();
        commandesBox.setBounds(220, 20, 200, 30);
        add(commandesBox);

        afficherButton = new JButton("Afficher");
        afficherButton.setBounds(200, 70, 100, 30);
        add(afficherButton);

        detailsTextArea = new JTextArea();
        detailsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(detailsTextArea);
        scrollPane.setBounds(20, 120, 450, 130);
        add(scrollPane);

        // Charger les commandes reçues dans la liste déroulante
        loadCommandesRecues();

        // Action du bouton Afficher
        afficherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                afficherCommande();
            }
        });

        setVisible(true);
    }

    private void loadCommandesRecues() {
        try {
            String query = "SELECT id, dateCommande FROM cmdeapprodepot WHERE statutCommande = 'Reçue'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String commande = "Commande " + rs.getInt("id") + " - " + rs.getDate("dateCommande");
                commandesBox.addItem(commande);
            }

            if (commandesBox.getItemCount() == 0) {
                commandesBox.addItem("Aucune commande reçue disponible");
                commandesBox.setEnabled(false);
                afficherButton.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des commandes reçues !");
        }
    }

    private void afficherCommande() {
        String selectedCommande = (String) commandesBox.getSelectedItem();
        if (selectedCommande == null || selectedCommande.contains("Aucune commande")) {
            JOptionPane.showMessageDialog(this, "Aucune commande à afficher !");
            return;
        }

        int idCommande = Integer.parseInt(selectedCommande.split(" ")[1]); // Récupérer l'ID de la commande

        try {
            String query = "SELECT * FROM cmdeapprodepot WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                StringBuilder commandeDetails = new StringBuilder();
                commandeDetails.append("ID Commande : ").append(rs.getInt("id")).append("\n")
                               .append("Date Commande : ").append(rs.getDate("dateCommande")).append("\n")
                               .append("Quantité Reçue : ").append(rs.getInt("quantiteRecue")).append("\n")
                               .append("Statut : ").append(rs.getString("statutCommande")).append("\n");
                detailsTextArea.setText(commandeDetails.toString());
            } else {
                detailsTextArea.setText("Aucune commande reçue trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération de la commande !");
        }
    }

    public static void main(String[] args) {
        new AfficherCommandeRecue();
    }
}
 