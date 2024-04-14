package org.elis.recycle.database;

import java.util.Optional;

public interface Database {

    Optional<Product> findProduct(String barcode);

}
