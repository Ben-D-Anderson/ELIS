package org.elis.recycle.database;

import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Database {

    private final List<Product> products;

    public Optional<Product> findProduct(String barcode) {
        return products.stream()
                .filter(product -> product.getBarcode().equals(barcode))
                .findFirst();
    }

    public List<Product> findProducts(String name) {
        Function<Product, Integer> indexOfSearchInName = product ->
                product.getName().toLowerCase().indexOf(name.toLowerCase());
        Comparator<Product> sortBySearchTermNearStart = Comparator.comparing(indexOfSearchInName);
        Comparator<Product> sortByProductName = Comparator.comparing(Product::getName);

        if (name.isEmpty()) {
            return products.stream()
                    .sorted(sortByProductName)
                    .collect(Collectors.toList());
        }

        return products.stream()
                .filter(product -> indexOfSearchInName.apply(product) >= 0)
                .sorted(sortBySearchTermNearStart.thenComparing(sortByProductName))
                .collect(Collectors.toList());
    }

}
