package com.googleSearch.test_googleSearch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FilenameUtils;

import com.googleSearch.test_googleSearch.Util.ExtensionsFilter;


public class Gui extends JFrame implements LoggerComponent,JImagePanelListener {

	private static Log logger = Log.getInstance().getLogger();
	private static final long serialVersionUID = 123123123;
	
	// Configuration parameters
    public final static String CONFIG_FILE = "googleSearch.ini";

	private static String PARAMCOMFIGFILE = "outputDir";
	private static String PARAMTEMPDIR = "tempDir";
	private static String PARAMUSERAGENT = "userAgent";
	private static String PARAMEXCLUDEDWORDS = "excludedWords";
	private static String PARAMSEARCHMODDS = "searchMods";
	private static String PARAMTARGETDIR = "targetDir";
	private static String PARAMPROXYHOST = "proxyHost";
	private static String PARAMPROXYPORT = "proxyPort";

	private static String outputDir = null;
	private static String tempDir = null;
	private static String userAgent = null;
	private static String[] excludedWords = null;
	private static String searchMods = null;
	private static String targetDir = null;
	private static String proxyHost = null;
	private static String proxyPort = null;
	/////////////////////////////
	private final static String LISTA_HECHO = "lista_hecho.log";
	private final static ExtensionsFilter EXT_FILTER = new ExtensionsFilter(new String[] {".iso"});
	private final static String PADDING = "                                                                                 ";

	private final static Integer MODE_INACTIVE = 0;
	private final static Integer MODE_SELECTED = 1;
	private final static Integer MODE_AUTOMATIC = 2;
	private final static Integer MODE_MANUAL = 3;

	private final int IMG_BUTTON_SIZE = 25;

	public static int APP_WIDTH = 2300;
	public static int APP_HEIGHT = 1400;

	private final static int PIC_COLS = 6;
	private final static int PIC_ROWS = 2;
	
	private JDesktopPane jDesktopPane = null;
	private JInternalFrame lastPanel = null;
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private ArrayList<JImagePanel> jImagePanels = null;

	// ===================================================== fields
	private JButton logFileButton = null;
	private JButton configFileButton = null;
	private JButton logConfigFileButton = null;
	private JButton parsedNewsButton = null;
	private static JTextArea textArea = null;
	private static JScrollPane scrollPane = null;

	private JLabel jlTarget = null;
	private JButton jbtTargetDir = null;
	private JButton jbtTargetFile = null;
	private JList<String> jlTargets = new JList<String>();
	private JButton jbtItemDel = null;
	private JButton jbtItemUp = null;
	private JButton jbtItemDown = null;

	private JLabel jlOutDir = null;
	private JTextField jtfOutDir = null;
	private JButton jbtOutDir = null;
	private JLabel jlTempDir = null;
	private JTextField jtfTempDir = null;
	private JButton jbtTempDir = null;
	private JLabel jlUserAgent = null;
	private JTextField jtfUserAgent = null;
	private JLabel jlExcludedWords = null;
	private JTextField jtfExcludedWords = null;
	private JLabel jlSearchMods = null;
	private JTextField jtfSearchMods = null;
	private JButton jbtSaveConfigParams = null;
	
	private JScrollPane jScrollPaneTargets = null;
	private JLabel jlWidth = null;
	private JTextField jtfWidth = null;
	private JLabel jlfHeight = null;
	private JTextField jtfHeight = null;
	private JRadioButton jrbSizeExact = null;
	private JRadioButton jrbSizeRelative = null;
	private JLabel jlSizeRelativeMargin = null;
	private JTextField jtfSizeRelativeMargin = null;
	private JRadioButton jrbSizeRelativeXY = null;
	private JLabel jlSizeRelativeWidth = null;
	private JTextField jtfSizeRelativeWidth = null;
	private JLabel jlSizeRelativeHeight = null;
	private JTextField jtfSizeRelativeHeight = null;
	private JRadioButton jrbModeSimple = null;
	private JRadioButton jrbModeAutomatic = null;
	private JRadioButton jrbModeManual = null;
	private JLabel jlWaitResponse = null;

	private JButton jbtStop = null;
	private JButton jbtStart = null;

