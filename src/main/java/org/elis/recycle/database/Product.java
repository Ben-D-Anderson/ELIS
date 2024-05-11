package org.elis.recycle.database;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class Product {

    @SerializedName("barcode")
    private final String barcode;
    @SerializedName("name")
    private final String name;
    @SerializedName("steps")
    private final List<RecycleStep> steps;

}
