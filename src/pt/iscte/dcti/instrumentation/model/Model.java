package pt.iscte.dcti.instrumentation.model;

import java.util.HashMap;
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
 * @created 06-Nov-2009 15:52:06
 */
public class Model extends IModel {
	
	private HashMap<String, ThreadFlow> _hashThreadFlow = new HashMap<String, ThreadFlow>(); 
	
	private HashMap<String, SintaticStructure> _hashSintaticStructure = new HashMap<String, SintaticStructure>();
	
	private HashMap<MyWeakReference, Instance> _hashInstance = new HashMap<MyWeakReference, Instance>();
	
	private int _waitTime;
	
	private Status _status = Status.Playing;
	
	public Vector<ThreadFlow> getThreadFlowList()
	{
		Vector<ThreadFlow> threadFlows = new Vector<ThreadFlow>();
		getThreadFlows().values();
		for (ThreadFlow threadFlow : getThreadFlows().values()) {
			threadFlows.add(threadFlow);
		}
		return threadFlows;
	}
	

	public Vector<SintaticStructure> getSintaticStructureList()
	{
		Vector<SintaticStructure> sintaticStructures = new Vector<SintaticStructure>();
		getThreadFlows().values();
		for (SintaticStructure sintaticStructure : getSintaticStructures().values()) {
			sintaticStructures.add(sintaticStructure);
		}
		return sintaticStructures;
	}
	
	

	public ThreadFlow getThreadFlow(String threadName)
	{
		return getThreadFlows().get(threadName);
	}
	

	public boolean existsThreadFlow(String threadName)
	{
		return getThreadFlows().containsKey(threadName);
	}
	
	public void addThreadFlow(ThreadFlow threadFlow)
	{
		getThreadFlows().put(threadFlow.getName(), threadFlow);
		notifyThreadFlowAdded(threadFlow);
	}
	
	public Instance getInstance(MyWeakReference ref)
	{
		return getInstances().get(ref);
	}
	
	public boolean existsInstance(MyWeakReference ref)
	{
		return getInstances().containsKey(ref);
	}
	
	public void addInstance(Instance instance)
	{
		getInstances().put(instance.getMyWeakReferenceObject(),instance);
	}

	private HashMap<String, ThreadFlow> getThreadFlows() {
		return getHashThreadFlow();
	}
	
	public SintaticStructure getSintaticStructure(String sintaticSignature)
	{
		return getSintaticStructures().get(sintaticSignature);
	}
	
	public boolean existsSintaticStructure(String sintaticSignature)
	{
		return getSintaticStructures().containsKey(sintaticSignature);
	}
	
	public void addSintaticStructure(SintaticStructure sintaticStructure)
	{
		getSintaticStructures().put(sintaticStructure.getSignature(), sintaticStructure);
	}

	private HashMap<String, SintaticStructure> getSintaticStructures() {
		return _hashSintaticStructure;
	}

	private HashMap<MyWeakReference, Instance> getInstances() {
		return _hashInstance;
	}

	private HashMap<String, ThreadFlow> getHashThreadFlow() {
		return _hashThreadFlow;
	}
	

	public synchronized void setWaitTime(int _waitTime) {
		this._waitTime = _waitTime;
		notifyTimeChanged(this._waitTime);
	}

	public synchronized int getWaitTime() {
		return _waitTime;
	}

	public void setStatus(Status _status) {		
		this._status = _status;
		notifyStatusChanged(_status);
	}

	public Status getStatus() {
		return _status;
	}
	
}