	private Integer activeMode = null;
	private int manualModeCurrentListIndex = 0;
	
	
	// ================================================ Constructor
	public Gui() {
		try {
			logger.info("Application starting...");
			buildGui();
			init();
			logger.info("Application started",this);
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDesktopPane()
	{
		if (jDesktopPane != null)
			getContentPane().remove(jDesktopPane);

		jDesktopPane = new JDesktopPane();
		jDesktopPane.setName("Desktop Principal");
//		jDesktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		UIDefaults ui = UIManager.getDefaults();
		if (((InputMapUIResource)ui.get("Desktop.ancestorInputMap")) != null)
			((InputMapUIResource)ui.get("Desktop.ancestorInputMap")).clear();
	}
	
	private void initMainFrame() {

		System.gc();
		try {
			if (UIManager.getSystemLookAndFeelClassName().indexOf("Windows") != -1)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else
				javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			System.out.println("Setting system look & feel");
			// UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

//		try {
//			Container c = this;
//			while (c.getParent() != null && !(c instanceof Frame)) {
//				c = c.getParent();
//			}
//			int width = this.getWidth();
//			int height = this.getHeight();
//			System.out.println("Resolution " + this.getWidth() + " th " + this.getHeight());
	
//			if (c instanceof Frame) {
//				Frame f = (Frame) c;
				// f.setSize(width, height);
				// f.setMinimumSize(new Dimension(width, height));
				// f.setPreferredSize(new Dimension(width, height));
//				Dimension dim = getToolkit().getScreenSize();
				// f.setLocation((dim.width - GuiConstants.APP_WIDTH) / 2 ,
				// (dim.height - GuiConstants.APP_HEIGHT -55) / 2);
				// f.setIconImage(getImage("tng/TNG.Logo.32.png"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		// changeJPanel(new JPanelLogin(this));
		// HttpServer.setCsvContent("1;1;1;1;1;1;1;1;1;1;");
		// HttpServer.setHtmlContent("");
		// HttpServer.setApplet(this);
	}

	private void buildGui() throws Exception {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.NORTH,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		this.initDesktopPane();
		gbl.setConstraints(jDesktopPane, gbc);
		this.getContentPane().add(jDesktopPane);

		this.setTitle("Parsing Debug");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(APP_WIDTH,APP_HEIGHT));
//		this.setBackground(Color.WHITE);

        initMainFrame();
		
        JPanel mainPanel = buildPanel();
//        mainPanel.setBackground(Color.WHITE);

        changeJPanel(mainPanel);
        
        pack();
		setVisible(true);
	}
	
	private JPanel buildPanel() throws Exception {
		
		JPanel content = new JPanel();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		textArea = new JTextArea();
		textArea.setAutoscrolls(true);
		textArea.setFont(new Font("Courier", Font.PLAIN, 12));
		textArea.setBackground(new Color(255,250,240));
		
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollPane.setPreferredSize(new Dimension(900, 150));
		textArea.setLineWrap(true);
		
		southPanel.setPreferredSize(new Dimension(900, 600));

		parsedNewsButton = new JButton("Parsed News");
		configFileButton = new JButton("Configuration");
		logFileButton = new JButton("Log file");
		logConfigFileButton = new JButton("Log Configuration");
//		startButton = new JButton("Empezar");

		northPanel.setLayout(new BorderLayout());
		JPanel northPanelNorth = new JPanel(new FlowLayout());
		northPanel.add(northPanelNorth,BorderLayout.NORTH);
		northPanelNorth.add(parsedNewsButton, BorderLayout.WEST);
		northPanelNorth.add(configFileButton, BorderLayout.WEST);
		northPanelNorth.add(new JLabel(PADDING.substring(0, 40)),BorderLayout.CENTER);
//		northPanelNorth.add(startButton, BorderLayout.CENTER);
		northPanelNorth.add(new JLabel(PADDING.substring(0, 40)),BorderLayout.CENTER);
		northPanelNorth.add(logFileButton, BorderLayout.EAST);
		northPanelNorth.add(logConfigFileButton, BorderLayout.EAST);
		northPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel controlPanel = buildControlPanel();
		
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(controlPanel, BorderLayout.CENTER);

		// ... Add components to layout
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		content.setLayout(gbl);
		gbc =  new GridBagConstraints(0,0,1,1,1.0,0.25,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		content.add(northPanel,gbc);
		gbc =  new GridBagConstraints(0,1,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		content.add(centerPanel,gbc);
		gbc =  new GridBagConstraints(0,2,1,1,1.0,0.5,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		content.add(southPanel,gbc);

		content.setBackground(Color.WHITE);
		northPanel.setBackground(Color.WHITE);
		centerPanel.setBackground(Color.LIGHT_GRAY);
		southPanel.setBackground(Color.WHITE);
		northPanelNorth.setBackground(Color.WHITE);
		controlPanel.setBackground(Color.WHITE);
		
		DefaultListModel<String> jlDefaultListModel = new DefaultListModel<>();
		jlTargets.setModel(jlDefaultListModel);
		jlTargets.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		logFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler log.txt");
				} catch (IOException ioe) {
					logger.error("IOException: " + ioe.getMessage());
				}
			}
		});

		configFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + CONFIG_FILE);
				} catch (IOException ioe) {
					logger.error("IOException: " + ioe.getMessage());
				}
			}
		});

		logConfigFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + Log.CONFIG_FILE);
				} catch (IOException ioe) {
					logger.error("IOException: " + ioe.getMessage());
				}
			}
		});

		parsedNewsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + LISTA_HECHO);
				} catch (IOException ioe) {
					logger.error("IOException: " + ioe.getMessage());
				}
			}
		});
		
		jbtStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				
				try {
					readSearchParams();

					if (jrbModeSimple.isSelected()) {
						setMode(MODE_SELECTED);
						List<String> targets = jlTargets.getSelectedValuesList();
						if (targets!=null && targets.size()==1) {
							ParserProcess parserProcess = new ParserProcess((String)targets.get(0),PIC_COLS*PIC_ROWS);
							parserProcess.start();
						}
					} else if (jrbModeAutomatic.isSelected()) {
						setMode(MODE_AUTOMATIC);
						String target = null;
						ListModel<String> model = jlTargets.getModel();
						for (int i = 0; i < model.getSize(); i++) {
							target = model.getElementAt(i);
							jlTargets.setSelectedIndex(i);
							logger.debug(target);
							ParserProcess parserProcess = new ParserProcess(target,1);
							parserProcess.start();
							try {
								parserProcess.join();
							} catch (InterruptedException ie) {
								ie.printStackTrace();
							}
							if (jImagePanels!=null && jImagePanels.size()>0) {
								jImagePanels.get(0).saveImage();
							}
//							boolean continueWithNext = showConfirmation("Imagen '"+target+"' grabada.");
//							if (!continueWithNext) {
//								break;
//							}
						}
					} else if (jrbModeManual.isSelected()) {
						setMode(MODE_MANUAL);
						processNextManualItem();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (!jrbModeManual.isSelected()) {
						setMode(MODE_INACTIVE);
					}
				}
			}

		});

		jbtStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setMode(MODE_INACTIVE);
			}
		});

		
