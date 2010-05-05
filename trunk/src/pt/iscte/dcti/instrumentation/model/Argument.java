package pt.iscte.dcti.instrumentation.model;

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
 * Represents the sintatic structure of an argument of a constructor or a method
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:05
 */
public class Argument {

	private String _type;
	private String _value;
	
	public Argument(String value, String type)
	{
		setValue(value);
		setType(type);
	}
	
	public void setValue(String _value) {
		this._value = _value;
	}
	
	public String getValue() {
		return _value;
	}

	public void setType(String _type) {
		this._type = _type;
	}

	public String getType() {
		return _type;
	}
}
