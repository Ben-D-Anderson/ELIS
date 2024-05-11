package org.elis.recycle.gui;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

import java.awt.*;

@AllArgsConstructor
public class FontContainer {

    @SerializedName("name")
    private final String name;
    @SerializedName("style")
    private final int style;
    @SerializedName("size")
    private final int size;

    public FontContainer(Font font) {
        this.name = font.getName();
        this.style = font.getStyle();
        this.size = font.getSize();
    }

    public Font asFont() {
        return new Font(name, style, size);
    }

}
