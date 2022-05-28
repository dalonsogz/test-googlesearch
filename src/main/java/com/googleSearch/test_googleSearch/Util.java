package com.googleSearch.test_googleSearch;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FilenameUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Yokese
 * Date: 27-feb-2005
 * Time: 15:49:53
 *
 * Class Util version 1.0
 */
public class Util
{
    public static void writeFile(String path, List list, boolean append) throws IOException
    {
        PrintWriter bfw = null;
        try
        {
            bfw = new PrintWriter(new BufferedWriter(new FileWriter(path, append)));
            Iterator it = list.iterator();
            while (it.hasNext())
            {
                bfw.println(it.next().toString());
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            bfw.close();
        }
    }

   public static void writeFile(String path, String data, boolean append) throws IOException
    {
        PrintWriter bfw = null;
        try
        {
            bfw = new PrintWriter(new BufferedWriter(new FileWriter(path,append)));
            bfw.println(data);
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (bfw != null)
            {
                bfw.close();
            }
        }
    }

    public static String readFile(String path) throws IOException
    {
        String fileContent = new String();

        try
        {
            BufferedReader in =
                    new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = in.readLine()) != null)
            {
                fileContent += line + "\n";
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        return fileContent;
    }

    public static List<String> readFileToList(String path) throws IOException,FileNotFoundException
    {
        List<String> fileContent = new ArrayList<String>();
        try
        {
            BufferedReader in =
                    new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = in.readLine()) != null)
            {
                fileContent.add(line);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        return fileContent;
    }

    public static void viewUrlInfo(URL url)
    {
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Protocol:" + url.getProtocol());
        System.out.println("Host:" + url.getHost());
        System.out.println("Authority:" + url.getAuthority());
        System.out.println("UserInfo:" + url.getUserInfo());
        System.out.println("Path:" + url.getPath());
        System.out.println("File:" + url.getFile());
        System.out.println("Port:" + url.getPort());
        System.out.println("Query:" + url.getQuery());
        System.out.println("Reference:" + url.getRef());
        System.out.println("-------------------------------------------------------------------------------------");
    }

    /**
     * Metodo para eliminar elementos duplicados de una lista
     * sin importar el orden
     * @param list la lista de elementos
     */
    public static void removeDuplicates(ArrayList list) {
        Set set = new HashSet();
        set.addAll(list);

        if(set.size() < list.size()) {
            list.clear();
            list.addAll(set);
        }
    }

    /**
     * Metodo para eliminar elementos duplicados de una lista de strings
     * @param list la lista de strings
     */
    public static void removeSortedStringDuplicates(ArrayList list) {
        for (int i=0; i<list.size(); i++) {
            String cad = list.get(i).toString();
            for (int j=(i+1); j<list.size(); j++) {
                String cadTemp = list.get(j).toString();
                if (cad.equals(cadTemp)) {
                    list.remove(j);
                    if (i==j) i--;
                }
            }
        }
    }

    public static String replace (String str, String cad1, String cad2) {
        if (str.indexOf(cad1)!=-1) {
            str = str.substring(0,str.indexOf(cad1)) + cad2 + str.substring(str.indexOf(cad1) + cad1.length());
            str = replace(str,cad1,cad2);
        }
        return str;
    }

    public static String urlEncode (String urlStr) throws MalformedURLException, UnsupportedEncodingException{
        if (urlStr == null) return null;
        StringBuffer formatedUrl = new StringBuffer();
        URL auxURL = new URL(urlStr);

        formatedUrl.append(auxURL.getProtocol());
        formatedUrl.append("://");
        formatedUrl.append(auxURL.getHost());
        formatedUrl.append(auxURL.getPath());

        if (auxURL.getQuery()!=null && !auxURL.getQuery().equals("")) {
            formatedUrl.append("?");
            String[] params = auxURL.getQuery().split("&");
            for (int i=0; i<params.length; i++) {
                formatedUrl.append(URLEncoder.encode(params[i].substring(0,params[i].indexOf("=")),"UTF-8"));
                formatedUrl.append("=");
                formatedUrl.append(URLEncoder.encode(params[i].substring(params[i].indexOf("=")+1),"UTF-8"));
                if (i!=(params.length-1)) formatedUrl.append("&");
            }
        }
        return formatedUrl.toString();
    }
    
	/**
	 * Method to save a serializeble object to a file
	 * 
	 * @param object
	 *            object to seialize
	 * @param filename
	 *            file name
	 * @throws IOException
	 *             I/O Exception
	 */
	public static void writeSerializaedObject(Serializable object, String filename) throws IOException {
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream(new FileOutputStream(filename));

			out.writeObject(object);
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException exception) {
				}
			}
		}
	}

	
	/**
	 * Method to read a serialized object from a file
	 * 
	 * @param filename
	 *            file name to read from
	 * @return the readed object
	 * @throws ClassNotFoundException
	 *             class not found exception
	 * @throws IOException
	 *             I/O Exception
	 */
	public static Object readSerializedObject(String filename) throws ClassNotFoundException,
			IOException {
		ObjectInputStream in = null;

		try {
			in = new ObjectInputStream(new FileInputStream(filename));

			return in.readObject();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException exception) {
				}
			}
		}
	}


    public static boolean isDoneBefore(String listaHechoFile, String cad) throws IOException {
        try {
            List<String> lista = readFileToList(listaHechoFile);
            if (lista.contains(cad)) {
                return true;
            }
            return false;
        } catch (FileNotFoundException fnf) {
            return false;
        }
    }

   
    public static String downloadURL (String urlStr, String destDir, String destName, String userAgent) throws MalformedURLException,IOException {
    	String destination = new String();
    	FileOutputStream fos = null;
    	ReadableByteChannel rbc = null;
    	try {
	    	URL url = new URL(urlStr);
//    		String urlEncoded = URLEncoder.encode(urlStr,"UTF-8");
//	    	URL website = new URL(urlEncoded);
	    	
	    	if (destDir!=null)	destination+=destDir;
	    	if (!destination.isEmpty()) {
	    		if (!destination.endsWith(FileSystems.getDefault().getSeparator())) {
	    			destination+=FileSystems.getDefault().getSeparator();
	    		}
	    	}
	    	if (destName!=null) {
	    		destination+=destName+"."+FilenameUtils.getExtension(url.getPath());
	    	} else {
	    		destination+=FilenameUtils.getName(url.getPath());
	    	}
	    	
	    	File destinationFile = new File(destination);
	    	int count = 1;
	    	String destinationAux = destination;
	    	while (destinationFile.exists()) {
	    		destinationAux=FilenameUtils.getFullPath(destination)+FilenameUtils.getBaseName(destination)+"("+(count++)+")."+FilenameUtils.getExtension(destination);
	    		destinationFile = new File(destinationAux);
	    	}
	    	destination=destinationAux;
	    	
	    	if (userAgent!=null) {
//		        HttpURLConnection con=(HttpURLConnection)(url.openConnection());
//		        System.setProperty("http.agent","");
//		        con.setRequestProperty("User-Agent",userAgent);
		        
	    		HttpURLConnection con=(HttpURLConnection)url.openConnection();
		        con.setRequestProperty("User-Agent",userAgent);
		    	rbc = Channels.newChannel(con.getInputStream());
		    	fos = new FileOutputStream(destination);
		    	fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	    	} else {
		    	rbc = Channels.newChannel(url.openStream());
		    	fos = new FileOutputStream(destination);
		    	fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	    	}
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		destination = null;
    	} finally {
    		if (fos!=null)
    			fos.close();
    		if (rbc!=null)
    			rbc.close();
    	}
    	
    	return destination;
    }
  
    public static ByteArrayOutputStream downloadURL (String urlStr, String userAgent) throws MalformedURLException,IOException {
    	InputStream is = null;
    	ByteArrayOutputStream bos = null;
    	try {
	    	URL url = new URL(urlStr);
	    	
    		HttpURLConnection con=(HttpURLConnection)url.openConnection();
	    	if (userAgent!=null) {
		        con.setRequestProperty("User-Agent",userAgent);
	    	}
	    	
	    	is = con.getInputStream();
	    	
	    	if (con.getResponseCode()==HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode()==HttpURLConnection.HTTP_MOVED_TEMP) {
	    		if (is!=null)
	    			is.close();
	    		return downloadURL (con.getHeaderField("Location"), userAgent);
	    	}
	    	
	        bos = new ByteArrayOutputStream();
	        int len;
	        byte[] buffer = new byte[4096];
	        while (-1 != (len = is.read(buffer))) {
	          bos.write(buffer, 0, len);
	        }
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (bos!=null)
    			bos.close();
    		if (is!=null)
    			is.close();
    	}
    	return bos;
    }

	public static class ExtensionsFilter implements FileFilter {
		private char[][] extensions;

		public ExtensionsFilter(String[] extensions) {
			int length = extensions.length;
			this.extensions = new char[length][];
			for (String s : extensions) {
				this.extensions[--length] = s.toCharArray();
			}
		}

		public boolean accept(File file) {
			char[] path = file.getPath().toCharArray();
			for (char[] extension : extensions) {
				if (extension.length > path.length) {
					continue;
				}
				int pStart = path.length - 1;
				int eStart = extension.length - 1;
				boolean success = true;
				for (int i = 0; i <= eStart; i++) {
					if ((path[pStart - i] | 0x20) != (extension[eStart - i] | 0x20)) {
						success = false;
						break;
					}
				}
				if (success)
					return true;
			}
			return false;
		}
	}
	

	/**
	 * Metodo para imprimir todos los datos de un objeto y de los objetos que contenga. Util para la comparacion
	 * del contenido de dos objetos de la misma clase. El contenido de los subobjetos se va tabulando para mejor comprension.
	 * 
	 * @param title nombre del objeto base, no tiene mayor relevancia salvo ponerlo en el printado
	 * @param obj objeto base a imprimir
	 * @param level nivel inicial de tabulado, poner a null para un comportamiento estandar
	 */
	public static void printMethods(String title,Object obj,Integer level) {
		Hashtable<String,Object> objects = new Hashtable<String,Object>();
		objects.put(title,obj);
		Util.printMethods(objects,level,null);
	}

	public static void printMethods(Hashtable<String,Object> objects,Integer level,ArrayList<Integer> objectsPrinted) {

		if (objects==null)
			return;
		
		Hashtable<String,Object> totalObjectsToPrint = null;
		Hashtable<String,Object> objectsToPrint = null;

		ArrayList<String> keyList = new ArrayList<String>(objects.keySet());
		Collections.sort(keyList);
		
		for (String key:keyList) {

			Object obj=objects.get(key);
			Integer objIdentityHashCode = System.identityHashCode(obj);

			try {

				if (objectsPrinted!=null && objectsPrinted.contains(objIdentityHashCode)) {
					log ("*****************************  (" + objIdentityHashCode + ")" + key + "  ************************ [->]",level);
					continue;
				} else if (objectsPrinted==null) {
					objectsPrinted = new ArrayList<Integer>();
				}
				objectsPrinted.add(objIdentityHashCode);				
				
				if (level==null || level.intValue()<0)
					level = new Integer(0);

				log ("\n");
				log ("*****************************  (" + objIdentityHashCode + ")" + key + "  *****************************",level);
					
				Class objClass = obj.getClass();
				Method[] methods = objClass.getMethods();
				Class[] methodParams = null;
				objectsToPrint = new Hashtable<String,Object>();
	
				// Ordenacion por nombre de los metodos
				ArrayList<String> methodNames = new ArrayList<String>();
				for (int i=0;i<methods.length;i++) {
	//				log("methods[i].getName():" + methods[i].getName());
					methodParams = methods[i].getParameterTypes();
					if (methods[i].getName().startsWith("get") && methodParams.length==0 && 
							!methods[i].getName().equals("getClass")) {
						methodNames.add(methods[i].getName());
					}
				}
				String[] methodNamesOrdered = new String[methodNames.size()];
				for (int i=0;i<methodNames.size();i++) {
					methodNamesOrdered[i]=methodNames.get(i);
				}
				Arrays.sort(methodNamesOrdered);
				Method[] methodsOrdered = new Method[methodNamesOrdered.length];
				for (int i=0;i<methodNamesOrdered.length;i++) {
					for (int j=0;j<methods.length;j++) {
						if (methods[j].getName().equals(methodNamesOrdered[i])) {
							methodsOrdered[i]=methods[j];
							break;
						}
					}
				}
				
				// Printado de los metodos y valores
				int count = 1;
				Object value = null;
				for (int i=0;i<methodsOrdered.length;i++) {
					methodParams = methodsOrdered[i].getParameterTypes();
					if (methodsOrdered[i].getName().startsWith("get") && methodParams.length==0 
							&& !methodsOrdered[i].getName().equals("getClass")) {
						try {
							value = methodsOrdered[i].invoke(obj,null);
							if (value!=null && ((value.toString()!=null && value.toString().indexOf('@')!=-1) || value.toString()==null) ) {
								if (objectsToPrint==null)
									objectsToPrint = new Hashtable<String,Object>();

								if (value instanceof List || value instanceof Collection || value instanceof Iterable || value instanceof Object[]) {
									Iterator<Object> it = null;
									String name = null;
									if (value instanceof List) {
										List list = (List)value;
										it = ((Iterable)list).iterator();
										name = "List";
									} else if (value instanceof Collection) {
										Collection<Object> col = (Collection)value;
										it = ((Iterable)col).iterator();
										name = "Collection";
									} else if (value instanceof Iterable) {
										it = ((Iterable)value).iterator();
										name = "Iterable";
									} else if (value instanceof Object[]) {
										ArrayList list = new ArrayList(Arrays.asList((Object[])value));
										it = ((Iterable)list).iterator();
										name = "Object[]";
									}
									Object val = null;
									if (name==null) {
										name = value.getClass().getName();
									}
									int countVal = 0;
									while(it.hasNext()) {
										val = it.next();
										objectsToPrint.put(key + "_" + methodsOrdered[i].getName()+"("+name+"_"+(countVal++)+")",val);
									}
								} else {
									objectsToPrint.put(key + "_" + methodsOrdered[i].getName()+"("+value.getClass().getName()+")",value);
								}
							}
							
//							if (value!=null)
								log((count++)+")'" + methodsOrdered[i].getName() + "' = '" + 
										(value==null?value:((value.toString()!=null && 
										value.toString().indexOf('@')!=-1)?
												(value.toString().substring(0,value.toString().indexOf('@'))+"@---" ):value)) + "'",level);
						} catch (IllegalAccessException iae) {
							log("Error accessing '" + methodsOrdered[i].getName() + "'");
							iae.printStackTrace();
						} catch (InvocationTargetException ite) {
							log("Error invocating '" + methodsOrdered[i].getName() + "'");
							ite.printStackTrace();
						} catch (Exception e) {
							log("Exception calling method '" + methodsOrdered[i].getName() + "'");
							e.printStackTrace();
						} catch (Throwable t) {
							log("(Throwable)Exception calling method '" + methodsOrdered[i].getName() + "'");
							t.printStackTrace();
						}
					}
				}
				
				if (objectsToPrint!=null) {
					if (totalObjectsToPrint==null)
						totalObjectsToPrint = new Hashtable<String,Object>();
					Enumeration keys = objectsToPrint.keys();
					String keyAux = null;
					while(keys.hasMoreElements()) {
						keyAux = (String)keys.nextElement();
						totalObjectsToPrint.put(keyAux,objectsToPrint.get(keyAux));
					}
				}
				
				if (objectsPrinted==null) {
					objectsPrinted = new ArrayList<Integer>();
					objectsPrinted.add(objIdentityHashCode);
				}
			} catch (Exception t) {
				log("Unexpected exception:" + t.getMessage());
				t.printStackTrace();
			} catch (Throwable t) {
				log("(Throwable)Unexpected exception:" + t.getMessage());
				t.printStackTrace();
			}
		}

		if (totalObjectsToPrint!=null) {
			ArrayList<String> keysAux = new ArrayList<String>(totalObjectsToPrint.keySet());
			Collections.sort(keysAux);

			log("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			Object obj = null;
			for (String keyAux:keysAux) {
				obj = totalObjectsToPrint.get(keyAux);
				log("'(" + System.identityHashCode(obj) + ")" + keyAux + "' - '" + obj + "'");
			}
			log("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		}

		printMethods(totalObjectsToPrint,++level,objectsPrinted);

	}

	public static ArrayList<String> readPropsStrList(Properties prop, String listName) throws Exception {
		ArrayList<String> result=null;
		
		String strList = prop.getProperty(listName);
		if (strList!=null) {
			result = new ArrayList<String>();
		    StringTokenizer st = new StringTokenizer(strList,",");
		    while (st.hasMoreTokens()) {
		         result.add(st.nextToken());
		    } 
		}
		return result;
	}

	public static String[] readPropsStrArray(Properties prop, String listName) throws Exception {
		ArrayList<String> resultList=readPropsStrList(prop,listName);
		String[] result = null;
		if (resultList!=null && resultList.size()>0) {
			result = resultList.toArray(new String[resultList.size()]);
		}
		return result;
	}

	private static void log(Object text) {
		if (text!=null)
			log(text,0);
	}
	private static void log(Object text, int tab) {
		if (text!=null) {
			String tabs = "";
			for (int i=0;i<tab;i++) {
				tabs+="\t";
			}
			String txt = tabs+text;
			System.out.println(txt);
//			try {
//				Util.writeFile("C:\\work\\objFile.txt",txt,true);
//			} catch (IOException ioe) {
//				ioe.printStackTrace();
//			}
		}
	}

	public static String arrayToString(String[] excludedWords) {
        String excludedWordsStr = new String();
        if (excludedWords!=null)
        	for (String excludedWord:excludedWords) excludedWordsStr+=excludedWord+",";
		return excludedWordsStr;
	}

	public static String[] stringToArray(String strList) throws Exception {
		ArrayList<String> resultList=null;
		if (strList!=null) {
			resultList = new ArrayList<String>();
		    StringTokenizer st = new StringTokenizer(strList,",");
		    while (st.hasMoreTokens()) {
		         resultList.add(st.nextToken());
		    } 
		}
		String[] result = null;
		if (resultList!=null) {
			result = resultList.toArray(new String[resultList.size()]);
		}
		return result;
	}
	
	public static String md5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	
	////// Images methods /////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static byte[] encodeMD5(byte[] bytes) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] md5 = md.digest(bytes);
	    return md5;
	}
	
	public static String convertToHex(byte[] data) {
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

	public static int unsignedByte(byte b) {
		return (b & 0xFF);
	}
	
	public static String identifyImgFileData (byte[] bytes)
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
	
	
	public static byte[] extractBytes (BufferedImage image) throws IOException {

		 // get DataBufferBytes from Raster
		 WritableRaster raster = image.getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return ( data.getData() );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
