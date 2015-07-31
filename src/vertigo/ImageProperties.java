/*
 * Copyright 2015
 *  http://wazza.co.ke
 * 8:28:58 PM  : Jul 28, 2015
 */
package vertigo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kelli
 */
public class ImageProperties {

    private int orientation;
    
    public ImageProperties(){
        orientation = 1;
    }
    
    /* Get orientation of the image */
    public int getImageOrientation(File imageFile){
    
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            Directory dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if(dir!= null){
            orientation = dir.getInt(274);
        }
            Logger.getLogger(ImageProperties.class.getName()).log(Level.INFO, 
                    "orientation flag: {0}",orientation);
        } catch (ImageProcessingException | IOException | MetadataException me) {
            Logger.getLogger(ImageProperties.class.getName()).log(Level.SEVERE,
                    null, "Error occured in extracting metadata");
        }
        return orientation;
    }
}
