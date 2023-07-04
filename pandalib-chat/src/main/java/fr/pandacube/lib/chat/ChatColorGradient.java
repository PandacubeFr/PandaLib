package fr.pandacube.lib.chat;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * A custom gradient with at least 2 colors in it.
 */
public class ChatColorGradient {
    private record GradientColor(
            float location,
            TextColor color
    ) implements Comparable<GradientColor> {
        @Override
        public int compareTo(@NotNull ChatColorGradient.GradientColor o) {
            return Float.compare(location(), o.location());
        }
    }

    private final List<GradientColor> colors = new ArrayList<>();

    /**
     * Put a specific color at a specific location in the gradient.
     * @param gradientLocation the location in the gradient.
     * @param gradientColor the color to put at this location.
     * @return this.
     */
    public synchronized ChatColorGradient add(float gradientLocation, TextColor gradientColor) {
        colors.add(new GradientColor(gradientLocation, gradientColor));
        colors.sort(null);
        return this;
    }

    /**
     * Compute a color by interpolating between the 2 colors surrounding the provided location.
     * @param gradientLocation the location at which to pick the gradient color.
     * @return the computed color.
     */
    public synchronized TextColor pickColorAt(float gradientLocation) {
        if (colors.isEmpty())
            throw new IllegalStateException("Must define at least one color in this ChatColorGradient instance.");
        if (colors.size() == 1)
            return colors.get(0).color();

        int i = 0;
        for (; i < colors.size(); i++) {
            if (gradientLocation <= colors.get(i).location())
                break;
        }

        if (i == 0)
            return colors.get(i).color();
        if (i == colors.size())
            return colors.get(colors.size() - 1).color();

        int p = i - 1;
        float pLoc = colors.get(p).location();
        float iLoc = colors.get(i).location();
        TextColor pCol = colors.get(p).color();
        TextColor iCol = colors.get(i).color();
        return ChatColorUtil.interpolateColor(pLoc, iLoc, gradientLocation, pCol, iCol);
    }
}
