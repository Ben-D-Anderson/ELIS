package org.elis.recycle.database;

import java.util.List;
import java.util.Optional;

public class Database {

    private final List<Product> products;

    public Database(List<Product> products) {
        this.products = products;
    }

    public Optional<Product> findProduct(String barcode) {
        return products.stream()
                .filter(product -> product.getBarcode().equals(barcode))
                .findFirst();
    }

}
