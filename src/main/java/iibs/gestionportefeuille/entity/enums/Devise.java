package iibs.gestionportefeuille.entity.enums;

public enum Devise {

    XOF("Franc CFA BCEAO", 0),
    EUR("Euro", 2),
    USD("Dollar américain", 2);

    private final String libelle;
    private final int nombreDecimales;

    Devise(String libelle, int nombreDecimales) {
        this.libelle = libelle;
        this.nombreDecimales = nombreDecimales;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getNombreDecimales() {
        return nombreDecimales;
    }
}