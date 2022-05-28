package com.googleSearch.test_googleSearch;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

public class JImagePanel extends JPanel {

	private static Log logger = Log.getInstance().getLogger();
	private static final long serialVersionUID = 9089648740073530708L;
	private static final String DEFAULT_SAVE_FORMAT = "jpg";

	private FindResult findResult = null;

	private File currentDir = null;
	private String downloadPath = null;
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

	
	private JImage jImage = null;
	private JLabel jlInfo = null;
	private JButton jbtSave = null;
	
	
    public JImagePanel(FindResult findResult, double zoomPercentage, Dimension vieWerDim) {
    	this.findResult=findResult;
    	jImage = new JImage(findResult.getThumbnailImage(),zoomPercentage,vieWerDim,false);
    	buildPanel();
    	init();
    }

	private void init() {
		jlInfo.setText("[" + findResult.getImageWidth() + " x " + findResult.getImageHeight() + "]  "
				+ (findResult.getType()!=null&&!findResult.getType().isEmpty()?"(" + findResult.getType() + ")":""));
		
		this.addMouseListener(
				new MouseListener() {
					
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							Image image = loadFullImage(true);
 							jImage.openImage(image, new Dimension(1200,900));
						}
					}
					public void mouseExited(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
			});
	}

	private void buildPanel() {

		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = null;
		gbc =  new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		this.add(jImage,gbc);
		
		jlInfo = new JLabel("---");
		gbc =  new GridBagConstraints(0,1,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		this.add(jlInfo,gbc);
		
		jbtSave = new JButton("Grabar");
		gbc =  new GridBagConstraints(0,2,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		this.add(jbtSave,gbc);
		
		jbtSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				saveImage();
			}
		});
		
    }
	
	public void saveImage() {
		saveImage(null);
	}
	
	public void saveImage(Image image) {

		if (findResult.getImageURL()!=null) {
	        Boolean saved = null;
	        try {
	        	File fileToSave = new File(currentDir + FileSystems.getDefault().getSeparator() + findResult.getNameWithCode());
	        	
	        	if (!fileToSave.exists()) {
	        		saved=Boolean.FALSE;
	        		if (image==null)
	        			image=loadFullImage(false);
		        	findResult.setImage((BufferedImage)image);
		        	if (findResult.getImage()==null) {
		        		showError("No se pudo descargar el fichero");
		        		return;
		        	}
		        	saveImageToFile(findResult.getImage(),fileToSave.getAbsolutePath());
		        	saved=Boolean.TRUE;
	        	}
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        if (saved!=null)
		        if (!saved.equals(Boolean.TRUE)) {
		        	showError("No se pudo grabar el fichero");
		        } else {
		        	fireSaveEvent(findResult);
		        }
		}
	}

	private Image loadFullImage(boolean saveIfNotExists) {
		Image image = null;
  	    try {
        	File fileToLoad = new File(currentDir + FileSystems.getDefault().getSeparator() + findResult.getNameWithCode());
        	
	    	if (fileToLoad.exists()) {
	    		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileToLoad));
	        	image = ImageIO.read(bis);
	    	} else {
	    		image = downloadFullImage();
	  	    	findResult.setImage((BufferedImage)image);
	  	    	if (saveIfNotExists) {
	  	    		saveImage(image);
	  	    	}
	    	}
  	    } catch (Exception e) {
  	    	e.printStackTrace();
        	showError("No se pudo cargar el fichero");
		}
	    return image;
	}
	
	private Image downloadFullImage() {
		Image image = null;
  	    try {
	    	if (findResult.getImageURL()!=null) {
	    		logger.info("Downloading '" + findResult.getImageURL() + "'");
	        	ByteArrayOutputStream bos = Util.downloadURL(findResult.getImageURL().toString(), userAgent);
	        	byte[] imageBytes = bos.toByteArray();
	        	image = ImageIO.read(new ByteArrayInputStream(imageBytes));
	    	}
  	    } catch (Exception e) {
  	    	e.printStackTrace();
        	showError("No se pudo descargar el fichero");
		}
    	return image;
	}
	
