package pt.iscte.dcti.instrumentation.model;

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
 * This class represents an abstract join point.
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:05
 * 
 */
public abstract class AbstractJoinPoint {

	private Event _eventAfter;
	private Event _eventBefore;
	private Instance _instanceThis;
	private Instance _instanceTarget;
	private SintaticStructure _sintaticStructure;
	private AbstractJoinPoint _parentAbstractJoinPoint;
	private ThreadFlow _threadFlow;
	private Vector<Argument> _arguments = new Vector<Argument>();
	private JoinPointContextInformation _joinPointContextInformation;

	
	public AbstractJoinPoint(Instance instanceThis, Instance instanceTarget, SintaticStructure sintaticStructure, AbstractJoinPoint parent, ThreadFlow threadFlow, Object[] arguments, JoinPointContextInformation joinPointContextInformation){
		setSintaticStructure(sintaticStructure);
		setInstanceThis(instanceThis);
		setInstanceTarget(instanceTarget);
		setParent(parent);
		setThreadFlow(threadFlow);
		setArguments(arguments);
		setJoinPointContextInformation(joinPointContextInformation);
	}

	public void setEventAfter(Event _eventAfter) {
		this._eventAfter = _eventAfter;
	}
	

	public Event getEventAfter() {
		return _eventAfter;
	}

	public void setEventBefore(Event _eventBefore) {
		this._eventBefore = _eventBefore;
	}

	public Event getEventBefore() {
		return _eventBefore;
	}

	public void setSintaticStructure(SintaticStructure _sintaticStructure) {
		this._sintaticStructure = _sintaticStructure;
	}

	public SintaticStructure getSintaticStructure() {
		return _sintaticStructure;
	}


	public void setInstanceThis(Instance _instanceThis) {
		this._instanceThis = _instanceThis;
	}


	public Instance getInstanceThis() {
		return _instanceThis;
	}


	public void setInstanceTarget(Instance _instanceTarget) {
		this._instanceTarget = _instanceTarget;
	}


	public Instance getInstanceTarget() {
		return _instanceTarget;
	}

	/**
	 * Looks for the children of a join point.
	 * @return vector with the children of the extended class
	 */
	public abstract Vector<AbstractJoinPoint> getChilds();
	
	/**
	 * Adds a joinpoint as a child.
	 * @param abstractJoinPoint the join point child to be added
	 */
	public abstract void addChild(AbstractJoinPoint abstractJoinPoint);


	public void setParent(AbstractJoinPoint _parentAbstractJoinPoint) {
		this._parentAbstractJoinPoint = _parentAbstractJoinPoint;
	}

	public AbstractJoinPoint getParent() {
		return _parentAbstractJoinPoint;
	}

	public void setThreadFlow(ThreadFlow _threadFlow) {
		this._threadFlow = _threadFlow;
	}

	public ThreadFlow getThreadFlow() {
		return _threadFlow;
	}

	// TODO Check why this method was defined here. --mms
//	private void addArgument(Argument argument)
//	{
//		getArguments().add(argument);
//	}
	
	public Vector<Argument> getArguments()
	{
		return (Vector<Argument>) _arguments.clone();
	}

	public void setArguments(Object[] arguments) {
		for (Object object : arguments) {
			if(object != null)
				_arguments.add(new Argument(object.toString(), object.getClass().getName()));
			else
				_arguments.add(new Argument("null", ""));
		}
	}
	
	/**
	 * Returns the kind of the join point.
	 * @return string with the kind of the join point
	 */
	public abstract String getKind();
	
	/**
	 * Gets the name of the relative icon to be displayed.
	 * @return string with the icon name
	 */
	public abstract String getImageName();

	public void setJoinPointContextInformation(
			JoinPointContextInformation _joinPointContextInformation) {
		this._joinPointContextInformation = _joinPointContextInformation;
	}

	public JoinPointContextInformation getJoinPointContextInformation() {
		return _joinPointContextInformation;
	}
}