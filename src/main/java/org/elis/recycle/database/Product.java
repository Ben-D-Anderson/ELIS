package org.elis.recycle.database;

public class Product {

    private final String barcode;

    public Product(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

}
