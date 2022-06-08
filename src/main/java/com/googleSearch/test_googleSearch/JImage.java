package com.googleSearch.test_googleSearch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

/**
* This class is the Image Panel where the image
* is drawn and scaled.
*/
public class JImage extends JPanel {

	private static Log logger = Log.getInstance().getLogger();
	private static final long serialVersionUID = 9089628740073531708L;
	
	private Dimension vieWerDim = null;
	private Image m_image = null;
	private Dimension dim = new Dimension (800,600);
	private AffineTransform at = null;
	private float[] dashPattern = { 5, 5 };

	private double m_zoomPercentage = 0.1;
	private double init_m_zoom = 1.0;
	private double m_zoom = 1.0;
	private int actualWidth = 0;
	private int actualHeight = 0;
	public int origX = 0;
	public int origY = 0;
	private int desplX = 0;
	private int desplY = 0;
	private int angle = 0;
	private	int	mouseX = 0;
	private	int	mouseY = 0;
	private	int	lastMouseX = 0;
	private	int	lastMouseY = 0;
	private double angleRotation = 0;

	private double xfactor = 0;
	private double yfactor = 0;
	private boolean fitToViewer = false;
	private boolean enableListeners = true;
	private boolean debug = false;

	private boolean movingImage = false;
	private boolean rotatingImage = false;

	
	/**
	 * Constructor
	 */
	public JImage() {
		super();
		init();
	}
	
	/**
	 * Constructor
	 * 
	 * @param image
	 * @param zoomPercentage
	 */
	public JImage(Image image, double zoomPercentage, Dimension vieWerDim, boolean enableListeners) {
		m_image = image;
		m_zoomPercentage = zoomPercentage / 100;
		this.enableListeners = enableListeners;
		this.vieWerDim = vieWerDim;
		init();
	}

	private	void init()
	{
		if (this.enableListeners ) {
			addListeners();
		}
	}
	
