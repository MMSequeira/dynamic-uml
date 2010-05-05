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
import pt.iscte.dcti.instrumentation.model.IModel.Status;
import pt.iscte.dcti.visual_tracer.patterns.IObserver;
import pt.iscte.dcti.visual_tracer.view.IView;
import pt.iscte.dcti.visual_tracer.view.PrincipalView;

public class Controller implements IController {

	private IView _view;
	private IModel _model;
	
	public Controller(IModel model)
	{
		setModel(model);
		setView(new PrincipalView(this));
		getModel().registerObserver((IObserver) getView());
	}
	
	public Instance getInstance(MyWeakReference ref)
	{
		Instance response = getModel().getInstance(ref);
		if(response == null && ref.getObject()!=null)
		{
			response = new Instance(ref);
			getModel().addInstance(response);
		}			
		return response;		
	}
	
	public boolean existInstance(MyWeakReference ref)
	{
		return getModel().existsInstance(ref);
	}
	
	public synchronized ThreadFlow getThreadFlow(String threadName, String threadFriendlyName)
	{
		ThreadFlow response = getThreadFlow(threadName);
		if(response == null)
		{
			response = new ThreadFlow(threadName, threadFriendlyName);
			getModel().addThreadFlow(response);
		}
		return response;
	}
	
	public Vector<ThreadFlow> getThreadFlows()
	{
		return getModel().getThreadFlowList();
	}
	
	public Vector<SintaticStructure> getSintaticStructure()
	{
		return getModel().getSintaticStructureList();
	}
	
	public ThreadFlow getThreadFlow(String threadName)
	{
		return getModel().getThreadFlow(threadName);
	}
	
	public void addThreadFlow(ThreadFlow threadFlow)
	{
		getModel().addThreadFlow(threadFlow);
	}
	

	public SintaticStructure getSintaticStructure(String sintaticSignature)
	{
		return getModel().getSintaticStructure(sintaticSignature);
	}
	
	public void addSintaticStructure(SintaticStructure sintaticStructure)
	{
		getModel().addSintaticStructure(sintaticStructure);
	}		

	void setModel(IModel _model) {
		this._model = _model;
	}

	IModel getModel() {
		return _model;
	}

	private void setView(IView _view) {
		this._view = _view;
	}

	private IView getView() {
		return _view;
	}

	@Override
	public void showUserInterface() {
		getView().showUserInterface();		
	}

	@Override
	public void abstractJoinPointAdded(AbstractJoinPoint abstractJoinPoint) {		
		getModel().notifyAbstractJoinPointAdded(abstractJoinPoint);		
	}

	@Override
	public int getWaitTime() {
		return getModel().getWaitTime();
	}

	@Override
	public void setWaitTime(int time) {
		getModel().setWaitTime(time);		
	}

	@Override
	public Status getSatus() {
		return getModel().getStatus();
	}

	@Override
	public void setStatus(Status status) {
		getModel().setStatus(status);		
	}

	@Override
	public void viewInstanceDetail(MyWeakReference ref, long nanoSecondsOfSnapshot) {	
		if(ref==null)
			return;
		Instance instance = getModel().getInstance(ref);
		getView().viewInstanceDetail(instance,nanoSecondsOfSnapshot);		
	}

	@Override
	public void setGeneralDataInformation(Snapshot snapshot) {
		getView().setGeneralDataInformation(snapshot);
	}

	@Override
	public void setGeneralJoinPointInformation(AbstractJoinPoint abstractJoinPoint) {
		getView().setGeneralJoinPointInformation(abstractJoinPoint);
	}

	@Override
	public void showJoinPoint(AbstractJoinPoint abstractJoinPoint) {
		getView().showJoinPoint(abstractJoinPoint);
		
	}	
}