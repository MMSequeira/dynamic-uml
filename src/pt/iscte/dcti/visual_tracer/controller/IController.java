package pt.iscte.dcti.visual_tracer.controller;

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

import java.util.Vector;

import pt.iscte.dcti.instrumentation.model.AbstractJoinPoint;
import pt.iscte.dcti.instrumentation.model.IModel;
import pt.iscte.dcti.instrumentation.model.Instance;
import pt.iscte.dcti.instrumentation.model.MyWeakReference;
import pt.iscte.dcti.instrumentation.model.SintaticStructure;
import pt.iscte.dcti.instrumentation.model.Snapshot;
import pt.iscte.dcti.instrumentation.model.ThreadFlow;

public interface IController {
	
	/** 
	 * Asks the view to turn itself visible.
	 */
	public void showUserInterface();
	
	/**
	 * Asks for a representation of an instance by its weak reference to the model and creates a new instance if it doesn't exist.
	 * @param ref Weak reference of the wanted representation of instance
	 * @return the representation of the instance
	 */
	public Instance getInstance(MyWeakReference ref);
	
	/**
	 * Asks the model if the wanted instance already exists.
	 * @param ref Weak reference of the wanted representation of instance
	 * @return true if the instance already exists, false otherwise
	 */
	public boolean existInstance(MyWeakReference ref);
	
	/**
	 * Verifies if the flow of the current thread already exists and creates a new one if it does not exist.
	 * @param threadName name of the current thread
	 * @param threadFriendlyName re-writable name of the thread (by toString method)
	 * @return instance of ThreadFlow for the current thread
	 */
	public ThreadFlow getThreadFlow(String threadName, String threadFriendlyName);
	
	/**
	 * Asks the model for the vector with all the thread flows.
	 * @return vector with all of the ThreadFlow instances
	 */
	public Vector<ThreadFlow> getThreadFlows();
	
	/**
	 * Asks the model for all the sintatic structures.
	 * @return vector with all the instances of sintatic structures
	 */
	public Vector<SintaticStructure> getSintaticStructure();
	
	/**
	 * Asks the model for the current thread flow by its name
	 * @param threadName name of the current thread
	 * @return the thread flow of the current thread
	 */
	public ThreadFlow getThreadFlow(String threadName);
	
	/**
	 * Asks the model to add a new thread flow to the list of thread flows.
	 * @param thread flow to add
	 */
	public void addThreadFlow(ThreadFlow threadFlow);
	
	/**
	 * Asks the model for a specific sintatic structure by its signature.
	 * @param sintaticSignature signature of the wanted sintatic structure
	 * @return wanted sintatic structure
	 */
	public SintaticStructure getSintaticStructure(String sintaticSignature);
	
	/**
	 * Asks the model to ass a sintatic structure to the list of sintatic structures.
	 * @param sintaticStructure sintatic structure to add
	 */
	public void addSintaticStructure(SintaticStructure sintaticStructure);
	
	/**
	 * Notifies the model that a new abstract join point was detected and added.
	 * @param abstractJoinPoint abstract join point that was added
	 */
	public void abstractJoinPointAdded(AbstractJoinPoint abstractJoinPoint);
	
	//View Control
	
	/**
	 * Asks the model to set the speed at which the application will be executed.
	 * @param time execution speed
	 */
	public void setWaitTime(int time);
	
	/**
	 * Asks the model which is the current speed at which the application is being executed.
	 * @return current execution speed
	 */
	public int getWaitTime();
	
	/**
	 * Asks the model to change the execution status of the application.
	 * @param status status with which the application will be (paused or playing)
	 */
	public void setStatus(IModel.Status status);
	
	/**
	 * Asks the model to get the execution status of the application.
	 * @return execution status (paused or playing)
	 */
	public IModel.Status getSatus();
	
	/**
	 * Asks the model for the details of a representation of an instance and tells the view to show that information.
	 * @param ref weak reference of the representation of instance
	 * @param nanoSecondsOfSnapshot time in nanoseconds in which the chosen snapshot occurred
	 */
	public void viewInstanceDetail(MyWeakReference ref,long nanoSecondsOfSnapshot);
	
	/**
	 * Asks the view to show the information of the join point.
	 * @param abstractJoinPoint join point with the information to show
	 */
	public void setGeneralJoinPointInformation(AbstractJoinPoint abstractJoinPoint);
	
	/**
	 * Asks the view to show the general information about a snapshot.
	 * @param snapshot snapshot with the information to show
	 */
	public void setGeneralDataInformation(Snapshot snapshot);
	
	/**
	 * Asks the view to show a specific join point in its respective thread flow.
	 * @param abstractJoinPoint join point to show
	 */
	public void showJoinPoint(AbstractJoinPoint abstractJoinPoint);
			
}