//	private void saveThumbnailImage(Image image) {
//		String destFile = currentDir + FileSystems.getDefault().getSeparator() + findResult.getName();
//		saveImageToFile(findResult.getThumbnailImage(),destFile);
//	}
	
	private void saveImageToFile(Image image, String fullPathName) {
		
		if (image!=null) {
	        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	        //bufferedImage is the RenderedImage to be written
	 
	        Graphics2D g2 = bufferedImage.createGraphics();
	        g2.drawImage(image, null, null);
	 
	        File imageFile = null;
	        boolean saved = false;
	        try {
	        	String type = findResult.getType();
	        	if (type==null || type.isEmpty()) {
	        		type=DEFAULT_SAVE_FORMAT;
	        	}
	        	imageFile = new File(fullPathName);

	        	saved = ImageIO.write(bufferedImage, type, imageFile);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        if (!saved) {
	        	showError("No se pudo grabar el fichero");
	        }
		}
		
//		JFileChooser fc = new JFileChooser(currentDir);
//        String imgType = identifyImgFileData(data);
//        fc.setAcceptAllFileFilterUsed(true);
//        if (imgType!=null) {
//            fc.setFileFilter(new CustomFileFilter(new String[]{"."+ imgType},imgType));
//        }
//        File f = new File(findResult.getTitle());
//        fc.setSelectedFile(f);
//        int returnVal = fc.showSaveDialog(JImagePanel.this);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = fc.getSelectedFile();
//            currentDir = fc.getCurrentDirectory();
////		            	((JPanelAnalysisFotos)parent).setCurrentDir(currentDir);
//            System.out.println("Saving: " + file.getName() + ".");
//
//    		BufferedOutputStream bof = null;
//    		try {
//    			bof = new BufferedOutputStream(new FileOutputStream(file));
//    			bof.write(data);
//
//    		} catch (Exception e) {
//    			System.out.println("Error saving the file '"+ file.getName() + "'");
//    			showError("Error saving the file '\"+ file.getName() + \"'\"");
//    		} catch (Throwable e) {
//    			e.printStackTrace();
//    			System.out.println("Error(Throwable) saving the file '"+ file.getName() + "'");
//    			showError("Error(Throwable) saving the file '"+ file.getName() + "'");
//    		} finally {
//    			try {
//    				bof.close();
//    			}catch (Exception e) {
//    				System.out.println("Error closing file '"+ file.getName() + "'");
//    			}catch (Throwable e) {
//    				System.out.println("Error(Throwable) closing file '"+ file.getName() + "'");
//    			}
//    		}
//        } else {
//            System.out.println("Save command cancelled by user.");
//        }

	}
	
	public String getFormatName(InputStream input) throws IOException {
	    ImageInputStream stream = ImageIO.createImageInputStream(input);

	    Iterator iter = ImageIO.getImageReaders(stream);
	    if (!iter.hasNext()) {
	        return null;
	    }
	    ImageReader reader = (ImageReader) iter.next();
	    ImageReadParam param = reader.getDefaultReadParam();
	    reader.setInput(stream, true, true);
	    BufferedImage bi;
	    try {
	        bi = reader.read(0, param);
	        return reader.getFormatName();
	    } finally {
	        reader.dispose();
	        stream.close();
	    }
	}
	
	public void fitToViewer() {
		jImage.fitToViewer();
	}

	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}
	
	public void setCurrentDir(String outDir) {
		this.currentDir = new File(outDir);
	}
	
	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
	

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
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
	
	private void showError(String error)
	{
		if (error != null)
		{
			JOptionPane.showMessageDialog(this,error,"Error",JOptionPane.ERROR_MESSAGE);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private List<JImagePanelListener> saveListeners = new ArrayList<JImagePanelListener>();

//	public synchronized void receiveEvent(Integer type) {
//		if (type!=null) {
//			if (type.equals(JImagePanelEvent.EVENT_SAVE)) {
//				fireSaveEvent(JImagePanelEvent.EVENT_SAVE);
//			}
//		}
//	}
	
	public synchronized void addSaveListener(JImagePanelListener l) {
		saveListeners.add(l);
	}

	public synchronized void removeSaveListener(JImagePanelListener l) {
		saveListeners.remove(l);
	}

	private synchronized void fireSaveEvent(FindResult findResult) {
		JImagePanelEvent event = new JImagePanelEvent(findResult, JImagePanelEvent.EVENT_SAVE);
		Iterator<JImagePanelListener> it = saveListeners.iterator();
		while (it.hasNext()) {
			((JImagePanelListener) it.next()).saveEventReceived(event);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
