package com.googleSearch.test_googleSearch;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class JImageViewer extends JInternalFrame {

	private static Log logger = Log.getInstance().getLogger();
	private static final long serialVersionUID = 9089148740073055708L;
	
	// JPanels
	private JPanel jpImages = null;
	
	private String title = null;
	private File currentDir = null;

	// Visor para imagenes
	public JImage jImage = null;
	public JPanel jpImageButtons = null;
	public JButton jbtSave = null;
	public JButton jbtRotateLeft = null;
	public JButton jbtRotateRight = null;
	public JButton jbtReset = null;
	public JButton jbtClose = null;
	private Image image = null;

	
	private Dimension viewerSizeImage = new Dimension(1200,900);
	private Dimension viewerDimd = null;

	
	public JImageViewer (String title,File currentDir, Image m_image) {
		this.title = title;
		this.currentDir = currentDir;
		try {
			image=m_image;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initJImageViewerDialog();
	}

	private void initJImageViewerDialog() {	
		
		jpImages = new JPanel();
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		jpImages.setLayout(new GridBagLayout());
		jImage = new JImage();
		SwingUtilities.setGridBagConstraints(gbc,0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpImages.add(jImage,gbc);

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
		
		this.setLayout(new GridBagLayout());
		SwingUtilities.setGridBagConstraints(gbc,0,0,1,10,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		this.add(jpImages,gbc);

		jImage.setVisible(false);
		jpImageButtons.setVisible(false);

		if (image!=null) {

			jbtSave.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						File currentDir = new File("E:\\java\\projects\\test-googleSearch\\testFolder\\pics\\"); //prueba_"+(int)(Math.random() * 10000)+".jpg");
						JFileChooser fc = new JFileChooser(currentDir);
						fc.setDialogType(JFileChooser.SAVE_DIALOG);
			            fc.setSelectedFile(new File("test2.jpg"));
			            fc.setFileFilter(new FileNameExtensionFilter("JPG file","jpg"));
			            fc.setAcceptAllFileFilterUsed(true);
			            int returnVal = fc.showSaveDialog(JImageViewer.this);
			            if (returnVal == JFileChooser.APPROVE_OPTION) {
			            	String filename = fc.getSelectedFile().toString();
			            	if (!filename.endsWith(".jpg"))
			            	        filename += ".jpg";
			                File file = new File(filename);
			                currentDir = fc.getCurrentDirectory();
			                logger.info("Saving: '" + file.getName()+ "'.");
			                BufferedOutputStream bof = null;
			        		try {
				        		bof = new BufferedOutputStream(new FileOutputStream(file));
			        			ImageIO.write(jImage.getRenderedImage(), "jpg", bof);
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
//			            } else {
//			                System.out.println("Save command cancelled by user.");
			            }
					}
				}
			);
	
	
			jbtRotateLeft.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jImage.rotateLeft();
					}
				}
			);
	
			jbtRotateRight.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jImage.rotateRight();
					}
				}
			);
			
			jbtReset.addActionListener (
				new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jImage.reset();
					}
				}
			);
	
		}

		jbtClose.addActionListener (
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);

//		jbtRotateLeft.setIcon(iconRotateLeft);
//		jbtRotateRight.setIcon(iconRotateRight);


		viewerDimd = viewerSizeImage;
		jImage.init(image,10,viewerSizeImage);
		jImage.setSize(viewerSizeImage);	
//		imagePanel.setData(data);

		jImage.setVisible(true);
			jpImageButtons.setVisible(true);
		
		this.setPreferredSize(viewerDimd);
		
//		// NO TOCAR: garantiza que los PDFs se rendericen bien al cargar inicialmente
//		this.setSize(viewerDimd);

		jbtClose.requestFocus();
		
		jImage.setDebug(true);
	}

	public void setViewerSizeImage(Dimension viewerSizeImage) {
		this.viewerSizeImage = viewerSizeImage;
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
