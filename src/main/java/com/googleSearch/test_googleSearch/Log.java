package com.googleSearch.test_googleSearch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log
{
    private static Log sic = null;

    private static boolean instanceFlag = false;

    private static org.apache.log4j.Logger logger = null;

    public static final String CONFIG_FILE = "config.properties";

    private JTextComponent textArea = null;
    
    private Log() {}

    public static Log getInstance()
    {
        if (!instanceFlag)
        {
            return Create();
        }
        else
        {
            return sic;
        }
    }

    private static synchronized Log Create()
    {
        sic = new Log();
        instanceFlag = true;
        logger = initLogger();
        return sic;
    }

    protected void Finalize()
    {
        instanceFlag = false;
    }

    private static org.apache.log4j.Logger initLogger()
    {
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);
        Properties loggerProps = new Properties();
        try
        {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(CONFIG_FILE));
            loggerProps.load(bis);
            bis.close();

        } catch (java.io.FileNotFoundException e)
        {
            System.out.println("Fichero de configuracion '" + CONFIG_FILE + "no econtrado.");
        } catch (java.io.IOException e)
        {
            System.out.println("Error leyendo los parametros de configuracion.");
        }
        PropertyConfigurator.configure(loggerProps);
        logger.setLevel(Level.ALL);
        return logger;
    }

	public void info(String data) {
		logger.info(data);
		addTextArea(data+"\n",textArea);
	}

	public void warn(String data) {
		logger.warn(data);
		addTextArea(data+"\n",textArea);
	}
	
	public void error(String data) {
		logger.error(data);
		addTextArea(data+"\n",textArea);
	}

	public void debug(String data) {
		logger.debug(data);
		addTextArea(data+"\n",textArea);
	}

	public void info(String data,LoggerComponent log) {
		logger.info(data);
		textArea = log.getLoggerComponent();
		addTextArea(data+"\n",textArea);
	}

	public void warn(String data,LoggerComponent log) {
		logger.warn(data);
		textArea = log.getLoggerComponent();
		addTextArea(data+"\n",textArea);
	}
	
	public void error(String data,LoggerComponent log) {
		logger.error(data);
		textArea = log.getLoggerComponent();
		addTextArea(data+"\n",textArea);
	}

	public void debug(String data,LoggerComponent log) {
		logger.debug(data);
		textArea = log.getLoggerComponent();
		addTextArea(data+"\n",textArea);
	}

	public static void addTextArea(String cad,JTextComponent textArea) {
//		System.out.println(cad);
		if (textArea!=null) {
			try { 
			    Document doc = textArea.getDocument();
			    doc.insertString(doc.getLength(),cad, null);
	//			textArea.append(cad);
				textArea.setCaretPosition(textArea.getDocument().getLength());
				textArea.validate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    public Log getLogger()
    {
        return this;
    }
}