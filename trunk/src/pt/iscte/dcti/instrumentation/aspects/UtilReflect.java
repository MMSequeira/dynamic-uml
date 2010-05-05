package pt.iscte.dcti.instrumentation.aspects;

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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;


public class UtilReflect {

	public static String getClassName(Object object)
	{
		if(object!=null)
			return object.getClass().getName();
		return "";
	}
	
	//return non-static fields from an instance
	public static Field[] getInstanceFields(Object object)
	{
		Field[] fields = null;
		if(object!=null)
			fields = object.getClass().getDeclaredFields().clone();
		return fields;		
	}
	
	//return non-static fields from an instance
	public static Field[] getClassFields(String fullClassName)
	{
		Field[] fields = null;
		if(fullClassName!=null)
			try {
				fields = Class.forName(fullClassName).getDeclaredFields().clone();
			} catch (Exception e) {
				// TODO This seems like a really bad idea! --mms
				e.printStackTrace();
			}
		return fields;		
	}
	
	//return non-static fields from an instance
	public static Field[] getClassFields(Class<?> clazz)
	{
		Field[] fields = null;
		if(clazz!=null)
			try {
				fields = clazz.getDeclaredFields().clone();
			} catch (Exception e) {
                // TODO This seems like a really bad idea! --mms
				e.printStackTrace();
			}
		return fields;		
	}
	
	public static Object getInstanceFieldValue(Field field, Object object)
	{
		if(field!=null && object!=null && !isJoinPointField(field.getName()))
		{
			field.setAccessible(true);
			try {
				return field.get(object);
			} catch (Exception e) {
				// we can't see this field
			}
		}
		return null;
	}
	
	public static Object getClassFieldValue(Field field, String fullClassName)
	{
		if(field!=null && fullClassName!=null)
		{
			field.setAccessible(true);
			try {
				return field.get(Class.forName(fullClassName).newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String getAccessType(int access)
	{
		/*String accessType="";
		switch (access) {		
		case Modifier.PRIVATE:
			accessType = "private";
			break;
		case Modifier.PROTECTED:
			accessType = "protected";
			break;			
		default:
			accessType = "public";
			break;
		}*/
		if (Modifier.isPrivate(access))
			return "private";
		else if ( Modifier.isProtected(access))
			return "protected";		
		else
			return "public";
	}
	
	public static boolean isStatic(int codeModifier)
	{
		return Modifier.isStatic(codeModifier);
	}
	
	public static Vector<String> getMethodArgumentTypes(Class<?> clazz, String methodName)
	{
		return null;
	}
	
	private static boolean isJoinPointField(String fieldName)
	{
		return fieldName.indexOf("ajc$") != -1;
	}
}
