package pt.iscte.dcti.instrumentation.model;

import java.lang.reflect.Modifier;

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
/**
 * Represents a filed.
* @author Carlos Correia
* @author Rute Oliveira
* @version 1.0
* @created 06-Nov-2009 15:52:06
*/
public class Field {

	private String _type;
	private String _name;
	private String _value;
	private int _modifiers;
	private Instance _instance;
	
	public Field(String name, String value, String type, int modifiers, Instance instance)
	{
		setName(name);
		setValue(value);
		setModifiers(modifiers);
		setType(type);
		setInstance(instance);
	}
	
	/**
	 * Verifies if the type of the field is one of the primitive types.
	 * @return true if it is a primitive type, false otherwise
	 */
	public boolean isPrimitiveType()
	{
		String[] primitiveTypes = {"int","String","boolean","char","long","double","float","Integer","Boolean","Character","Long"};
		int indexOfPoint = getType().lastIndexOf(".");
		int indexOfSpace = getType().lastIndexOf(" ");
		String type = "";
		if(indexOfPoint!=-1)		
			type = getType().substring(indexOfPoint+1);		
		else if(indexOfSpace!=-1)
			type = getType().substring(indexOfSpace+1); 		
		else
			type = getType();
		for (String primitiveType : primitiveTypes) {
			if(type.equals(primitiveType))
				return true;
		}
		return false;
	}
	
	public void setName(String _name) {
		this._name = _name;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setValue(String _value) {
		this._value = _value;
	}
	
	public String getValue() {
		return _value;
	}

	private void setModifiers(int _modifiers) {
		this._modifiers = _modifiers;
	}

	private int getModifiers() {
		return _modifiers;
	}
	
	/**
	 * Constructs the image name of the field based on its access type and if it static or not.
	 * @return string with the image name
	 */
	public String getImageName()
	{
		String imageName = "";
		if(this.isPublic())
			imageName += "Public";
		else if(this.isPrivate())
			imageName += "Private";
		else if(this.isProtected())
			imageName += "Protected";
		if(this.isStatic())
			imageName += "Static";
		imageName += "Field";
		return imageName;
	}
	
	public boolean isPublic(){
		return Modifier.isPublic(getModifiers());
	}
	
	public boolean isPrivate(){
		return Modifier.isPrivate(getModifiers());
	}
	
	public boolean isProtected()
	{
		return Modifier.isProtected(getModifiers());
	}
	
	public boolean isStatic()
	{
		return Modifier.isStatic(getModifiers());
	}

	public void setType(String _type) {
		this._type = _type;
	}

	public String getType() {
		return _type;
	}
	
	/**
	 * Constructs the description of the filed with its current value.
	 * @return description of the field and its current value
	 */
	public String toString()
	{
		return getType()+" "+getName()+" = "+getValue();
	}

	public void setInstance(Instance _instance) {
		this._instance = _instance;
	}

	public Instance getInstance() {
		return _instance;
	}
	
	
	/**
	 * Verifies if the given name and value of the given field is equal to its name and value.
	 * @param object field to compare
	 * @return true if the two fields are equal, false otherwise
	 */
	public boolean equals(Object object)
	{
		boolean isEqual = false;
		if(object instanceof Field)
		{
			Field field = (Field)object;
			if(field.getName().equals(this.getName()) && field.getValue().equals(this.getValue()))
				isEqual = true;
		}
		return isEqual;
	}
}
