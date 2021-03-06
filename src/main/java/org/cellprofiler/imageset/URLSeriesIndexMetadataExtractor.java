package org.cellprofiler.imageset;

import java.util.*;

/**
 * @author Lee Kamentsky
 *
 * The ImagePlaneMetadataExtractor adds the image plane's series
 * and / or index to the image's metadata.
 * 
 */
public class URLSeriesIndexMetadataExtractor implements MetadataExtractor<ImagePlane> {
	static final public String SERIES_TAG = "Series";
	static final public String INDEX_TAG = "Frame";
	final static public String URL_TAG = "FileLocation";
	
	static final private String ZERO = "0";
	static final private List<String> metadataKeys = 
		Collections.unmodifiableList(Arrays.asList(URL_TAG, SERIES_TAG, INDEX_TAG));
	/**
	 * Construct an extractor of the image plane series and index.
	 * 
	 */
	public URLSeriesIndexMetadataExtractor() {
			
	}
	/* (non-Javadoc)
	 * @see org.cellprofiler.imageset.MetadataExtractor#extract(java.lang.Object)
	 */
	public Map<String, String> extract(ImagePlane source) {
		Map<String, String> result = new HashMap<String, String>(2);
		int series = source.getSeries().getSeries();
		int index = source.getIndex();
		result.put(URL_TAG, StringCache.intern(source.getImageFile().getURI().toString()));
		result.put(SERIES_TAG, (series == 0)? ZERO: StringCache.intern(Integer.toString(series)));
		result.put(INDEX_TAG, (index == 0)? ZERO: StringCache.intern(Integer.toString(index)));
		return result;
	}
	/* (non-Javadoc)
	 * @see org.cellprofiler.imageset.MetadataExtractor#getMetadataKeys()
	 */
	public List<String> getMetadataKeys() {
		return metadataKeys;
	}
	
}
