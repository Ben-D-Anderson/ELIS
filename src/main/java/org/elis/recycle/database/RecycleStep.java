package org.elis.recycle.database;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecycleStep {

    @SerializedName("instruction")
    private final String instruction;
    @SerializedName("requires_waste_disposal")
    private final boolean requiresWasteDisposal;

}
