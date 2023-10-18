package example.cayenne;

import example.cayenne.auto._OvChipkaart;

public class OvChipkaart extends _OvChipkaart {

    private static final long serialVersionUID = 1L;

    public String toString() {
        return "Kaart nummer: " + kaartNummer + " - Saldo: " + saldo + " - Klasse: " + klasse;
    }

}
