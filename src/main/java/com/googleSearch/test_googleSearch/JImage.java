package com.googleSearch.test_googleSearch;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
* This class is the Image Panel where the image
* is drawn and scaled.
*/
public class JImage extends JPanel {

	private Dimension vieWerDim = null;
	private Image m_image = null;
	private Dimension dim = new Dimension (800,600);
	private AffineTransform at = null;

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

	private double xfactor = 0;
	private double yfactor = 0;
	private boolean fitToViewer = false;

	private boolean viewerEnabled = false;
	private boolean movingImage = false;

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
	public JImage(Image image, double zoomPercentage, Dimension vieWerDim) {
		m_image = image;
		m_zoomPercentage = zoomPercentage / 100;
		this.vieWerDim = vieWerDim;
		init();
	}

	private	void	init()
	{
		this.addMouseListener(
			new MouseListener() {
				
				public void mouseClicked(MouseEvent e) {
					//System.out.println("mouseClicked (" + mouseX + "-" + mouseY + ")");
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
					move(mouseX-e.getX(),mouseY-e.getY());
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
					move(mouseX-e.getX(),mouseY-e.getY());
					//System.out.println("mouseDragged (" + mouseX + "->" + (mouseX-e.getX()) + "-" + mouseY + "->" + (mouseY-e.getY()) + ")");
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
	}
	
	public	void	setTextColor(Color c)
	{
		textColor = c;
	}

	public	void	setBackTextColor(Color c)
	{
		backTextColor = c;
	}

	private	Color	backTextColor = Color.black;
	private	Color	textColor = Color.white;
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

		//		System.out.println("xy: (" + x + "," + y + ")");

		if (movingImage) {
			
			int valMinusXCondition = 0;
			int valMinusYCondition = 0;
			if (init_m_zoom==xfactor) {
				if ((Math.toRadians(angle)/Math.PI) % 1 == 0) {
					valMinusXCondition = (int)(vieWerDim.width/(init_m_zoom/m_zoom));
					valMinusYCondition = (int)((vieWerDim.height/(yfactor/xfactor))/(init_m_zoom/m_zoom));
				} else {
					valMinusXCondition = (int)((vieWerDim.width/(vieWerDim.width/m_image.getHeight(this)/yfactor))/(init_m_zoom/m_zoom));
					valMinusYCondition = (int)((vieWerDim.height/(xfactor/yfactor))/(init_m_zoom/m_zoom)/xfactor);
				}
			} else {
				if ((Math.toRadians(angle)/Math.PI) % 1 == 0) {
					valMinusXCondition = (int)((vieWerDim.width/(xfactor/yfactor))/(init_m_zoom/m_zoom));
					valMinusYCondition = (int)(vieWerDim.height/(init_m_zoom/m_zoom));
				} else {
					valMinusXCondition = (int)((vieWerDim.width/(yfactor/xfactor))/(init_m_zoom/m_zoom)/xfactor);
					valMinusYCondition = (int)(vieWerDim.height/(init_m_zoom/m_zoom)/xfactor);
				}
			}
			
	//		if ((Math.toRadians(angle)/Math.PI) % 1 != 0) {
	//			int aux=valMinusXCondition;
	//			valMinusXCondition=valMinusYCondition;
	//			valMinusYCondition=aux;
	//		}
			
	//		valMinusXCondition=5000;
			if (x<-valMinusXCondition) {
	//			printInfo(x,y,"-x");
				x=-valMinusXCondition+10;
				desplX=(int)(origX-((actualWidth-vieWerDim.width)/2))-x;
			} else if (x>(vieWerDim.width)) {
	//			printInfo(x,y,"+x");
				x=(int)((xfactor*m_image.getWidth(null))-10);
				desplX=(int)(origX-((actualWidth-vieWerDim.width)/2))-x;
			}
	
			if (y<-valMinusYCondition) {
	//			printInfo(x,y,"-y");
				y=-valMinusYCondition+10;
				desplY=(int)(origY-((actualHeight-vieWerDim.height)/2))-y;
			} else if (y>(vieWerDim.height)) {
	//			printInfo(x,y,"+y");
				y=(int)((yfactor*m_image.getHeight(null))-10);
				desplY=(int)(origY-((actualHeight-vieWerDim.height)/2))-y;
			}
			
			movingImage = false;
		}

		at = AffineTransform.getTranslateInstance(x,y);
		at.rotate(Math.toRadians(angle),(actualWidth/2),(actualHeight/2));
		at.scale(m_zoom,m_zoom);
		
		g2D.drawImage(m_image,at,this);

//		printInfoAux(x,y,valMinusXCondition,valMinusYCondition);
		
		g2D.setColor(backTextColor);
		g2D.fillRect(5,5,90,40);
		g2D.setColor(textColor);
		g2D.drawString("[" + m_image.getWidth(this) + "x" + m_image.getHeight(this) + "]",10,20);
        Font font = new Font("Arial", Font.BOLD,  15);
        g2D.setFont(font);
		g2D.setColor(textColor);
		g2D.drawString("Zoom:" + ((int)(m_zoom*10))/10.0,10,40);
//		g2D.setColor(Color.ORANGE);
//		g2D.drawString("["+actualWidth+"x"+actualHeight+"]   z:"+Math.round(m_zoom*10)/10+"   " + angle +"�   ["+origX+"x"+origY+"]", 100, 10);
//		g2D.setColor(Color.YELLOW);
//		g2D.drawString("("+x+","+y+")", 10, 10);
//		g2D.drawString("("+(x+actualWidth)+","+y+")", vieWerDim.width-75, 10);
//		g2D.drawString("("+(x+actualWidth)+","+(y+actualWidth)+")", vieWerDim.width-75, vieWerDim.height-6);
//		g2D.drawString("("+x+","+(y+actualWidth)+")", 10, vieWerDim.height-6);
//		g2D.setColor(Color.RED);
//		g2D.drawRect((actualWidth/2)+((vieWerDim.width-actualWidth)/2)-5, (actualHeight/2)+((vieWerDim.height-actualHeight)/2)-5, 10, 10);
//		g2D.setColor(Color.GREEN);
//		g2D.drawRect(x-5,y-5,10,10);
		
//		System.out.println("\n-----------------------> RectX:" + dim.getWidth() + " - RectY:" + dim.getHeight() + "\n" +
//							"ImgWidth:" + actualWidth + " - ImgHeight:" + actualHeight + "\n" +
//							"ViewerWidth:" + vieWerDim.getWidth() + " - ViewerHeight:" + vieWerDim.getHeight() + "\n");
		
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
	
	private void printInfoAux(int x,int y,int valMinusXCondition,int valMinusYCondition) {
		System.out.println("xy: (" + x + "," + y + "),angle: " + Math.toRadians(angle)/Math.PI +
				") - vieWerDim: (" + vieWerDim.width + "," + vieWerDim.height + 
				") - m_zoom: " + m_zoom + ",xfactor: " + xfactor + ",yfactor: " + yfactor +
				" - valMinusXCondition: " + valMinusXCondition+ ",valMinusYCondition: " + valMinusYCondition +
				" - orig: (" + origX + "," + origY +
				") - vieWerDim: (" + vieWerDim.width + "," + vieWerDim.height + 
				") - actualWidth: " + actualWidth + ",actualHeight: " + actualHeight + 
				" - init_m_zoom:" + init_m_zoom);
	}
	
	public void setActualOrig() {
		origX = (int)(origX-desplX);
		origY = (int)(origY-desplY);
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
//				int count = 0;
//				while (count<3 && (image.getWidth(null)==-1 || image.getHeight(null)==-1)) {
//					Thread.sleep(100);
//					count++;
//				}
				xfactor = 1/(image.getWidth(null)/dim.getWidth());
				yfactor = 1/(image.getHeight(null)/dim.getHeight());
				if (image.getHeight(null)*xfactor <= dim.getHeight()) {
					m_zoom = xfactor;
				} else {
					m_zoom = yfactor;
				}
				
				init_m_zoom = m_zoom;
				
				fitToViewer = true;
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
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

//	private void openImage() {
//		try {
//			if (m_image!=null) {
//				JImageViewer jImageViewer=new JImageViewer("",extractBytes((BufferedImage)m_image),null);
//				JInternalDialog jid = new JInternalDialog("Image viewer",this,jImageViewer,true,true,true,true);
//				jid.show();
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

	private void openImage() {
		try {
			if (m_image!=null) {
				JImageViewer jImageViewer=new JImageViewer("",extractBytes((BufferedImage)m_image),null);
				Container cont = this.getParent();  
				
				while ((cont!=null) && !(cont instanceof JDesktopPane)) {  
					System.out.println(cont);
					cont = cont.getParent();  
				}  
				System.out.println(cont);
				if (cont!=null) { 
				    JDesktopPane desktop = (JDesktopPane)cont;
				    JInternalFrame internalFrame = jImageViewer; //new JInternalFrame("Can Do All", true, true, true, true);
				    desktop.add(internalFrame);
				    internalFrame.setBounds(25, 25, 800, 600);
				    internalFrame.setVisible(true);
				    internalFrame.show();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public byte[] extractBytes (BufferedImage image) throws IOException {

		 // get DataBufferBytes from Raster
		 WritableRaster raster = image .getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return ( data.getData() );
		}
}
