package pt.iscte.dcti.instrumentation.model;

import java.util.Stack;
import java.util.Vector;

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
public class ThreadFlow {
	
	private String _name;
	private String _friendlyName;	
	//this vector will only save the heads of the tree of existing abstractJoinPoints
	private Vector<AbstractJoinPoint> _abstractJoinPoints = new Vector<AbstractJoinPoint>();  
	
	private Stack<AbstractJoinPoint> _stackAbstractJoinPoint = new Stack<AbstractJoinPoint>();

	public ThreadFlow(String name, String friendlyName)
	{
		setName(name);
		setFriendlyName(friendlyName);
	}

	
	public void addAbstractJoinPointHead(AbstractJoinPoint abstractJoinPoint)
	{
		getAbstractJoinPoints().add(abstractJoinPoint);
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getName() {
		return _name;
	}

	public void setFriendlyName(String _friendlyName) {
		this._friendlyName = _friendlyName;
	}

	public String getFriendlyName() {
		return _friendlyName;
	}

	private Vector<AbstractJoinPoint> getAbstractJoinPoints() {
		return _abstractJoinPoints;
	}
	
	public void addToStack(AbstractJoinPoint abstractJoinPoint)
	{
		getStackAbstractJoinPoint().add(abstractJoinPoint);
	}
	
	public AbstractJoinPoint popFromStack()
	{
		return getStackAbstractJoinPoint().pop();
	}
	
	public AbstractJoinPoint lastElementFromStack()
	{
		return getStackAbstractJoinPoint().size() > 0 ? getStackAbstractJoinPoint().lastElement() : null;
	} 
	
	public boolean isAbstrcatJoinPointHead()
	{
		return getStackAbstractJoinPoint().size()==0;
	}


	// TODO Check why this method was defined here --mms
//	private void setStackAbstractJoinPoint(Stack<AbstractJoinPoint> _stackAbstractJoinPoint) {
//		this._stackAbstractJoinPoint = _stackAbstractJoinPoint;
//	}


	private Stack<AbstractJoinPoint> getStackAbstractJoinPoint() {
		return _stackAbstractJoinPoint;
	}
	
	public String toString()
	{
		return getName()+" - "+getFriendlyName();
	}
}