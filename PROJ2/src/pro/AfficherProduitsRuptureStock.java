package pro;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AfficherProduitsRuptureStock extends JFrame {
    private JTextArea produitsTextArea;
    private Connection connection;

    public AfficherProduitsRuptureStock() {
        // Connexion à la base de données
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données !");
            return;
        }

        // Configuration de la fenêtre
        setTitle("Produits en Rupture de Stock");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        produitsTextArea = new JTextArea();
        produitsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(produitsTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // Charger les produits
        loadProduitsRuptureStock();

        setVisible(true);
    }

    private void loadProduitsRuptureStock() {
        produitsTextArea.setText("");
        try {
            String query = "SELECT idProduit, designationProduit FROM produit WHERE stockEntrepot <= 0";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder produits = new StringBuilder();
            while (rs.next()) {
                produits.append("ID : ").append(rs.getInt("idProduit"))
                        .append(" - Nom : ").append(rs.getString("designationProduit"))
                        .append("\n");
            }

            if (produits.length() == 0) {
                produitsTextArea.setText("Aucun produit en rupture de stock.");
            } else {
                produitsTextArea.setText(produits.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits !");
        }
    }

    public static void main(String[] args) {
        new AfficherProduitsRuptureStock();
    }
}