	private void addListeners() {
		this.addMouseListener(
				new MouseListener() {
					
					public void mouseClicked(MouseEvent e) {
						//System.out.println("mouseClicked (" + mouseX + "-" + mouseY + ")");
						if (javax.swing.SwingUtilities.isMiddleMouseButton(e)) {
							logger.debug("x,y [" + e.getX() + "," + e.getY() + "]");
							logger.debug("origX,Y [" + origX + "," + origY + "]");
						}
						if (e.getClickCount() == 2 && m_image!=null) {
							openImage();
						}
					}
					public void mouseExited(MouseEvent e) {
						//System.out.println("mouseExited (" + mouseX + "-" + mouseY + ")");
					}
					public void mouseEntered(MouseEvent e) {
						//System.out.println("mouseEntered (" + mouseX + "-" + mouseY + ")");
					}
					public void mousePressed(MouseEvent e) {
						mouseX = e.getX();
						mouseY = e.getY();
						//System.out.println("mousePressed (" + mouseX + "-" + mouseY + ")");
					}
					public void mouseReleased(MouseEvent e) {
						mouseX = e.getX();
						mouseY = e.getY();
						setActualOrig();
						repaint();
						//System.out.println("---------------------------");
//						move(mouseX-e.getX(),mouseY-e.getY());
						//System.out.println("mouseReleased (" + mouseX + "-" + mouseY + ")");
					}
			});
	
			
			this.addMouseWheelListener(
				new MouseWheelListener() {
					
					public void mouseWheelMoved(MouseWheelEvent e) {
						int notches = e.getWheelRotation();
					       if (notches < 0) {
								zoomIn();
								//System.out.println(getM_zoom() + "(" + "):L(" + e.getX() + "," + e.getY() + ")");
					       } else {
								zoomOut();
								//System.out.println(getM_zoom() + "(" + "):R(" + e.getX() + "," + e.getY() + ")");
					       }
					}
				}
			);
			
			
			this.addMouseMotionListener(
				new MouseMotionListener() {
					public void mouseMoved(MouseEvent e) {
						//System.out.println("mouseMoved");
					}
					
					public void mouseDragged(MouseEvent e) {
						if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
							move(mouseX-e.getX(),mouseY-e.getY());
//							System.out.println("mouseDragged (" + mouseX + "->" + (mouseX-e.getX()) + "-" + mouseY + "->" + (mouseY-e.getY()) + ")");
						} else if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
							int xdiff = e.getX()-lastMouseX;
							int ydiff = e.getY()-lastMouseY;
							lastMouseX = e.getX();
							lastMouseY = e.getY();
							boolean clockwise = (xdiff-ydiff)>0;
							rotate(clockwise);
						}
					}
				}
			);		
	}
	
	/**
	 * Init instance with main values
	 * 
	 * @param image
	 * @param zoomPercentage
	 */
	public void init(Image image, double zoomPercentage, Dimension vieWerDim) {
		setBackground(Color.black);
		m_image = image;
		m_zoomPercentage = zoomPercentage / 100;
		this.vieWerDim = vieWerDim;
		this.dim.setSize(vieWerDim);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean getDebug() {
		return debug;
	}

	public void setTextColor(Color c)
	{
		textColor = c;
	}

	public void setBackTextColor(Color c)
	{
		backTextColor = c;
	}

	private Color backTextColor = Color.black;
	private Color textColor = Color.white;
	/**
	 * This method is overriden to draw the image and scale the graphics
	 * accordingly
	 */
	public void paintComponent(Graphics grp) {
		
		Graphics2D g2D = (Graphics2D) grp;
		// set the background color and fill it
		g2D.setColor(getBackground());
		g2D.fillRect(0,0,(int)dim.getWidth(),(int)dim.getHeight());

		vieWerDim = this.getSize();
		if (m_image == null)
			return;
		
		if (fitToViewer) {
			setImage(m_image);
			fitToViewer = false;
		}


		actualWidth=(int)(m_zoom*m_image.getWidth(this));
		actualHeight=(int)(m_zoom*m_image.getHeight(this));

		int x = (int)(origX-((actualWidth-vieWerDim.width)/2))-desplX;
		int y = (int)(origY-((actualHeight-vieWerDim.height)/2))-desplY;

		if (movingImage) {
//			int xy[] = limitMovement(x,y);
			movingImage = false;
//			x=xy[0];
//			y=xy[1];
		}

		at = AffineTransform.getTranslateInstance(x,y);
		at.rotate(Math.toRadians(angle),(actualWidth/2),(actualHeight/2));
		at.scale(m_zoom,m_zoom);
		
		g2D.drawImage(m_image,at,this);

		if (rotatingImage) {
			rotatingImage = false;
		}

//		printInfoAux(x,y);
		
		g2D.setColor(backTextColor);
		g2D.fillRect(5,5,90,40);
		g2D.setColor(textColor);
		g2D.drawString("[" + actualWidth + "x" + actualHeight + "]",10,25);
        Font font = new Font("Arial", Font.BOLD,  15);
        g2D.setFont(font);
		g2D.setColor(textColor);
		g2D.drawString("Zoom:" + ((int)(m_zoom*10))/10.0,10,40);
		if (debug) {
			int xbase=100;
			int ybase=5;
			g2D.setColor(Color.YELLOW);
			g2D.drawString("("+x+","+y+")", 10, 10);
			g2D.drawString("("+(x+actualWidth)+","+y+")", vieWerDim.width-75, 10);
			g2D.drawString("("+(x+actualWidth)+","+(y+actualHeight)+")", vieWerDim.width-75, vieWerDim.height-6);
			g2D.drawString("("+x+","+(y+actualHeight)+")", 10, vieWerDim.height-6);
			g2D.setColor(Color.BLACK);
			g2D.fillRect(xbase+65,ybase-3,625,17);
			g2D.setColor(Color.ORANGE);
			g2D.drawString("width,height[" + m_image.getWidth(this) + "x" + m_image.getHeight(this) + "]   zoom:"+Math.round(m_zoom*10)/10+"   angle:" + 
							angle +"º  origX,oriY["+origX+"x"+origY+"]    desplX,desplY["+desplX+"x"+desplY+"]", xbase+70, ybase+10);

			int rad = 8;

			int xlim1 = x;
			int ylim1 = y;
			int xlim2 = x+actualWidth;
			int ylim2 = y+actualHeight;

			int centerX=origX+(actualWidth/2)+((vieWerDim.width-actualWidth)/2)-desplX;
			int centerY=origY+(actualHeight/2)+((vieWerDim.height-actualHeight)/2)-desplY;

			g2D.setColor(Color.WHITE);
			g2D.setStroke(new BasicStroke(2));
			g2D.drawOval(centerX,centerY,rad*2,rad*2);

			AffineTransform atVerts = new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
			atVerts.rotate(Math.toRadians(angle),centerX,centerY);
			Point2D a1 = new Point2D.Double(xlim1,ylim1);
			Point2D a2 = new Point2D.Double(xlim2,ylim1);
			Point2D a3 = new Point2D.Double(xlim1,ylim2);
			Point2D a4 = new Point2D.Double(xlim2,ylim2);

			g2D.setTransform(atVerts);
			g2D.setColor(Color.RED);
			g2D.drawString("a1",(int)a1.getX(),(int)a1.getY());
			g2D.drawOval((int)a1.getX(),(int)a1.getY(),1,1);
			g2D.setColor(Color.BLUE);
			g2D.drawString("a2",(int)a2.getX(),(int)a2.getY());
			g2D.drawOval((int)a2.getX(),(int)a2.getY(),1,1);
			g2D.setColor(Color.GREEN);
			g2D.drawString("a3",(int)a3.getX(),(int)a3.getY());
			g2D.drawOval((int)a3.getX(),(int)a3.getY(),1,1);
			g2D.setColor(Color.ORANGE);
			g2D.drawString("a4",(int)a4.getX(),(int)a4.getY());
			g2D.drawOval((int)a4.getX(),(int)a4.getY(),1,1);

			
			at = new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
			g2D.setTransform(at);
			Point2D a1t = atVerts.transform(a1, new Point2D.Double());
			Point2D a2t = atVerts.transform(a2, new Point2D.Double());
			Point2D a3t = atVerts.transform(a3, new Point2D.Double());
			Point2D a4t = atVerts.transform(a4, new Point2D.Double());
			int[] axt = {(int)a1t.getX(),(int)a2t.getX(),(int)a3t.getX(),(int)a4t.getX()};
			int[] ayt = {(int)a1t.getY(),(int)a2t.getY(),(int)a3t.getY(),(int)a4t.getY()};
			Util.quickSort(axt, 0, axt.length-1);
			Util.quickSort(ayt, 0, ayt.length-1);
			g2D.setColor(Color.GRAY);
			g2D.drawString("v1",axt[0],ayt[0]);
			g2D.drawOval(axt[0],ayt[0],1,1);
			g2D.drawString("v2",axt[3],ayt[3]);
			g2D.drawOval(axt[3],ayt[3],1,1);
			g2D.setStroke(new BasicStroke((float)1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10,dashPattern,0));
			g2D.drawRect(axt[0],ayt[0],axt[3]-axt[0],ayt[3]-ayt[0]);
			g2D.setColor(Color.BLACK);
			g2D.fillRect(10,100,115,70);			
			g2D.setColor(Color.GRAY);
			g2D.drawRect(5,100,120,70);
			g2D.setColor(Color.WHITE);
			g2D.drawString("[" + (axt[3]-axt[0]) + "x" + (ayt[3]-ayt[0]) + "]",10,120);
			g2D.drawString("axMin[" + axt[0] + "," + ayt[0] + "]",10,140);
			g2D.drawString("axMax[" + axt[3] + "," + ayt[3] + "]",10,160);

		}
		
//		System.out.println("\n-----------------------> RectX:" + dim.getWidth() + " - RectY:" + dim.getHeight() + "\n" +
//							"ImgWidth:" + actualWidth + " - ImgHeight:" + actualHeight + "\n" +
//							"ViewerWidth:" + vieWerDim.getWidth() + " - ViewerHeight:" + vieWerDim.getHeight() + "\n");
	}
	
	private int[] limitMovement(int x, int y) {
		int valMinusXCondition = 0;
		int valMinusYCondition = 0;
		if (init_m_zoom == xfactor) {
			if ((Math.toRadians(angle) / Math.PI) % 1 == 0) {
				valMinusXCondition = (int) (vieWerDim.width / (init_m_zoom / m_zoom));
				valMinusYCondition = (int) ((vieWerDim.height / (yfactor / xfactor)) / (init_m_zoom / m_zoom));
			} else {
				valMinusXCondition = (int) ((vieWerDim.width / (vieWerDim.width / m_image.getHeight(this) / yfactor)) / (init_m_zoom / m_zoom));
				valMinusYCondition = (int) ((vieWerDim.height / (xfactor / yfactor)) / (init_m_zoom / m_zoom) / xfactor);
			}
		} else {
			if ((Math.toRadians(angle) / Math.PI) % 1 == 0) {
				valMinusXCondition = (int) ((vieWerDim.width / (xfactor / yfactor)) / (init_m_zoom / m_zoom));
				valMinusYCondition = (int) (vieWerDim.height / (init_m_zoom / m_zoom));
			} else {
				valMinusXCondition = (int) ((vieWerDim.width / (yfactor / xfactor)) / (init_m_zoom / m_zoom) / xfactor);
				valMinusYCondition = (int) (vieWerDim.height / (init_m_zoom / m_zoom) / xfactor);
			}
		}

		// if ((Math.toRadians(angle)/Math.PI) % 1 != 0) {
		// int aux=valMinusXCondition;
		// valMinusXCondition=valMinusYCondition;
		// valMinusYCondition=aux;
		// }

		// valMinusXCondition = 5000;
		if (x < -valMinusXCondition) {
//			printInfo(x, y, "-x");
			x = -valMinusXCondition + 10;
			desplX = (int) (origX - ((actualWidth - vieWerDim.width) / 2)) - x;
		} else if (x > (vieWerDim.width)) {
//			printInfo(x, y, "+x");
			x = (int) ((xfactor * m_image.getWidth(null)) - 10);
			desplX = (int) (origX - ((actualWidth - vieWerDim.width) / 2)) - x;
		}

		if (y < -valMinusYCondition) {
//			printInfo(x, y, "-y");
			y = -valMinusYCondition + 10;
			desplY = (int) (origY - ((actualHeight - vieWerDim.height) / 2)) - y;
		} else if (y > (vieWerDim.height)) {
//			printInfo(x, y, "+y");
			y = (int) ((yfactor * m_image.getHeight(null)) - 10);
			desplY = (int) (origY - ((actualHeight - vieWerDim.height) / 2)) - y;
		}

		int[] vals = { x, y };
		return vals;
	}
	
	private void repaintGraphics(Graphics grp,int actualWidth, int actualHeight, int xdespl,int ydespl) {
		
		Graphics2D g2D = (Graphics2D) grp;
		// set the background color and fill it
		g2D.setColor(getBackground());
		g2D.fillRect(0,0,(int)dim.getWidth(),(int)dim.getHeight());

		at = AffineTransform.getTranslateInstance(xdespl,ydespl);
		at.rotate(Math.toRadians(angle),(actualWidth/2),(actualHeight/2));
		at.scale(m_zoom,m_zoom);
		
		g2D.drawImage(m_image,at,this);
	}

	private void printInfo(int x,int y, String str) {
		System.out.println("---------------> Fuera " + str);
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("xy: (" + x + "," + y + "),angle: " + Math.toRadians(angle));
		System.out.println("m_zoom: " + m_zoom);
		System.out.println("xfactor: " + xfactor + "yfactor:" + yfactor);
		System.out.println("m_image: (" + m_image.getWidth(null) + "," + m_image.getHeight(null) + ")");
		System.out.println("vieWerDim: (" + vieWerDim.width + "," + vieWerDim.height + ")");
		System.out.println("desplX: " + desplX + ",desplY:" + desplY);
		System.out.println("atTranslate: (" + (at!=null?at.getTranslateX():"-")+ "," + (at!=null?at.getTranslateY():"-") + ")");
		System.out.println("--------------------------------------------------------------------------------");
	}
	
	private void printInfoAux(int x,int y) {; // ,int valMinusXCondition,int valMinusYCondition) {
		System.out.println("xy: (" + x + "," + y + "),angle: " + Math.toRadians(angle)/Math.PI +
				") - vieWerDim: (" + vieWerDim.width + "," + vieWerDim.height + 
				") - m_zoom: " + m_zoom + ",xfactor: " + xfactor + ",yfactor: " + yfactor +
//				" - valMinusXCondition: " + valMinusXCondition+ ",valMinusYCondition: " + valMinusYCondition +
				" - orig: (" + origX + "," + origY +
				") - vieWerDim: (" + vieWerDim.width + "," + vieWerDim.height + 
				") - actualWidth: " + actualWidth + ",actualHeight: " + actualHeight + 
				" - init_m_zoom:" + init_m_zoom);
	}
	
	public void setActualOrig() {
		origX = (int)(origX-desplX);
		origY = (int)(origY-desplY);
		desplX=0;
		desplY=0;
//		repaint();
	}

	public void setData(byte[] data)
	{
		m_image = null;
		
		if (data != null)
		{
			try {
				m_image = new ImageIcon(data).getImage();
//				Toolkit tk = Toolkit.getDefaultToolkit();
//				m_image = tk.createImage(data);
				setImage(m_image);
//				m_zoom = 0.2;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		repaint();
	}
	
	public void setImage(Image image) {
		this.m_image = image;
		if (image != null) {
			try {
				dim = getSize();
//				System.out.println("X="+image.getWidth(null) + "-Y=" +image.getHeight(null));
				int count = 0;
				while (count<20 && (image.getWidth(null)==-1 || image.getHeight(null)==-1)) {
					Thread.sleep(100);
					count++;
				}
				xfactor = 1/(image.getWidth(null)/dim.getWidth());
				yfactor = 1/(image.getHeight(null)/dim.getHeight());
				if (image.getHeight(null)*xfactor <= dim.getHeight()) {
					m_zoom = xfactor;
				} else {
					m_zoom = yfactor;
				}
				
				init_m_zoom = m_zoom;
//				init_m_zoom = 1;
				
				fitToViewer = true;
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public Image getImage() {
		return m_image;
	}

	public BufferedImage getRenderedImage() {

//		vieWerDim = this.getSize();
//		actualWidth=(int)(m_zoom*m_image.getWidth(this));
//		actualHeight=(int)(m_zoom*m_image.getHeight(this));

		int x = (int)(origX-((actualWidth-vieWerDim.width)/2))-desplX;
		int y = (int)(origY-((actualHeight-vieWerDim.height)/2))-desplY;

		int xlim1 = x;
		int ylim1 = y;
		int xlim2 = x+actualWidth;
		int ylim2 = y+actualHeight;

		int centerX=origX+(actualWidth/2)+((vieWerDim.width-actualWidth)/2)-desplX;
		int centerY=origY+(actualHeight/2)+((vieWerDim.height-actualHeight)/2)-desplY;

		AffineTransform atVerts = new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
		atVerts.rotate(Math.toRadians(angle),centerX,centerY);
		Point2D a1 = new Point2D.Double(xlim1,ylim1);
		Point2D a2 = new Point2D.Double(xlim2,ylim1);
		Point2D a3 = new Point2D.Double(xlim1,ylim2);
		Point2D a4 = new Point2D.Double(xlim2,ylim2);
		
		Point2D a1t = atVerts.transform(a1, new Point2D.Double());
		Point2D a2t = atVerts.transform(a2, new Point2D.Double());
		Point2D a3t = atVerts.transform(a3, new Point2D.Double());
		Point2D a4t = atVerts.transform(a4, new Point2D.Double());
		int[] axt = {(int)a1t.getX(),(int)a2t.getX(),(int)a3t.getX(),(int)a4t.getX()};
		int[] ayt = {(int)a1t.getY(),(int)a2t.getY(),(int)a3t.getY(),(int)a4t.getY()};
		Util.quickSort(axt, 0, axt.length-1);
		Util.quickSort(ayt, 0, ayt.length-1);

		BufferedImage bi = null;
		boolean debugBak = getDebug();
		setDebug(false);
		repaint();
		try {
			if (axt[0]<0) {
				axt[0]=0;
			}
			if (ayt[0]<0) {
				ayt[0]=0;
			}
			if (axt[3]>vieWerDim.width) {
				axt[3]=vieWerDim.width;
			}
			if (ayt[3]>vieWerDim.height) {
				ayt[3]=vieWerDim.height;
			}
			
			Rectangle region = new Rectangle(axt[0],ayt[0],axt[3]-axt[0],ayt[3]-ayt[0]);
			logger.debug("axt[0]="+axt[0]+",ayt[0]="+ayt[0]+",axt[3]-axt[0]="+(axt[3]-axt[0])+",ayt[3]-ayt[0]="+(ayt[3]-ayt[0]));
			bi = ScreenImage.createImage(this,region);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDebug(debugBak);
		repaint();
		
		return bi;
	}


	/**
	 * This method is overriden to return the preferred size which will be
	 * the width and height of the image plus the zoomed width width and
	 * height. while zooming out the zoomed width and height is negative
	 */
	public Dimension getPreferredSize() {
		return dim;
	}

	public void setPreferredSize(Dimension dim) {
		this.dim=dim;
	}

	/**
	 * Sets the new zoomed percentage
	 * 
	 * @param zoomPercentage
	 */
	public void setZoomPercentage(int zoomPercentage) {
		m_zoomPercentage = ((double) zoomPercentage) / 100;
	}
	
	/**
	 * This method set the image to the original size by setting the zoom
	 * factor to 1. i.e. 100%
	 */
	public void originalSize() {
		m_zoom = 1;
	}

	
	/**
	 * Move image
	 */
	public void move(int desplX, int desplY) {
		movingImage=true;
		this.desplX = desplX;
		this.desplY = desplY;
		repaint();
	}
	
	/**
	 * Rotate image
	 */
	public void rotate(boolean clockwise) {
		rotatingImage=true;
		desplX=0;
		desplY=0;
		angleRotation= 2 * (clockwise?1:-1);
//		System.out.println("angleRotation="+angleRotation + ",clockwise=" + clockwise);
		angle+=angleRotation;
		angleRotation=0;
		repaint();
		rotatingImage=false;
	}
		
	/**
	 * This method increments the zoom factor with the zoom percentage, to
	 * create the zoom in effect
	 */
	public void zoomIn() {
		desplX=0;
		desplY=0;
		if ((m_zoom + m_zoomPercentage)>10) {
			return;
		}
		m_zoom += m_zoomPercentage;
		actualWidth=(int)(m_zoom*m_image.getWidth(this));
		actualHeight=(int)(m_zoom*m_image.getHeight(this));
		repaint();
	}
	
	/**
	 * This method decrements the zoom factor with the zoom percentage, to
	 * create the zoom out effect
	 */
	public void zoomOut() {
		desplX=0;
		desplY=0;
		if ((m_zoom - m_zoomPercentage)<0.1) {
			return;
		}
		m_zoom -= m_zoomPercentage;
		actualWidth=(int)(m_zoom*m_image.getWidth(this));
		actualHeight=(int)(m_zoom*m_image.getHeight(this));
		repaint();
	}
	
	/**
	 * This method returns the currently zoomed percentage
	 * 
	 * @return
	 */
	public double getZoomedTo() {
		return m_zoom * 100;
	}

	public double getM_zoom() {
		return m_zoom;
	}

	public void setM_zoom(double m_zoom) {
		this.m_zoom = m_zoom;
	}

	public void reset() {
		m_zoom = init_m_zoom;
		desplX = 0;
		desplY = 0;
		origX = 0;
		origY = 0;
		angle = 0;
		setImage(m_image);
		repaint();
	}
	
	public void rotateLeft() {
		angle-=90;
		repaint();
	}
	
	public void rotateRight() {
		angle+=90;
		repaint();
	}

	public void fitToViewer() {
		fitToViewer=true;
	}
	
	public void clear() {
		setData(null);
	}

	private void openImage() {
		openImage(null,null);
	}
	
	public void openImage(Image image,Dimension viewerImageSize) {
		try {
			JImageViewer jImageViewer = null;
			Container cont = this.getParent();  
			while ((cont!=null) && !(cont instanceof JDesktopPane)) {  
//				System.out.println(cont);
				cont = cont.getParent();  
			}  
//			System.out.println(cont);

			if (image!=null) {
				jImageViewer=new JImageViewer("",null,image);
			} else if (m_image!=null) {
				jImageViewer=new JImageViewer("",null,m_image);
			}
				
			if (cont!=null) { 
				
				if (viewerImageSize==null) {
					viewerImageSize = dim;
				}
				
			    JDesktopPane desktop = (JDesktopPane)cont;
			    JInternalFrame internalFrame = jImageViewer; //new JInternalFrame("Can Do All", true, true, true, true);
			    desktop.add(internalFrame);
			    internalFrame.setBounds(25, 25, viewerImageSize.width, viewerImageSize.height);
			    internalFrame.setVisible(true);
			    internalFrame.show();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

}
