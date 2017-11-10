package com.googleSearch.test_googleSearch;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;


public class JImageViewer extends JInternalFrame { //extends JPanel  {

	// JPanels
	private JPanel jpImages = null;
	
	private String title = null;
	private byte[] data = null;
	private File currentDir = null;

	// Visor para imagenes
	public JImage imagePanel = null;
	public JPanel jpImageButtons = null;
	public JButton jbtSave = null;
	public JButton jbtRotateLeft = null;
	public JButton jbtRotateRight = null;
	public JButton jbtReset = null;
	public JButton jbtClose = null;
	private Image image = null;

	
	private static ImageIcon iconRotateLeft = null;
	private static ImageIcon iconRotateRight = null;

	private Dimension viewerSizeImage = new Dimension(800,600);
	private Dimension viewerDimd = null;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	

	public JImageViewer (String title,byte[] data, File currentDir) {
//		super(title,null,true,true,true,true);
		this.title = title;
		this.data = data;
		this.currentDir = currentDir;
		
		loadIcons();
		
		initJImageViewerDialog();
	}

	private void loadIcons() {
//		if (iconRotateLeft==null) {
//			Image imageLock = jPanelBase.getJTeseoClient().getImage("rotateLeft.png");
//			if (imageLock!=null)
//				iconRotateLeft = new ImageIcon(imageLock.getScaledInstance(16,16,Image.SCALE_SMOOTH));
//		}
//		if (iconRotateRight==null) {
//			Image imageUnlock = jPanelBase.getJTeseoClient().getImage("rotateRight.png");
//			if (imageUnlock!=null)
//				iconRotateRight = new ImageIcon(imageUnlock.getScaledInstance(16,16,Image.SCALE_SMOOTH));
//		}
	}
	
	private void initJImageViewerDialog() {	
		
//		try {
//			jpImages = PaneBuilder.buildJPanel("gestphotos/jpImageViewer", jPanelBase, this, this, null);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}

		JPanel jpImages = new JPanel();
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		jpImages.setLayout(new GridBagLayout());
		imagePanel = new JImage();
		SwingUtilities.setGridBagConstraints(gbc,0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImages.add(imagePanel,gbc);

		jpImageButtons = new JPanel();
		SwingUtilities.setGridBagConstraints(gbc,0,1,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImages.add(jpImageButtons,gbc);

		
		jbtSave = new JButton("Grabar");
		SwingUtilities.setGridBagConstraints(gbc,0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImageButtons.add(jbtSave, gbc);

		jbtRotateRight = new JButton("R->");
		SwingUtilities.setGridBagConstraints(gbc,1,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImageButtons.add(jbtRotateRight, gbc);
		
		jbtRotateLeft = new JButton("<-R");
		SwingUtilities.setGridBagConstraints(gbc,2,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImageButtons.add(jbtRotateLeft, gbc);

		jbtReset = new JButton("Reiniciar");
		SwingUtilities.setGridBagConstraints(gbc,3,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImageButtons.add(jbtReset, gbc);
		
		jbtClose = new JButton("Cerrar");
		SwingUtilities.setGridBagConstraints(gbc,4,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImageButtons.add(jbtClose, gbc);
		
//		final Container cont = getContentPane();
//		cont.setLayout(new GridBagLayout());
//		SwingUtilities.setGridBagConstraints(gbc,0,0,1,10,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
//		cont.add(jpImages, gbc);
		
		this.setLayout(new GridBagLayout());
		SwingUtilities.setGridBagConstraints(gbc,0,0,1,10,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		this.add(jpImages,gbc);

		imagePanel.setVisible(false);
		jpImageButtons.setVisible(false);
		if (data!=null) {

			jbtSave.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						JFileChooser fc = new JFileChooser(currentDir);
			            String imgType = identifyImgFileData(data);
			            fc.setAcceptAllFileFilterUsed(true);
			            if (imgType!=null) {
				            fc.setFileFilter(new CustomFileFilter(new String[]{"."+ imgType},imgType));
			            }
			            File f = new File(title);
			            fc.setSelectedFile(f);
			            int returnVal = fc.showSaveDialog(JImageViewer.this);
			            if (returnVal == JFileChooser.APPROVE_OPTION) {
			                File file = fc.getSelectedFile();
			                currentDir = fc.getCurrentDirectory();
	//		            	((JPanelAnalysisFotos)parent).setCurrentDir(currentDir);
			                System.out.println("Saving: " + file.getName() + ".");
	
			        		BufferedOutputStream bof = null;
			        		try {
			        			bof = new BufferedOutputStream(new FileOutputStream(file));
			        			bof.write(data);
	
			        		} catch (Exception e) {
			        			System.out.println("Error saving the file '"+ file.getName() + "'");
			        			showError("Error saving the file '\"+ file.getName() + \"'\"");
			        		} catch (Throwable e) {
			        			e.printStackTrace();
			        			System.out.println("Error(Throwable) saving the file '"+ file.getName() + "'");
			        			showError("Error(Throwable) saving the file '"+ file.getName() + "'");
			        		} finally {
			        			try {
			        				bof.close();
			        			}catch (Exception e) {
			        				System.out.println("Error closing file '"+ file.getName() + "'");
			        			}catch (Throwable e) {
			        				System.out.println("Error(Throwable) closing file '"+ file.getName() + "'");
			        			}
			        		}
			            } else {
			                System.out.println("Save command cancelled by user.");
			            }
					}
				}
			);
	
	
			jbtRotateLeft.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						imagePanel.rotateLeft();
					}
				}
			);
	
			jbtRotateRight.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						imagePanel.rotateRight();
					}
				}
			);
			
			jbtReset.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						imagePanel.reset();
					}
				}
			);
	
		}

		jbtClose.addActionListener (
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					dispose();
				}
			}
		);

