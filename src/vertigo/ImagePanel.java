/*
 * Copyright 2015
 *  http://wazza.co.ke
 * 9:42:50 PM  : Jul 22, 2015
 */
package vertigo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author kelli
 */
public class ImagePanel extends JPanel {

    private BufferedImage bufferedImage;
    private Point pointStart = new Point(0, 0);
    private Point pointEnd = new Point(0, 0);
    private JPanel clippingPanel;
    private Dimension scaleSize;
    private BufferedImage clippedImg;
    private AffineTransform transform =  new AffineTransform();

    /* Constructor for custom JPanel */
    public ImagePanel() {
        super();

        //default dimension for painting empty panel
        scaleSize = new Dimension(464, 264);
        //add listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pointStart = new Point(e.getPoint());
                pointEnd = pointStart;
                Logger.getLogger(ImagePanel.class.getName()).
                        log(Level.INFO, "MousePress Event at {0}", e.getPoint());
//                repaintClipPanel();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                pointEnd = e.getPoint();
                Logger.getLogger(ImagePanel.class.getName()).
                        log(Level.INFO, "MouseRelease Event at {0}", e.getPoint());
//                repaintClipPanel();
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Logger.getLogger(ImagePanel.class.getName()).
                        log(Level.INFO, "MouseDrag Event at {0}", e.getPoint());
                pointEnd = e.getPoint();
//                repaintClipPanel();
                repaint();
            }
        });
    }

    /**
     * Paint panel with image content from selected file
     *
     * @param f selected bufferImage file
     * @param dimension resize image to dimension
     */
    public ImagePanel(File f, Dimension dimension) {

        //set dimension to use for painting the BufferImage
        this.scaleSize = dimension;
        try {
            Logger.getLogger(ImagePanel.class.getName()).
                    log(Level.INFO, "Reading file...{0} ", f.getCanonicalFile());
            
            //get transform if image is rotated
            transform = checkRotationTransform(f);
            System.out.println("Transform is: "+transform.getTranslateX());
            bufferedImage = ImageIO.read(f);
        } catch (IOException ex) {
            Logger.getLogger(ImagePanel.class.getName()).
                    log(Level.SEVERE, "Error reading image file: ", ex.getMessage());
        }

        //add listeners
        addMouseListener(new MouseAdapter() {
            @Override

            public void mousePressed(MouseEvent e) {
                /*get a reference to pnTarget graphics object 
                 set the canvas to the bufferimage, set clip and paint*/
                pointStart = new Point(e.getPoint());
                clippingPanel = getClipPanel(e);
                pointEnd = pointStart;
                Logger.getLogger(ImagePanel.class.getName()).
                        log(Level.INFO, "MousePress Event at {0}", e.getPoint());
                repaint();
                repaintClipPanel();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Logger.getLogger(ImagePanel.class.getName()).
                        log(Level.INFO, "MouseRelease Event at {0}", e.getPoint());
                pointEnd = e.getPoint();
                repaint();
                repaintClipPanel();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Logger.getLogger(ImagePanel.class.getName()).
                        log(Level.INFO, "MouseDrag Event at {0}", e.getPoint());
                pointEnd = e.getPoint();
                repaint();
                repaintClipPanel();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        drawImage(g);
        drawMarquee(g);
        drawClippedImg(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dimension = new Dimension(464, 264);
        return dimension;
    }

    /* ========== Utility methods =============== */
    /* Get the right-side panel for patinting clipped image on  */
    public JPanel getClipPanel(MouseEvent event) {

        Component c = SwingUtilities.getRoot(this);
        Component splitPane = SwingUtilities.getDeepestComponentAt(c, 0, 0);
        JPanel panel = null;

        if (splitPane instanceof JSplitPane) {
            Component x = splitPane.getComponentAt(event.getX() + 400, event.getY());
            panel = (JPanel) x;
        }
        return panel;
    }

    /* Repaint panel with clip image */
    public void repaintClipPanel() {
        if (clippingPanel != null) {
            clippingPanel.repaint();
        }
    }

    /* Marquee properties */
    public BasicStroke setStrokeProperties() {
        float dash1[] = {10.0f};
        BasicStroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);
        return dashed;
    }

    /* Draw BufferImage and scale to dimensions */
    private void drawImage(Graphics g) {
//        Graphics2D g2d = (Graphics2D)g;
//       g2d.rotate(Math.toRadians(90));
        g.drawImage(bufferedImage, 0, 0, scaleSize.width, scaleSize.height, null);
//        g2d.drawImage(bufferedImage, transform, this);
    }

    /**
     * Draw the marquee
     */
    private void drawMarquee(Graphics g) {
        Rectangle marqueeSelect = new Rectangle(pointStart.x, pointStart.y,
                pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(setStrokeProperties());
        g2.setPaint(new Color(255, 100, 100, 100));
        g2.fill(marqueeSelect);
        g2.draw(marqueeSelect);
    }

    /**
     * Draw the clipped image from BufferImage
     */
    private void drawClippedImg(Graphics g) {
        if (clippingPanel != null) {
            clippedImg = bufferedImage.
                    getSubimage(pointStart.x, pointEnd.y, pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
//            Graphics2D g2d = (Graphics2D) g.create();
            Rectangle rect2 = new Rectangle(pointStart.x, pointEnd.y,
                    pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
//            clippingPanel.getGraphics().setClip(rect2);
            clippingPanel.getGraphics().drawImage(clippedImg,
                    rect2.x, rect2.y, rect2.width, rect2.height, null);
        }
    }

    /* Determine image orientation and rotate if needed. */
    
    private AffineTransform checkRotationTransform(File image){
        ImageProperties properties = new ImageProperties();
        int orientation = properties.getImageOrientation(image);
        System.out.println("got orientation: "+orientation);
        
        AffineTransform tx = new AffineTransform();
        switch(orientation){
            case 1:
                //image is OK
                break;
            case 3: //bottom up?
                tx.rotate(Math.toRadians(180));
                break;
            case 6: //right side ?
                tx.rotate(Math.toRadians(90));
                break;
            case 8: //left side ?
                tx.rotate(Math.toRadians(-90));
                break;
        }
        return tx;
    }
                
                
                
        }
   
