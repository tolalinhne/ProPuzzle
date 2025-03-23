package Utils;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LaTexRenderer {
    public static Image renderFormula(String latex, float size) {
        try {
            TeXFormula formula = new TeXFormula(latex);

            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);

            BufferedImage bImg = new BufferedImage(
                    icon.getIconWidth(),
                    icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2d = bImg.createGraphics();
            icon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();

            return SwingFXUtils.toFXImage(bImg, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

