package com.googleSearch.test_googleSearch;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/******************************************************************************
 * <tt>Interface que debe implementar el panelBase</tt> <br>
 * @Logesta
 * @Copyright (c) 2006
 * @author        RRH
 * @version       1.0
 ******************************************************************************/

public interface PanelBase
{
	public	final	static	int	TESEO		= 0;
	public	final	static	int	SECMONITOR	= 1;
	public	final	static	int	SECURITY	= 2;
	public	final	static	int	SCANNER		= 3;
	public	final	static	int	TNG			= 4;

	public	final	static	int	PRODUCTION	= 0;
	public	final	static	int	TEST		= 1;
	public	final	static	int	DEVELOPER	= 2;

	public	final	static	int	MESSAGETYPE_NONE = 0;
	public	final	static	int	MESSAGETYPE_INFO = 1;
	public	final	static	int	MESSAGETYPE_WARNING = 2;
	public	final	static	int	MESSAGETYPE_ERROR = 3;

	public	final	static	long	MESSAGETYPE_TIME_NONE = 10000L;
	public	final	static	long	MESSAGETYPE_TIME_INFO = 5000L;
	public	final	static	long	MESSAGETYPE_TIME_WARNING = 5000L;
	public	final	static	long	MESSAGETYPE_TIME_ERROR = 10000L;
	public	final	static	long	MESSAGETYPE_TIME_INFO_SHORT = 1000L;

	public	void	setClipboard(Object o);

	public	Object	getClipboard();

	public void setEnvironment(int env);

	public int getEnvironment();

	public String getEnvironmentString();

	public String getGetSessionId();
	
//	public TeseoClient getJTeseoClient();

	public int getInterfaz();

	public void setPerfil(int perfil);

//	public Zmq getZmqContext();

//	public void unregisterZmqListener(ZmqAdapter zmqAdapter);
	
//	public void setUser(User u);

//	public User getUser();

//	public	boolean can(Agency branch, int function);

	public	boolean can(int function);

	public Vector findAllCurrencies();

	public Vector fillAgencies(Vector v);

	public Image getImageIcon(String file);

	public Image resizeImage(Image img, Dimension newDim);

	public Image resizeImage(Image img, Dimension newDim, boolean keepRatio);

	public JFrame getJFrame();
 
	public int getUserType();
 
	public void setEnabled(boolean flag);

	public Object createCustomObject(Object data,int id);

	public Object send(Object data,String method);

	public Object send(String service,String method,Class[] params,Object[] values);
	
	public Object send(String service,String method,Class[] params,Object[] values, int timeout);

	public Object send(String service,String method,Class[] params,Object[] values, int buga, int bugb);
	
	public Object send(String service,String method,Class[] params,Object[] values, int buga, int bugb, int timeout);

//	public void showError(CodifiedException error);

//	public GpsSetup	findGpsSetup(GpsSetup gs);

//	public void showInfo(CodifiedException error);

	public void showStatus(String error);

	public void showStatus(int message_type, String message, long duration);

	public void exit();

//	public void showError(CodifiedException error,boolean mode);

//	public void showInfo(CodifiedException error,boolean mode);

	public boolean isInternalOrFilial();

	public boolean isInternal();

	public boolean isCentro();

	public boolean isTransp();

	public boolean isFilial();

	public boolean isCrAlarm();

//	public CommonServer getCommonServer();

//	public boolean	isCustomerCostCenter(CostCenter costCenter);

	public boolean isAutonomo();

   	public String retrieveUrlTemplateBase();

	public void destroyCache();

	public void addInternalFrame(JInternalFrame jid);

	public void removeInternalDialog(JInternalFrame jid,boolean enable);
	
	public JDesktopPane getJDesktopPane();

	public int startJob(String service,String method,Class[] params,Object[] values, String description, int type);
	
	public int startJob(String service,String method,Class[] params,Object[] values, String description, int type, int buga, int bugb);
	
//	public boolean analyzeKpiResult(Object obj, InvokeLater invokeLater, User user);

	public void syso(String o);

	public void syse(String e);

	public void syse(Throwable t);
	
	public void disableButtons();
	
	public JPanel getFilterPanel();

	public void toggleFilterPanel(int categoryId,boolean selected);
	
	public Dimension getSize();
	
	public void setEnabledSearch(boolean enabled);
	
//	public void saveAppPreferences(AppContainer appContainer);
	
//	public int analyzeTravelResponse(TravelResponse travelResponse);
}
