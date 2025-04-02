package pro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ModifierStockProduit extends JFrame {
    private JComboBox<String> produitComboBox;
    private JTextField quantiteField;
    private JLabel ancienneQuantiteLabel;
    private JButton modifierButton;
    private Connection connection;

    public ModifierStockProduit() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return;
        }

        // Configuration de la fenêtre
        setTitle("Modifier Stock Produit");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel produitLabel = new JLabel("Produit :");
        produitLabel.setBounds(20, 20, 100, 30);
        add(produitLabel);

        produitComboBox = new JComboBox<>();
        produitComboBox.setBounds(120, 20, 300, 30);
        add(produitComboBox);

        JLabel ancienneQuantiteTextLabel = new JLabel("Quantité Actuelle :");
        ancienneQuantiteTextLabel.setBounds(20, 70, 120, 30);
        add(ancienneQuantiteTextLabel);

        ancienneQuantiteLabel = new JLabel("...");
        ancienneQuantiteLabel.setBounds(150, 70, 100, 30);
        add(ancienneQuantiteLabel);

        JLabel quantiteLabel = new JLabel("Nouvelle Quantité :");
        quantiteLabel.setBounds(20, 120, 150, 30);
        add(quantiteLabel);

        quantiteField = new JTextField();
        quantiteField.setBounds(170, 120, 150, 30);
        add(quantiteField);

        modifierButton = new JButton("Modifier");
        modifierButton.setBounds(150, 170, 100, 30);
        add(modifierButton);

        // Charger les produits dans le ComboBox
        loadProduits();

        // Ajouter un listener pour afficher la quantité actuelle lorsque l'utilisateur sélectionne un produit
        produitComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherAncienneQuantite();
            }
        });

        // Action pour modifier
        modifierButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifierQuantite();
            }
        });

        setVisible(true);
    }

    // Charger les produits disponibles dans le JComboBox
    private void loadProduits() {
        try {
            String query = "SELECT idProduit, designationProduit FROM produit";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String produit = rs.getInt("idProduit") + " - " + rs.getString("designationProduit");
                produitComboBox.addItem(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits !");
        }
    }

    // Afficher l'ancienne quantité pour le produit sélectionné
    private void afficherAncienneQuantite() {
        String selectedProduit = (String) produitComboBox.getSelectedItem();
        if (selectedProduit == null) {
            ancienneQuantiteLabel.setText("...");
            return;
        }

        int idProduit = Integer.parseInt(selectedProduit.split(" - ")[0]);

        try {
            String query = "SELECT stockMag FROM produit WHERE idProduit = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, idProduit);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int ancienneQuantite = rs.getInt("stockMag");
                ancienneQuantiteLabel.setText(String.valueOf(ancienneQuantite));
            } else {
                ancienneQuantiteLabel.setText("...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération de la quantité actuelle !");
        }
    }

    // Modifier la quantité en stock pour le produit sélectionné
    private void modifierQuantite() {
        String selectedProduit = (String) produitComboBox.getSelectedItem();
        if (selectedProduit == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit !");
            return;
        }

        int idProduit = Integer.parseInt(selectedProduit.split(" - ")[0]);
        int nouvelleQuantite;

        try {
            nouvelleQuantite = Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une quantité valide !");
            return;
        }

        try {
            String query = "UPDATE produit SET stockMag = ? WHERE idProduit = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, idProduit);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Stock modifié avec succès !");
            afficherAncienneQuantite(); // Rafraîchir l'ancienne quantité
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification !");
        }
    }

    public static void main(String[] args) {
        new ModifierStockProduit();
    }
}
	