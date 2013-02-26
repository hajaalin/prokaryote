/**
 * CellProfiler is distributed under the GNU General Public License.
 * See the accompanying file LICENSE for details.
 *
 * Copyright (c) 2003-2009 Massachusetts Institute of Technology
 * Copyright (c) 2009-2013 Broad Institute
 * All rights reserved.
 * 
 * Please see the AUTHORS file for credits.
 * 
 * Website: http://www.cellprofiler.org
 */
package org.cellprofiler.imageset;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.services.OMEXMLService;

import ome.xml.model.OME;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * @author Lee Kamentsky
 * 
 * ImageFile models an image file. Image files have a URL
 * for their retrieval, and OME metadata retrieved from the file.
 *
 */
public class ImageFile {
	private final URL url;
	private String fileName = null;
	private String pathName = null;
	private OME omexml = null;
	
	/**
	 * Construct an image file from a URL
	 * @param url - URL to be used to retrieve the file
	 */
	public ImageFile(URL url) {
		this.url = url;
	}
	
	/**
	 * @return the image file's URL.
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * @return the file name portion of the URL
	 */
	public String getFileName() {
		if (fileName == null) {
			URI uri;
			try {
				uri = url.toURI();
				if (uri.getScheme().equals("file")) {
					File file = new File(uri);
					fileName = file.getName();
				} else {
					String path = uri.getPath();
					fileName = path.substring(path.lastIndexOf("/")+1);
				}
			} catch (URISyntaxException e) {
				Logger.getLogger(getClass()).info(
						"Failed to extract file name from badly formed URL: " + 
						url.toString());
				return null;
			}
		}
		return fileName;
	}
	
	/**
	 * Extract the "path name" from the URL. For file URLs, this is the
	 * path name of the folder containing the file, for others, it is
	 * the portion of the URL preceeding the file name.
	 * 
	 * @return the path name portion of the URL
	 */
	public String getPathName() {
		if (pathName == null) {
			try {
				final URI uri = url.toURI();
				if (uri.getScheme().equals("file")) {
					final File file = new File(uri);
					pathName = file.getParentFile().getAbsolutePath();
				} else {
					String path = uri.getPath();
					int lastSepIndex = path.lastIndexOf("/");
					if (lastSepIndex <= 0) {
						final URI uriOut = new URI(uri.getScheme(), uri.getHost(), null, null);
						pathName = uriOut.toURL().toString();
					} else {
						final URI uriOut = new URI(
								uri.getScheme(), 
								uri.getHost(), 
								path.substring(0, lastSepIndex),
								null);
						pathName = uriOut.toURL().toString();
					}
				}
			} catch (URISyntaxException e) {
				Logger.getLogger(getClass()).info(
						"Failed to extract metadata from badly formed URL: " + 
						url.toString());
				return null;
			} catch (MalformedURLException e) {
				Logger.getLogger(getClass()).warn(
						String.format("Failed to reconstitute path from URL, \"%s\"", url.toString()));
				return null;
			}
		}
		return pathName;
	}
	/**
	 * Set the file's OME XML metadata (e.g. as collected by BioFormats)
	 * 
	 * @param omexml - The OME XML as a string
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws DependencyException 
	 * @throws ServiceException 
	 */
	public void setXMLDocument(String omexml) throws ParserConfigurationException, SAXException, IOException, DependencyException, ServiceException {
		OMEXMLService svc = new ServiceFactory().getInstance(OMEXMLService.class);
		Object root = svc.createOMEXMLRoot(omexml);
		if (! (root instanceof OME))
			throw new ServiceException("Root of XML document wasn't OME");
		this.omexml = (OME)root;
	}

	/**
	 * Set the file's OME XML metadata from a stream
	 * 
	 * @param isOMEXML read the OME XML from this stream
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ServiceException 
	 * @throws DependencyException 
	 */
	public void setXMLDocument(InputStream isOMEXML) throws ParserConfigurationException, SAXException, IOException, DependencyException, ServiceException {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(isOMEXML));
		StringBuilder result = new StringBuilder();
		String sep = System.getProperty("line.separator");
		String line;
		while((line=rdr.readLine())!=null) {
			result.append(line);
			result.append(sep);
		}
		setXMLDocument(result.toString());
	}
	
	/**
	 * Remove the XML document object (e.g. to save space)
	 */
	public void clearXMLDocument() {
		this.omexml = null;
	}
	
	/**
	 * @return the root of the OME document model.
	 */
	public OME getMetadata() {
		return omexml;
	}
	@Override
	public String toString() {
		return String.format("ImageFile: %s", this.url);
	}
}