//		jbtRotateLeft.setIcon(iconRotateLeft);
//		jbtRotateRight.setIcon(iconRotateRight);


		viewerDimd = viewerSizeImage;
		imagePanel.init(image,10,viewerSizeImage);
		imagePanel.setSize(viewerSizeImage);	
		imagePanel.setData(data);

		imagePanel.setVisible(true);
		jpImageButtons.setVisible(true);
		
		this.setPreferredSize(viewerDimd);
		
//		// NO TOCAR: garantiza que los PDFs se rendericen bien al cargar inicialmente
//		this.setSize(viewerDimd);
		
		jbtClose.requestFocus();
	}
	
	////// Images methods /////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private byte[] encodeMD5(byte[] bytes) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] md5 = md.digest(bytes);
	    return md5;
	}
	
	private String convertToHex(byte[] data) {
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < data.length; i++) {
	        int halfbyte = (data[i] >>> 4) & 0x0F;
	        int two_halfs = 0;
	        do {
	            if ((0 <= halfbyte) && (halfbyte <= 9))
	                buf.append((char) ('0' + halfbyte));
	            else
	                buf.append((char) ('a' + (halfbyte - 10)));
	            halfbyte = data[i] & 0x0F;
	        } while(two_halfs++ < 1);
	    }
	    return buf.toString();
	}

	private int unsignedByte(byte b) {
		return (b & 0xFF);
	}
	
	private String identifyImgFileData (byte[] bytes)
	{
		if(bytes!=null)
		{
			if ( (unsignedByte(bytes[0])==0xFF && unsignedByte(bytes[1])==0xD8 && unsignedByte(bytes[2])==0xFF) )
			{
				// FF D8 FF DB								ÿØÿÛ			JPEG raw
				// FF D8 FF E0 nn nn 4A 46 49 46 00 01		ÿØÿà ..JF IF..	JPEG in JFIF format
				// FF D8 FF E1 nn nn 45 78 69 66 00 00		ÿØÿá ..Ex if..	JPEG in Exif format
				// FF D8 FF FE nn nn 45 78 69 66 00 00		ÿØÿá ..Ex if..	JPEG in Exif format (?)
				return "jpg";
			}
			if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') 
			{
				return "gif";
			}
			if ( (bytes[0] == 'I' && bytes[1] == 'I' && bytes[2] == '*') || (bytes[0] == 'M' && bytes[1] == 'M' && bytes[2] == '*') )
			{
				return "tiff";
			}
			if (bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F')
			{
				return "pdf";
			}
			if (unsignedByte(bytes[0])==0x89 && unsignedByte(bytes[1])==0x50 && unsignedByte(bytes[2])==0x4E && unsignedByte(bytes[3])==0x47 &&
					unsignedByte(bytes[4])==0x0D && unsignedByte(bytes[5])==0x0A && unsignedByte(bytes[6])==0x1A && unsignedByte(bytes[7])==0x0A)
			{
				// 89 50 4E 47 0D 0A 1A 0A					.PNG....		PNG (Portable Network Graphics format)
				return "png";
			}
		}
		return null;
	}
	
	private class CustomFileFilter extends FileFilter {

		private String[] okFileExtensions=new String[] {};
		private String description=null;;

		public CustomFileFilter(String[] okFileExtensions, String description) {
			super();
			init(okFileExtensions,description);
		}

		public CustomFileFilter(String[] okFileExtensions) {
			super();
			init(okFileExtensions,null);
		}

		private void init(String[] okFileExtensions, String description) {
			this.okFileExtensions = okFileExtensions;
			this.description = description;
		}
		
		public boolean accept(File file) {
			for (String extension:okFileExtensions) {
				if (file.getName().toLowerCase().endsWith(extension) || file.isDirectory()) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return description;
		}
	}
	
	public void showError(String error)
	{
		if (error != null)
		{
			JOptionPane.showConfirmDialog(this,error,"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	

}
