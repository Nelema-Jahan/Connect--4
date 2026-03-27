import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class DiscPanel extends JPanel {
    private Color discColor;

    public void setDiscColor(Color color) {
        this.discColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        int margin = 8;
        int discSize = Math.min(width, height) - 2 * margin;
        int x = (width - discSize) / 2;
        int y = (height - discSize) / 2;
        
        // Draw disc with gradient
        if (discColor != null) {
            // Create gradient paint for 3D effect
            GradientPaint gp = new GradientPaint(
                x, y, discColor.brighter(),
                x + discSize, y + discSize, discColor.darker(),
                false
            );
            g2d.setPaint(gp);
            g2d.fillOval(x, y, discSize, discSize);
            
            // Draw shadow effect
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.drawOval(x + 2, y + 2, discSize - 4, discSize - 4);
            
            // Draw highlight
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.drawOval(x + 3, y + 3, discSize / 2, discSize / 2);
            
            // Draw border
            g2d.setColor(new Color(20, 20, 20, 100));
            g2d.setStroke(new java.awt.BasicStroke(2.5f));
            g2d.drawOval(x, y, discSize, discSize);
        } else {
            // Draw empty slot with gradient background
            GradientPaint gp = new GradientPaint(
                x, y, new Color(100, 180, 220),
                x + discSize, y + discSize, new Color(50, 150, 200),
                false
            );
            g2d.setPaint(gp);
            g2d.fillOval(x, y, discSize, discSize);
            
            // Border
            g2d.setColor(new Color(30, 100, 180));
            g2d.setStroke(new java.awt.BasicStroke(1.5f));
            g2d.drawOval(x, y, discSize, discSize);
        }
    }
}