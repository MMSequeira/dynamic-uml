package pt.iscte.dcti.instrumentation.model;
import java.util.Vector;

import pt.iscte.dcti.visual_tracer.patterns.IObserver;
import pt.iscte.dcti.visual_tracer.patterns.ISubject;

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
 * @created 06-Nov-2009 15:52:06
 */
public abstract class IModel implements ISubject {

	private Vector<IObserver> _observers  = new Vector<IObserver>();
	
	public enum Status
	{
		Playing,
		Paused,
		UserExit
	}	

	public IModel(){

	}	
	
	@Override
	public void registerObserver(IObserver observer) {
		this.getObservers().add(observer);		
	}

	@Override
	public void removeObsserver(IObserver observer) {
		this.getObservers().remove(observer);
	}
	
	@Override
	public void notifyThreadFlowAdded(ThreadFlow threadFlow) {
		if(!getStatus().equals(Status.UserExit))
			for (IObserver observer : getObservers())
				observer.threadFlowAdded(threadFlow);		
	}
	
	@Override
	public void notifyAbstractJoinPointAdded(AbstractJoinPoint abstractJoinPoint) {
		if(!getStatus().equals(Status.UserExit))
			for (IObserver observer : getObservers())
				observer.abstractJoinPointAdded(abstractJoinPoint);			
	}
	
	@Override
	public void notifyTimeChanged(int time)
	{
		for (IObserver observer : getObservers())
			observer.timeChanged(time);		
	}
	
	/**
	 * Asks the observer to notify the subscribed views that the status changed.
	 * @param execution status set
	 */
	public void notifyStatusChanged(IModel.Status status)
	{		
		for (IObserver observer : getObservers())
			observer.statusChanged(status);
	}
	
	private Vector<IObserver> getObservers() {
		return _observers;
	}
	
	/**
	 * Gets the list of the existent thread flows.
	 * @return vector with thread flows
	 */
	public abstract Vector<ThreadFlow> getThreadFlowList();
	
	/**
	 * Gets the list of the existent sintatic structures.
	 * @return vector with the sintatic structures
	 */
	public abstract Vector<SintaticStructure> getSintaticStructureList();
	
	/**
	 * Gets the thread flow by the thread name.
	 * @param threadName name of the thread to search for the correspodent thread flow
	 * @return wanted thread flow
	 */
	public abstract ThreadFlow getThreadFlow(String threadName);
	
	/**
	 * Verifies if the thread flow exists by the thread name.
	 * @param threadName name of the thread
	 * @return true if the thread flow already exists, false otherwise
	 */
	public abstract boolean existsThreadFlow(String threadName);
	
	/**
	 * Adds a thread flow to the list.
	 * @param threadFlow thread flow to add
	 */
	public abstract void addThreadFlow(ThreadFlow threadFlow);
	
	/**
	 * Gets the representation of an instance.
	 * @param ref weak reference of the instance
	 * @return representation of the instance
	 */
	public abstract Instance getInstance(MyWeakReference ref);
	
	/**
	 * Verifies if a given representation of instance exists.
	 * @param ref weak reference of the instance
	 * @return true if the representation of the instance exists, false otherwise
	 */
	public abstract boolean existsInstance(MyWeakReference ref);
	
	/**
	 * Adds a representation of the instance to the list.
	 * @param instance representation of instance to add to the list
	 */
	public abstract void addInstance(Instance instance);
	
	/**
	 * Gets the wanted sintatic structure by its signature.
	 * @param sintaticSignature signature of the wanted sintatic structure
	 * @return sintatic structure correspondent to the signature
	 */
	public abstract SintaticStructure getSintaticStructure(String sintaticSignature);
	
	/**
	 * Verifies if a given sintatic structure exists in the list.
	 * @param sintaticSignature sintatic structure to find
	 * @return true if the sintatic structure exists on the list, false otherwise
	 */
	public abstract boolean existsSintaticStructure(String sintaticSignature);
	
	/**
	 * Adds a sintatic structure to the list.
	 * @param sintaticStructure sintatic structure to add
	 */
	public abstract void addSintaticStructure(SintaticStructure sintaticStructure);

	/**
	 * Sets the speed at which the application will be executed.
	 * @param _waitTime execution speed
	 */
	public abstract void setWaitTime(int _waitTime) ;

	/**
	 * Gets the speed at which the application is being executed.
	 * @return current execution speed
	 */
	public abstract int getWaitTime() ;

	/**
	 * Sets the execution status of the application.
	 * @param _status execution status (paused or playing)
	 */
	public abstract void setStatus(Status _status);

	/**
	 * Gets the execution status of the application.
	 * @return execution status (paused or playing)
	 */
	public abstract Status getStatus();
	
}