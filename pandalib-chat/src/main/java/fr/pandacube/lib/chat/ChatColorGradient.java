package fr.pandacube.lib.chat;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.format.TextColor;

/**
 * A custom gradient with a least 2 colors in it.
 */
public class ChatColorGradient {
    private record GradientColor(float location, TextColor color) { }

    private final List<GradientColor> colors = new ArrayList<>();

    /**
     * Put a specific color at a specific location in the gradient.
     * @param gradientLocation the location in the gradient.
     * @param gradientColor the color to put at this location.
     * @return this.
     */
    public synchronized ChatColorGradient add(float gradientLocation, TextColor gradientColor) {
        colors.add(new GradientColor(gradientLocation, gradientColor));
        return this;
    }

    /**
     * Compute a color by interpolating between the 2 colors surrounding the provided location.
     * @param gradientLocation the location at which to pick the gradient color.
     * @return the computed color.
     */
    public synchronized TextColor pickColorAt(float gradientLocation) {
        if (colors.isEmpty())
            throw new IllegalStateException("Must define at least one color in this ChatValueGradient instance.");
        if (colors.size() == 1)
            return colors.get(0).color();

        colors.sort((p1, p2) -> Float.compare(p1.location(), p2.location()));

        if (gradientLocation <= colors.get(0).location())
            return colors.get(0).color();
        if (gradientLocation >= colors.get(colors.size() - 1).location())
            return colors.get(colors.size() - 1).color();

        int p1 = 1;
        for (; p1 < colors.size(); p1++) {
            if (colors.get(p1).location() >= gradientLocation)
                break;
        }
        int p0 = p1 - 1;
        float v0 = colors.get(p0).location(), v1 = colors.get(p1).location();
        TextColor cc0 = colors.get(p0).color(), cc1 = colors.get(p1).color();
        return ChatColorUtil.interpolateColor(v0, v1, gradientLocation, cc0, cc1);
    }
}