//		jrbSizeRelative.setSelected(true);
//		jrbModeSimple.setSelected(true);
		
		
		////////////////////////////////// TEST VALUES ///////////////////////////////////////////////////////
		jtfWidth.setText("400");
		jtfHeight.setText("600");
		jrbSizeRelative.setSelected(true);
		jtfSizeRelativeMargin.setText("0.2");
		jrbModeSimple.setSelected(true);
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		return content;
	}
	
	private JPanel  buildControlPanel() throws Exception {
		
		JPanel controlPanel = new JPanel();
		
		JPanel leftPanel= new JPanel();
		JPanel centerPanel= new JPanel();
		JPanel rightPanel= new JPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		controlPanel.setLayout(new GridBagLayout());
		gbc =  new GridBagConstraints(0,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		controlPanel.add(leftPanel,gbc);
		gbc =  new GridBagConstraints(1,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		controlPanel.add(centerPanel,gbc);
		gbc =  new GridBagConstraints(2,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		controlPanel.add(rightPanel,gbc);

		leftPanel.setLayout(new GridBagLayout());
		centerPanel.setLayout(new GridBagLayout());
		rightPanel.setLayout(new GridBagLayout());

		ImageIcon delIcon = getScaledImage("cross.png",IMG_BUTTON_SIZE);
		ImageIcon upIcon = getScaledImage("up.png",IMG_BUTTON_SIZE);
		ImageIcon downIcon = getScaledImage("down.png",IMG_BUTTON_SIZE);

		// leftPanel
		jlTarget = new JLabel();
		gbc =  new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,2,5),0,0);
		leftPanel.add(jlTarget,gbc);
		jbtTargetDir = new JButton("...");
		gbc =  new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,2,5),0,0);
		leftPanel.add(jbtTargetDir,gbc);
		jbtTargetFile = new JButton("File");
		gbc =  new GridBagConstraints(2,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,0,2,0),0,0);
		leftPanel.add(jbtTargetFile,gbc);
		jScrollPaneTargets = new JScrollPane();
		jScrollPaneTargets.setViewportView(jlTargets);
		gbc =  new GridBagConstraints(0,1,3,3,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,5,5,5),0,0);
		leftPanel.add(jScrollPaneTargets,gbc);
		jbtItemUp = new JButton(upIcon);
		gbc =  new GridBagConstraints(3,1,1,1,0.0,0.0,GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(20,0,5,0),0,0);
		leftPanel.add(jbtItemUp,gbc);
		jbtItemDown = new JButton(downIcon);
		gbc =  new GridBagConstraints(3,2,1,1,0.0,0.0,GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(0,0,50,0),0,0);
		leftPanel.add(jbtItemDown,gbc);
		jbtItemDel = new JButton(delIcon);
		gbc =  new GridBagConstraints(3,3,1,1,0.0,0.0,GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0);
		leftPanel.add(jbtItemDel,gbc);

		setIconButtonStyle(jbtItemDel);
		setIconButtonStyle(jbtItemUp);
		setIconButtonStyle(jbtItemDown);


		// centerPanel
		JPanel jpSize = new JPanel();
		jpSize.setLayout(new GridBagLayout());
		JPanel jpMode = new JPanel();
		jpMode.setLayout(new GridBagLayout());
		JPanel configPanel = buildConfigPanel();
		gbc =  new GridBagConstraints(0,0,2,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0);
		centerPanel.add(configPanel,gbc);
		gbc =  new GridBagConstraints(0,1,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0);
		centerPanel.add(jpSize,gbc);
		gbc =  new GridBagConstraints(1,1,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,50,5,5),0,0);
		centerPanel.add(jpMode,gbc);
		configPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jpSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jpMode.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JPanel jpSizePanel = new JPanel();
		jpSizePanel.setLayout(new GridBagLayout());
		jlWidth = new JLabel("Ancho");
		gbc =  new GridBagConstraints(0,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,5,2,2),0,0);
		jpSizePanel.add(jlWidth,gbc);
		jtfWidth = new JTextField(4);
		gbc =  new GridBagConstraints(1,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,5),0,0);
		jpSizePanel.add(jtfWidth,gbc);
		jlfHeight =new JLabel("Alto");
		gbc =  new GridBagConstraints(2,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,5,2,2),0,0);
		jpSizePanel.add(jlfHeight,gbc);
		jtfHeight = new JTextField(4);
		gbc =  new GridBagConstraints(3,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,5),0,0);
		jpSizePanel.add(jtfHeight,gbc);

		JPanel jpSizePanelOptions = new JPanel();
		jpSizePanelOptions.setLayout(new GridBagLayout());
		jrbSizeExact = new JRadioButton("Exacto");
		jrbSizeExact.setOpaque(false);
		gbc =  new GridBagConstraints(0,1,2,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jrbSizeExact,gbc);
		jrbSizeRelative = new JRadioButton("Relativo");
		jrbSizeRelative.setOpaque(false);
		gbc =  new GridBagConstraints(0,2,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jrbSizeRelative,gbc);
		jlSizeRelativeMargin = new JLabel("Ratio");
		gbc =  new GridBagConstraints(1,2,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jlSizeRelativeMargin,gbc);
		jtfSizeRelativeMargin = new JTextField(3);
		gbc =  new GridBagConstraints(2,2,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jtfSizeRelativeMargin,gbc);
		jrbSizeRelativeXY = new JRadioButton("Relativo");
		jrbSizeRelativeXY.setOpaque(false);
		gbc =  new GridBagConstraints(0,3,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jrbSizeRelativeXY,gbc);
		jlSizeRelativeWidth = new JLabel("+-Ancho");
		gbc =  new GridBagConstraints(1,3,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jlSizeRelativeWidth,gbc);
		jtfSizeRelativeWidth = new JTextField(3);
		gbc =  new GridBagConstraints(2,3,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jtfSizeRelativeWidth,gbc);
		jlSizeRelativeHeight = new JLabel("+-Alto");
		gbc =  new GridBagConstraints(3,3,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jlSizeRelativeHeight,gbc);
		jtfSizeRelativeHeight = new JTextField(3);
		gbc =  new GridBagConstraints(4,3,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		jpSizePanelOptions.add(jtfSizeRelativeHeight,gbc);
		
		gbc =  new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,2,5),0,0);
		jpSize.add(jpSizePanel,gbc);
		gbc =  new GridBagConstraints(0,1,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,5,5,5),0,0);
		jpSize.add(jpSizePanelOptions,gbc);

		
		ButtonGroup bgSize = new ButtonGroup();
		bgSize.add(jrbSizeExact);
		bgSize.add(jrbSizeRelative);
		bgSize.add(jrbSizeRelativeXY);


		jrbModeSimple = new JRadioButton("Seleccionado");
		jrbModeSimple.setOpaque(false);
		gbc =  new GridBagConstraints(0,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		jpMode.add(jrbModeSimple,gbc);
		jrbModeAutomatic = new JRadioButton("Automático");
		jrbModeAutomatic.setOpaque(false);
		gbc =  new GridBagConstraints(0,1,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		jpMode.add(jrbModeAutomatic,gbc);
		jrbModeManual = new JRadioButton("Manual");
		jrbModeManual.setOpaque(false);
		gbc =  new GridBagConstraints(0,2,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		jpMode.add(jrbModeManual,gbc);
		jlWaitResponse = new JLabel("");
		gbc =  new GridBagConstraints(1,1,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		jpMode.add(jlWaitResponse,gbc);
		ButtonGroup bgMode = new ButtonGroup();
		bgMode.add(jrbModeSimple);
		bgMode.add(jrbModeAutomatic);
		bgMode.add(jrbModeManual);


		// rightPanel
		jbtStart = new JButton("Empezar");
		gbc =  new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		rightPanel.add(jbtStart,gbc);
		jbtStop = new JButton("Parar");
		gbc =  new GridBagConstraints(0,1,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		rightPanel.add(jbtStop,gbc);
		
//		controlPanel.setBackground(Color.WHITE);
//		leftPanel.setBackground(Color.WHITE);
//		centerPanel.setBackground(Color.WHITE);
//		rightPanel.setBackground(Color.WHITE);
//		jpSize.setBackground(Color.WHITE);
//		jpMode.setBackground(Color.WHITE);
//		configPanel.setBackground(Color.WHITE);
//		jpSizePanel.setBackground(Color.WHITE);
//		jpSizePanelOptions.setBackground(Color.WHITE);

		
		jrbSizeExact.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean sizeExact = false;
				if (jrbSizeExact.isSelected()) {
					sizeExact = true;
				}
				if (sizeExact) {
					jtfSizeRelativeMargin.setEnabled(false);
					jtfSizeRelativeWidth.setEnabled(false);
					jtfSizeRelativeHeight.setEnabled(false);
				}
			}
		});
		
		jrbSizeRelative.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean sizeRelativeEnabled = false;
				if (jrbSizeRelative.isSelected()) {
					sizeRelativeEnabled = true;
				}
				jtfSizeRelativeMargin.setEnabled(sizeRelativeEnabled);
			}
		});

		jrbSizeRelativeXY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean sizeRelativeXYEnabled = false;
				if (jrbSizeRelativeXY.isSelected()) {
					sizeRelativeXYEnabled = true;
				}
				jtfSizeRelativeWidth.setEnabled(sizeRelativeXYEnabled);
				jtfSizeRelativeHeight.setEnabled(sizeRelativeXYEnabled);
			}
		});

		jbtTargetDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(jlTarget.getText()));
				chooser.setDialogTitle("Select target dir.");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(Gui.this) == JFileChooser.APPROVE_OPTION) {
					fillTargets(chooser.getSelectedFile());
					jlTarget.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});		
		
		jbtTargetFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jbChooseFileActionPerformed(e);
			}
		});

		jbtItemDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow(jlTargets);
			}
		});

		jbtItemUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveRowUp(jlTargets);
			}
		});

		jbtItemDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveRowDown(jlTargets);
			}
		});

		return controlPanel;
	}

	private JPanel  buildConfigPanel() throws Exception {
		
		JPanel configPanel = new JPanel();
		
		JPanel centerPanel= new JPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		configPanel.setLayout(new GridBagLayout());
		gbc =  new GridBagConstraints(0,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0);
		configPanel.add(centerPanel,gbc);

		centerPanel.setLayout(new GridBagLayout());

		// centerPanel
		jlOutDir = new JLabel("Output dir.");
		gbc =  new GridBagConstraints(0,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jlOutDir,gbc);
		jtfOutDir = new JTextField(65);
		gbc =  new GridBagConstraints(1,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jtfOutDir,gbc);
		jbtOutDir = new JButton("...");
		gbc =  new GridBagConstraints(2,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jbtOutDir,gbc);
		jlTempDir = new JLabel("Temp dir.");
		gbc =  new GridBagConstraints(0,1,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jlTempDir,gbc);
		jtfTempDir = new JTextField(65);
		gbc =  new GridBagConstraints(1,1,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jtfTempDir,gbc);
		jbtTempDir = new JButton("...");
		gbc =  new GridBagConstraints(2,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jbtTempDir,gbc);
		jlUserAgent = new JLabel("User agent");
		gbc =  new GridBagConstraints(0,2,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jlUserAgent,gbc);
		jtfUserAgent = new JTextField(65);
		gbc =  new GridBagConstraints(1,2,2,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jtfUserAgent,gbc);
		jlExcludedWords = new JLabel("Excluded words");
		gbc =  new GridBagConstraints(0,3,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jlExcludedWords,gbc);
		jtfExcludedWords = new JTextField(65);
		gbc =  new GridBagConstraints(1,3,2,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jtfExcludedWords,gbc);
		jlSearchMods = new JLabel("Modificadores");
		gbc =  new GridBagConstraints(0,4,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jlSearchMods,gbc);
		jtfSearchMods = new JTextField(65);
		gbc =  new GridBagConstraints(1,4,2,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jtfSearchMods,gbc);
		
		jbtSaveConfigParams = new JButton("Grabar");
		gbc =  new GridBagConstraints(3,0,1,10,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0);
		centerPanel.add(jbtSaveConfigParams,gbc);
		
		
//		configPanel.setBackground(Color.WHITE);
//		centerPanel.setBackground(Color.WHITE);

		
		jbtSaveConfigParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveParams();
				logger.debug("Parameters saved");
			}
		});
		
		jbtOutDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select output dir.");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(Gui.this) == JFileChooser.APPROVE_OPTION) {
					jtfOutDir.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		jbtTempDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select temp dir.");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(Gui.this) == JFileChooser.APPROVE_OPTION) {
					jtfTempDir.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});

		return configPanel;
	}

	
	public void changeJPanel(JPanel jPanel) throws Exception {
		if (jPanel != null) {
			if (this.lastPanel != null) {
				jDesktopPane.remove(this.lastPanel);
				SwingUtilities.freeMemory(this.lastPanel);
			}

			this.lastPanel = new JInternalFrame();
			lastPanel.getContentPane().add(jPanel);
			lastPanel.setVisible(true);
			try {
				if (lastPanel.isEnabled())
					lastPanel.setSelected(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			lastPanel.addComponentListener(new ComponentListener() {
				public void componentResized(ComponentEvent evt) {
					Component c = (Component) evt.getSource();
					//APP_WIDTH = (int) this.getWidth();
					//APP_HEIGHT = (int) this.getHeight();
					// ........
				}

				public void componentHidden(ComponentEvent e) {
				}

				public void componentMoved(ComponentEvent e) {
				}

				public void componentShown(ComponentEvent e) {
				}
			});

			lastPanel.pack();
			lastPanel.updateUI();
			lastPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

			jPanel.setOpaque(true);
			jDesktopPane.add(lastPanel, new Integer(0));

			try {
				((javax.swing.plaf.basic.BasicInternalFrameUI) lastPanel.getUI()).setNorthPane(null);
				lastPanel.setMaximum(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println("add new " + jPanel);
		}

		setSize(new Dimension(APP_WIDTH,APP_HEIGHT));
		pack();
		repaint();
	}

	private void setMode(Integer mode) {
		
		try {
			
			activeMode=mode;
			if (mode.equals(MODE_SELECTED) || mode.equals(MODE_AUTOMATIC) || mode.equals(MODE_MANUAL)) {
				jlTarget.setEnabled(false);
				jbtTargetDir.setEnabled(false);
				jbtTargetFile.setEnabled(false);
				jlTargets.setEnabled(false);
				jbtItemDel.setEnabled(false);
				jbtItemUp.setEnabled(false);
				jbtItemDown.setEnabled(false);
				jtfOutDir.setEnabled(false);
				jbtOutDir.setEnabled(false);
				jtfTempDir.setEnabled(false);
				jbtTempDir.setEnabled(false);
				jtfUserAgent.setEnabled(false);
				jtfSearchMods.setEnabled(false);
				jtfExcludedWords.setEnabled(false);
				jbtSaveConfigParams.setEnabled(false);
				jtfWidth.setEnabled(false);
				jtfHeight.setEnabled(false);
				jrbSizeExact.setEnabled(false);
				jrbSizeRelative.setEnabled(false);
				jtfSizeRelativeMargin.setEnabled(false);
				jrbSizeRelativeXY.setEnabled(false);
				jlSizeRelativeWidth.setEnabled(false);
				jtfSizeRelativeWidth.setEnabled(false);
				jlSizeRelativeHeight.setEnabled(false);
				jtfSizeRelativeHeight.setEnabled(false);
				jrbModeSimple.setEnabled(false);
				jrbModeAutomatic.setEnabled(false);
				jrbModeManual.setEnabled(false);
				jbtStart.setEnabled(false);
			} else if (mode.equals(MODE_INACTIVE)) {
				jlTarget.setEnabled(true);
				jbtTargetDir.setEnabled(true);
				jbtTargetFile.setEnabled(true);
				jlTargets.setEnabled(true);
				jbtItemDel.setEnabled(true);
				jbtItemUp.setEnabled(true);
				jbtItemDown.setEnabled(true);
				jtfOutDir.setEnabled(true);
				jbtOutDir.setEnabled(true);
				jtfTempDir.setEnabled(true);
				jbtTempDir.setEnabled(true);
				jtfUserAgent.setEnabled(true);
				jtfSearchMods.setEnabled(true);
				jtfExcludedWords.setEnabled(true);
				jbtSaveConfigParams.setEnabled(true);
				jtfWidth.setEnabled(true);
				jtfHeight.setEnabled(true);
				jrbSizeExact.setEnabled(true);
				jrbSizeRelative.setEnabled(true);
				jtfSizeRelativeMargin.setEnabled(true);
				jrbSizeRelativeXY.setEnabled(true);
				jlSizeRelativeWidth.setEnabled(true);
				jtfSizeRelativeWidth.setEnabled(true);
				jlSizeRelativeHeight.setEnabled(true);
				jtfSizeRelativeHeight.setEnabled(true);
				jrbModeSimple.setEnabled(true);
				jrbModeAutomatic.setEnabled(true);
				jrbModeManual.setEnabled(true);
				jbtStart.setEnabled(true);
				manualModeCurrentListIndex=0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		
		initParams();
        File fileBaseDir = new File(targetDir);

		jtfOutDir.setText(outputDir);
		jtfTempDir.setText(tempDir);
		jtfUserAgent.setText(userAgent);
		jtfExcludedWords.setText(Util.arrayToString(excludedWords));
		jtfSearchMods.setText(searchMods);
		jlTarget.setText(targetDir);
		
		fillTargets(fileBaseDir);
	}
	
	private void fillTargets(File fileBaseDir) {
		
		((DefaultListModel)jlTargets.getModel()).removeAllElements();
		
		if (targetDir!=null) {
			File fileTargetDir = new File(targetDir);
			if (fileTargetDir.isFile()) {
				fillJbChooseFile(fileTargetDir.getAbsolutePath());
			} else if (fileTargetDir.isDirectory()) {
		        // Reading directory contents
		        File[] files = fileBaseDir.listFiles(EXT_FILTER);
		        if (files!=null && files.length>0) {
		        	String fileName = null;
		        	for (File file:files) {
		        		fileName=FilenameUtils.getBaseName(file.getPath());
		        		((DefaultListModel)jlTargets.getModel()).addElement(fileName);
		        	}
		        }
			}
		}
		
    }

	public void addTextArea(String cad) {
		if (textArea!=null) {
			textArea.append(cad);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.validate();
		}
	}

	private void processNextManualItem() {

		manualModeCurrentListIndex++;
		try {
			String target = null;
			ListModel<String> model = jlTargets.getModel();
			if (model.getSize()>=manualModeCurrentListIndex) {
				target = model.getElementAt(manualModeCurrentListIndex-1);
				jlTargets.setSelectedIndex(manualModeCurrentListIndex-1);
				logger.debug(target);
				ParserProcess parserProcess = new ParserProcess(target,PIC_COLS*PIC_ROWS);
				parserProcess.start();
				parserProcess.join();
			} else {
				setMode(MODE_INACTIVE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			manualModeCurrentListIndex--;
		}
	}
	
	private void readSearchParams() throws Exception {
		
		outputDir = jtfOutDir.getText();
		tempDir = jtfTempDir.getText();
		userAgent = jtfUserAgent.getText();
		excludedWords = Util.stringToArray(jtfExcludedWords.getText());
		searchMods = jtfSearchMods.getText();
		targetDir = jlTarget.getText();
	}
	
	class ParserProcess extends Thread {
		
		private String searchString = null;
		private Integer numResults = null;
		
		public ParserProcess(String searchString, Integer numResults) {
			this.searchString=searchString;
			this.numResults=numResults;
		}

		public void run() {
			
			try {

				// Leer parametros de tamaño
				Integer widthParam = jtfWidth.getText()!=null&&!jtfWidth.getText().isEmpty()?new Integer(jtfWidth.getText()):null;
				Integer heightParam = jtfHeight.getText()!=null&&!jtfHeight.getText().isEmpty()?new Integer(jtfHeight.getText()):null;
				Float sizeMarginParam = null;
				if (jrbSizeRelative.isSelected()) {
					sizeMarginParam = jtfSizeRelativeMargin.getText()!=null&&!jtfSizeRelativeMargin.getText().isEmpty()?new Float(jtfSizeRelativeMargin.getText()):null;
				}
				Integer widthMarginParam = null;
				Integer heightMarginParam = null;
				if (jrbSizeRelativeXY.isSelected()) {
					widthMarginParam = jtfSizeRelativeWidth.getText()!=null&&!jtfSizeRelativeWidth.getText().isEmpty()?new Integer(jtfSizeRelativeWidth.getText()):null;
					heightMarginParam = jtfSizeRelativeHeight.getText()!=null&&!jtfSizeRelativeHeight.getText().isEmpty()?new Integer(jtfSizeRelativeHeight.getText()):null;
				}
				

//				startButton.setEnabled(false);
//				jbtStart.setEnabled(false);
				
				jImagePanels = new ArrayList<JImagePanel>();
				southPanel.removeAll();
				southPanel.repaint();
				
				GridBagLayout gbl = new GridBagLayout();
				GridBagConstraints gbc = new GridBagConstraints();
				southPanel.setLayout(gbl);

				logger.debug("Search string: '" + searchString + "'");

	        	String question = removeExcludedWords(searchString,excludedWords);
	        	
//	        	if (jrbModeSimple.isSelected()) {
	//				ArrayList<FindResult> findResults = findImage(question,"PS2 cover",USER_AGENT,4);
					FindImageJSoup fij = new FindImageJSoup(searchString);
					logger.debug("question:"+ question);
			       	ArrayList<FindResult> findResults = fij.findImage(question,searchMods, userAgent,numResults,widthParam,heightParam,
			       			sizeMarginParam,widthMarginParam,heightMarginParam,
			       			proxyHost,(proxyPort!=null&&!proxyPort.isEmpty())?Integer.parseInt(proxyPort):null);
//			       	Util.writeSerializaedObject(findResults, "findResults");
//			       	ArrayList<FindResult> findResults = (ArrayList<FindResult>)Util.readSerializedObject("findResults");
//					
					String thumbsdir = tempDir + "thumbs" + FileSystems.getDefault().getSeparator();
					if (tempDir!=null && findResults!=null && findResults.size()>0) {
						String resourceStr = null;
						JImagePanel jImagePanel = null;
						int x=0;
						int y=0;
						for (FindResult findResult:findResults) {
							if (findResult.getImageURL()!=null) {
//								resourceStr = findResult.getImageURL();
								resourceStr = findResult.getThumbnailURL().toString();
								String destination = thumbsdir + findResult.getThumbnailNameWithCode();
								if (!new File(destination).isFile()) {
									destination = Util.downloadURL(resourceStr,thumbsdir,findResult.getThumbnailNameWithCode(),userAgent);
								}
								
								Image image = null;
								try {
									image = ImageIO.read(new File(destination)); //OUT_DIR + FileSystems.getDefault().getSeparator() + question));
									findResult.setThumbnailImage((BufferedImage)image);
								} catch (IOException e) {
									e.printStackTrace();
								}
								if (image!=null) {
									jImagePanel = new JImagePanel(findResult, 100, new Dimension(400,600));
									jImagePanel.fitToViewer();
									jImagePanel.setCurrentDir(outputDir);
									jImagePanel.setPreferredSize(new Dimension(200,300));
									jImagePanel.setUserAgent(userAgent);
									jImagePanel.setDownloadPath(outputDir);
	//								jImagePanel.setDownloadName(searchString);
									gbc =  new GridBagConstraints(x++,y,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0);
									jImagePanel.addSaveListener(Gui.this);
									jImagePanels.add(jImagePanel);
									southPanel.add(jImagePanel,gbc);
									southPanel.validate();
	//								break;
								}
	
								if (x%PIC_COLS==0) {
									x=0;
									y++;
								}
							}
						}
					}
//	        	}
				
	        	
	        	
//	        	if (Util.isDoneBefore(LISTA_HECHO,searchString)) {
//	        		logger.debug("Already done");
//	        		return;
//	        	}
	        	
//				Util.writeFile(LISTA_HECHO,searchString,true);
				
//				Thread.sleep(2000);

				
			} catch (Exception e) {
				showError("Error al hacer la consulta:" + e.getMessage());
				e.printStackTrace();
			} finally {
//				startButton.setEnabled(true);
//				jbtStart.setEnabled(true);
			}
		}
	}

    private void jbChooseFileActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(targetDir!=null?targetDir:"."));
        chooser.setDialogTitle("Fichero de nombres");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.showOpenDialog(Gui.this);

        if (chooser.getSelectedFile()!=null) {
        	targetDir = chooser.getSelectedFile().getAbsolutePath().substring(0,chooser.getSelectedFile().getAbsolutePath().lastIndexOf(System.getProperty("file.separator")));
        	String selFileAbsolutePath = chooser.getSelectedFile().getAbsolutePath();
        	fillJbChooseFile(selFileAbsolutePath);
        }
    }

    private void fillJbChooseFile(String selFileAbsolutePath) {
        System.out.println("-->Abrir fichero:" + selFileAbsolutePath);

        jlTarget.setText(selFileAbsolutePath);

		((DefaultListModel)jlTargets.getModel()).removeAllElements();

        BufferedReader reader = null;
        try {
            String line = null;
            int counter = 0;
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(selFileAbsolutePath), "ISO-8859-15"));
            while ((line = reader.readLine()) != null) {
            	((DefaultListModel)jlTargets.getModel()).addElement(line);
                counter++;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (reader!=null)
                    reader.close();
            } catch (Exception e) {
                System.out.println("Error cerrando File '" + selFileAbsolutePath + "':" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void deleteRow (JList jList) {
        int selRows [] = jList.getSelectedIndices();
        if (selRows!=null && selRows.length>0) {
        	DefaultListModel model = (DefaultListModel) jList.getModel();
            for (int i=selRows.length-1;i>=0;i--) {
            	model.remove(selRows[i]);
            }
        }
    }

    private void moveRowUp (JList jList) {
        int selRows [] = jList.getSelectedIndices();
        if (selRows!=null && selRows.length>0) {
        	DefaultListModel model = (DefaultListModel)jList.getModel();
            ListSelectionModel selModel = jList.getSelectionModel();
            Object aux = null;
            for (int i=0;i<selRows.length;i++) {
                if (selRows[i]>0 && !selModel.isSelectedIndex(selRows[i]-1)) {
                    aux = model.getElementAt(selRows[i]-1);
                    model.setElementAt(model.getElementAt(selRows[i]), selRows[i]-1);
                    model.setElementAt(aux, selRows[i]);
                	selModel.addSelectionInterval(selRows[i]-1, selRows[i]-1);
                	selModel.removeSelectionInterval(selRows[i], selRows[i]);
                }
            }
        }
    }

    private void moveRowDown (JList jList) {
        int selRows [] = jList.getSelectedIndices();
        if (selRows!=null && selRows.length>0) {
        	DefaultListModel model = (DefaultListModel)jList.getModel();
            ListSelectionModel selModel = jList.getSelectionModel();
            Object aux = null;
            for (int i=selRows.length-1;i>=0;i--) {
                if (selRows[i]<(model.getSize()-1) && !selModel.isSelectedIndex(selRows[i]+1)) {
                    aux = model.getElementAt(selRows[i]+1);
                    model.setElementAt(model.getElementAt(selRows[i]), selRows[i]+1);
                    model.setElementAt(aux, selRows[i]);
                	selModel.addSelectionInterval(selRows[i]+1, selRows[i]+1);
                	selModel.removeSelectionInterval(selRows[i], selRows[i]);
                }
            }
        }
    }

	private void setIconButtonStyle(JButton jButton) {
		jButton.setPreferredSize(new Dimension(30,30));
		jButton.setBorder(BorderFactory.createEmptyBorder());
		jButton.setContentAreaFilled(false);
//		jButton.setFocusPainted(false);
	}
	
	private ImageIcon scaleImage(Image image, int size) {
		ImageScaler imageScaler = new ImageScaler(image);
		imageScaler.createScaledImage(size, ImageScaler.ScalingDirection.HORIZONTAL);
		return imageScaler.getScaledImage();
	}

	private ImageIcon getScaledImage(String name, int size) {
		return scaleImage(getImage(name),size);
	}

	private Image getImage(String name) {
		Image image	= null;
		ClassLoader cl = this.getClass().getClassLoader();
		if ((name!=null)&&(cl!=null)) {
			image = Toolkit.getDefaultToolkit().createImage(cl.getResource(name));
		}
		return image;
	}	

	private void showError(String error) {
		if (error != null) {
			JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean showConfirmation(String message) {
		boolean result = false;
		if (message != null) {
			int response = JOptionPane.showConfirmDialog(this, message, "Aviso", JOptionPane.OK_CANCEL_OPTION);
			if (response==JOptionPane.OK_OPTION) {
				result = true;
			}
		}
		return result;
	}

	private String removeExcludedWords(String str,String[] strsExcluded) {
		
		if(str!=null && !str.isEmpty() && strsExcluded!=null && strsExcluded.length>0) {
			for (String strExcluded:strsExcluded) {
				str = str.replaceAll(strExcluded, "");
			}
		}
		return str;
	}
	
	private void test() {
		try {
			southPanel.removeAll();
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			southPanel.setLayout(gbl);

			FindResult findResult = new FindResult();
			Image image = ImageIO.read(new File(outputDir + FileSystems.getDefault().getSeparator() + "test.jpg"));
			findResult.setThumbnailImage((BufferedImage)image);
			JImagePanel jImagePanel = new JImagePanel(findResult, 100, new Dimension(400,600));
			jImagePanel.fitToViewer();
			jImagePanel.setPreferredSize(new Dimension(200,300));
			gbc =  new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0);
			southPanel.add(jImagePanel,gbc);
			
//			findResult = new FindResult();
//			image = ImageIO.read(new File(outputDir + FileSystems.getDefault().getSeparator() + "Bully(2).jpg"));
//			findResult.setThumbnailImage((BufferedImage)image);
//			jImagePanel = new JImagePanel(findResult, 100, new Dimension(400,600));
//			jImagePanel.fitToViewer();
//			jImagePanel.setPreferredSize(new Dimension(200,300));
//			gbc =  new GridBagConstraints(1,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0);
//			southPanel.add(jImagePanel,gbc);
			
			southPanel.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private ArrayList<FindResult> findImage(String question, String questionMods, String ua, int numResults) throws Exception {
//		
//		ArrayList<FindResult> results = null;
//		FindImageJSoup fij = new FindImageJSoup();
//       	System.out.println("question:"+ question);
//       	results = fij.findImage(question,questionMods,ua,numResults);
//			
//        return results;
//	}
	

	@Override
	public JTextComponent getLoggerComponent() {
		return textArea;
	}


	public static void main(String[] args) {
		System.out.println("Launching Gui");
		Gui gui = new Gui();
	}
	
	
    private void initParams() {
        try {
            Properties prop = readConfig();
            outputDir = prop.get(PARAMCOMFIGFILE).toString();
            tempDir = prop.get(PARAMTEMPDIR).toString();
            userAgent = prop.get(PARAMUSERAGENT).toString();
            excludedWords = Util.readPropsStrArray(prop,PARAMEXCLUDEDWORDS);
            searchMods = prop.get(PARAMSEARCHMODDS).toString();
            targetDir = prop.getProperty(PARAMTARGETDIR).toString();
            proxyHost = prop.getProperty(PARAMPROXYHOST).toString();
            proxyPort = prop.getProperty(PARAMPROXYPORT).toString();

            logger.info("-------------------------------------------------------- Configuration params --------------------------------------------------------",this);
            logger.info("outputDir=" + outputDir,this);
            logger.info("tempDir=" + tempDir,this);
            logger.info("userAgent=" + userAgent,this);
            logger.info("excludedWords=" + Util.arrayToString(excludedWords),this);
            logger.info("searchMods=" + searchMods,this);
            logger.info("baseDir=" + targetDir,this);
            logger.info("proxyHost=" + proxyHost,this);
            logger.info("proxyPort=" + proxyPort,this);
            logger.info("--------------------------------------------------------------------------------------------------------------------------------------",this);

        } catch (Exception e) {
            logger.error("IOException:" + e.getMessage());
            e.printStackTrace();
        }
    }
 
    private void saveParams() {
        try {
            Properties prop = new Properties();
			
    		prop.setProperty(PARAMCOMFIGFILE, jtfOutDir.getText());
    		prop.setProperty(PARAMTEMPDIR, jtfTempDir.getText());
    		prop.setProperty(PARAMUSERAGENT, jtfUserAgent.getText());
    		prop.setProperty(PARAMEXCLUDEDWORDS, jtfExcludedWords.getText());
    		prop.setProperty(PARAMSEARCHMODDS, jtfSearchMods.getText());
    		prop.setProperty(PARAMTARGETDIR, jlTarget.getText());
    		// TODO
//    		prop.setProperty(PARAMPROXYHOST, null);
//    		prop.setProperty(PARAMPROXYPORT, null);

    		prop.store(new FileOutputStream(CONFIG_FILE),null);
        } catch (Exception e) {
            logger.error("IOException:" + e.getMessage());
            e.printStackTrace();
        }

    }

    private static Properties readConfig() throws IOException {
        Properties prop = new Properties();
        InputStream is = null;

        try {
            is = new FileInputStream(CONFIG_FILE);
            prop.load(is);
        }
        catch (IOException ioe) {
            logger.error("Error leyendo el fichero de configuracion '" + CONFIG_FILE + "'");
            throw ioe;
        }
        return prop;
    }
    
    
	////////// JImagePanelListener interface ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void saveEventReceived(JImagePanelEvent event) {
		if (event!=null) {
			FindResult source = (FindResult)event.getSource();
			System.out.println("Image saved '"+source.getName()+"'");
			if (activeMode!=null && activeMode.equals(MODE_MANUAL))
				processNextManualItem();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
