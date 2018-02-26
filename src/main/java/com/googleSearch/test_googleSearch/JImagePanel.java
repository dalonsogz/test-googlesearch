package com.googleSearch.test_googleSearch;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
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

public class JImagePanel extends JPanel {

	private static final long serialVersionUID = -9089648740073530708L;


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
    	jImage = new JImage(findResult.getThumbnailImage(),zoomPercentage,vieWerDim);
    	buildPanel();
    	init();
    }

	private void init() {
		jlInfo.setText("[" + findResult.getImageWidth() + " x " + findResult.getImageHeight() + "](" + findResult.getType() + ")]");
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

        boolean saved = false;
        try {
        	if (findResult.getImage()==null) {
//        		String destination = Util.downloadURL(findResult.getImageURL(),downloadPath,findResult.getName(),userAgent);
//	        	String fullPathName = downloadPath + FileSystems.getDefault().getSeparator() + findResult.getName();
	        	ByteArrayOutputStream bos = Util.downloadURL(findResult.getImageURL(), userAgent);
	        	byte[] imageBytes = bos.toByteArray();
	        	Image image = ImageIO.read(new ByteArrayInputStream(imageBytes));
	        	findResult.setImage((BufferedImage)image);
        	}
        	
        	saveImageToFile(findResult.getImage());
        	
        	saved=true;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        if (!saved) {
        	showError("No se pudo grabar el fichero");
        } else {
        	fireSaveEvent(findResult);
        }
	}

	private void saveThumbnailImage(Image image) {
		saveImageToFile(findResult.getThumbnailImage());
	}
	
	private void saveImageToFile(Image image) {
		
		if (image!=null) {
	        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	        //bufferedImage is the RenderedImage to be written
	 
	        Graphics2D g2 = bufferedImage.createGraphics();
	        g2.drawImage(image, null, null);
	 
	        File imageFile = null;
	        boolean saved = false;
	        try {
	        	String fullPathName = currentDir + FileSystems.getDefault().getSeparator() + findResult.getName();
	        	String type = findResult.getType();
	        	if (type==null || type.isEmpty()) {
	        		type=DEFAULT_SAVE_FORMAT;
	        	}
	        	fullPathName+="."+type;
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
