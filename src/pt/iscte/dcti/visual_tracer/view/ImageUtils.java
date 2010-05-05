package pt.iscte.dcti.visual_tracer.view;

/**
 * Visual Tracer - An Application of Java Code Instrumentation using AspectJ 
 * Copyright (C) 2010  
 * Carlos Correia - mail.cefc@gmail.com 
 * Rute Oliveira - rute23@gmail.com
 * Manuel Menezes de Sequeira - manuel.sequeira@iscte.pt
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class ImageUtils {

	private static ImageRegistry imageRegistry = new ImageRegistry();
	private static String imageFormat =".png";
	
	public enum ImageName
	{
		//Menu images
		Exit,
		User,
		Group,
		Help,
		//Data images
		PublicClass,
		PublicField,
		PrivateField,
		ProtectedField,
		PublicStaticField,
		PrivateStaticField,
		ProtectedStaticField,
		//Layout Images
		Gnu,
		Clock,
		ClockEdit,
		Page,
		PagePosition,
		Blue,
		DoorOut,
		ControlPlay,
		ControlPause,
		InstanceDetail,
		Thread,
		Eye,
		//Images for Events
		GenericEvent,
		StaticInitialization,
		ConstructorCall,
		ConstructorExecution,
		FieldRead,
		FieldWrite,
		MethodCall,
		MethodExecution
	}
	
	/**
	 * Retrieves the image corresponding to the given file name.
	 * Note that the image is managed by an image registry. You should not
	 * dispose the image after use.
	 * @param shortFileName
	 * @return
	 */
	public static Image getImage(String imageName) {
		Image image = null;		
		String shortFileName = imageName+imageFormat;
		
		File imageFile = new File( "Images/" + shortFileName);
		if(!imageFile.exists())
			return null;
		
		if(imageRegistry.getDescriptor(shortFileName) == null) 
		{
			ImageDescriptor descriptor = ImageDescriptor.createFromFile(null, "Images/" + shortFileName);
			imageRegistry.put(shortFileName, descriptor);
		}
		image = imageRegistry.get(shortFileName);

		return image;
	}
	
}