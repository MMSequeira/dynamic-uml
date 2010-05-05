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
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:07
 */
public abstract class SintaticStructure {

	public AbstractJoinPoint m_AbstractJoinPoint;
	
	private String _signature;
	private String _name;
	private int _modifiers;
	
	public String getImageName()
	{
		String imageName="";
		if(isPublic())
			imageName+="Public";
		else if(isProtected())
			imageName+="Protected";
		else if(isPrivate())
			imageName+="Private";
		if(isStatic())
			imageName += "Static";
		imageName += getFriendlyName();
		return imageName;
	}
	
	public abstract String getFriendlyName();

	public SintaticStructure(String signature, String name, int modifiers)
	{
		setName(name);
		setSignature(signature);
		setModifiers(modifiers);
	}	
	
	public boolean isPublic()
	{
		return Modifier.isPublic(getModifiers());
	}
	
	public boolean isPrivate()
	{
		return Modifier.isPrivate(getModifiers());
	}
	
	public boolean isStatic()
	{
		return Modifier.isStatic(getModifiers());
	}
	
	public boolean isProtected()
	{
		return Modifier.isProtected(getModifiers());
	}

	public String getSignature(){
		return _signature;
	}
	
	public void setSignature(String signature){
		this._signature = signature;
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getName() {
		return _name;
	}

	private void setModifiers(int _modifiers) {
		this._modifiers = _modifiers;
	}

	private int getModifiers() {
		return _modifiers;
	}

	public abstract String getFriendlySignature();
}