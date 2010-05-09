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

//import static java.lang.System.out;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import org.aspectj.lang.JoinPoint;

import pt.iscte.dcti.instrumentation.model.AbstractJoinPoint;
import pt.iscte.dcti.instrumentation.model.Attribute;
import pt.iscte.dcti.instrumentation.model.Constructor;
import pt.iscte.dcti.instrumentation.model.ConstructorCall;
import pt.iscte.dcti.instrumentation.model.ConstructorExecution;
import pt.iscte.dcti.instrumentation.model.Event;
import pt.iscte.dcti.instrumentation.model.FieldRead;
import pt.iscte.dcti.instrumentation.model.FieldWrite;
import pt.iscte.dcti.instrumentation.model.IModel;
import pt.iscte.dcti.instrumentation.model.Instance;
import pt.iscte.dcti.instrumentation.model.JoinPointContextInformation;
import pt.iscte.dcti.instrumentation.model.Method;
import pt.iscte.dcti.instrumentation.model.MethodCall;
import pt.iscte.dcti.instrumentation.model.MethodExecution;
import pt.iscte.dcti.instrumentation.model.Model;
import pt.iscte.dcti.instrumentation.model.MyWeakReference;
import pt.iscte.dcti.instrumentation.model.SintaticStructure;
import pt.iscte.dcti.instrumentation.model.Snapshot;
import pt.iscte.dcti.instrumentation.model.StaticInitialization;
import pt.iscte.dcti.instrumentation.model.StaticInitializer;
import pt.iscte.dcti.instrumentation.model.ThreadFlow;
import pt.iscte.dcti.visual_tracer.controller.Controller;
import pt.iscte.dcti.visual_tracer.controller.IController;




abstract aspect AbstractTracer {
	
	private boolean _firstExecution = true;
	
	private int _callDepth = -1;
	private IController _controller;
	
	public AbstractTracer()
	{
	}
	
	//REGION ASPECTS	
	
	//catch the execution of the main method
	pointcut mainMethod() : execution(public static void *.main(..));		
	
	//catch all join points (in class and nested classes) inside of packages that are part of the profiler application
	pointcut codeInsideMyProject() : within((pt.iscte.dcti.instrumentation..* || pt.iscte.dcti.visual_tracer..*) && !pt.iscte.dcti.instrumentation.examples..*) || cflow(execution(String *.toString()));
	
	pointcut classInitialization() : staticinitialization(*);
	
	pointcut consctructorCall() : call(*.new(..)) ;
	
	pointcut consctructorExecution() : execution(*.new(..)) ;
	
	pointcut methodCall() : call(* *.*(..)) ;
	
	pointcut methodExecution() : execution(* *.*(..)) ;	
	
	pointcut fieldWriteAccess() : set(* *.*);
	
	pointcut fieldReadAcess() : get(* *.*);	
		
	
	Object around() : fieldWriteAccess() && !codeInsideMyProject()
	{
		slowDown();
		print("before set ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		
		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
		
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
					
		Event eventBefore = constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new Attribute(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new FieldWrite(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);
		
		//creating snapshots
		if(instanceThis!=null && instanceTarget!=null && instanceThis.equals(instanceTarget))
		{
			Snapshot snapshot = getSnapshot(instanceThis,abstractJoinPoint);
			instanceThis.addSnapshot(snapshot);
		}
		else if(instanceThis!=null)
		{
			Snapshot snapshot = getSnapshot(instanceThis,abstractJoinPoint);
			instanceThis.addSnapshot(snapshot);
		}
		else if(instanceTarget!=null)
		{
			Snapshot snapshot = getSnapshot(instanceTarget,abstractJoinPoint);
			instanceTarget.addSnapshot(snapshot);
		}

		Event eventAfter =constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);
		
		print("before set ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		return returnValue;
	}
	
	Object around() : fieldReadAcess() && !codeInsideMyProject()
	{
		slowDown();
		print("before get ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		
		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
				
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
		
		Event eventBefore = constructEvent(thisJoinPoint,instanceThis, instanceTarget);		
		
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new Attribute(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new FieldRead(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);

		Event eventAfter = constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);
		
		print("before get ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		return returnValue;
	}
	
	Object around() : consctructorCall() && !codeInsideMyProject()
	{
		slowDown();
		print("before consctructorCall ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		
		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
		
		
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
		
		Event eventBefore = constructEvent(thisJoinPoint,instanceThis, instanceTarget);		
		
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new Constructor(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new ConstructorCall(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);

		Event eventAfter = constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);		
		
		print("after consctructorCall ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());		
				
		return returnValue;	
	}
	
	
	Object around() : consctructorExecution() && !codeInsideMyProject()
	{
		slowDown();
		print("before consctructorExecution ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		
		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
		
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
		
		Event eventBefore = constructEvent(thisJoinPoint,instanceThis, instanceTarget);			
		
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new Constructor(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new ConstructorExecution(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);

		Event eventAfter = constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);		
		
		print("after consctructorExecution ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());		
				
		return returnValue;
	}
	
	
	Object around() : methodCall() && !codeInsideMyProject()
	{
		slowDown();
		print("before methodCall ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		
		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
		
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
		
		Event eventBefore = constructEvent(thisJoinPoint,instanceThis, instanceTarget);		
		
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new Method(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new MethodCall(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);

		Event eventAfter =constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);;

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);

		print("after methodCall ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		return returnValue;
	}
	
	
	Object around() : methodExecution() && !codeInsideMyProject()
	{
		slowDown();
		print("before executionMainMethod ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());		

		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
		
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
		
		Event eventBefore = constructEvent(thisJoinPoint,instanceThis, instanceTarget);		
		
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new Method(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new MethodExecution(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);

		Event eventAfter = constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);	

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);

		print("after executionMainMethod ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		return returnValue;
	}
	
	
	Object around() : classInitialization() && !codeInsideMyProject()
	{
		print("before classInitialization ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		if(isFirstExecution())
		{
			startUserInterface();		
			waitUntilUserInterfaceIsNotCreated();
			setFirstExecution(false);
		}
		slowDown();
		
		ThreadFlow threadFlow = getController().getThreadFlow(getCurrentThreadName(),getCurrentThreadFriendlyName());
		
		Instance instanceThis = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getThis()));
		Instance instanceTarget = getController().getInstance(MyWeakReference.createMyWeakReference(thisJoinPoint.getTarget()));
		
		Event eventBefore = constructEvent(thisJoinPoint, instanceTarget, instanceThis);		
		
		//SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart);
		SintaticStructure sintaticStructure = getController().getSintaticStructure(thisJoinPointStaticPart.getSignature().toString());
		if(sintaticStructure == null)
		{
			sintaticStructure = new StaticInitializer(thisJoinPointStaticPart.getSignature().toString(),thisJoinPointStaticPart.getSignature().getName(),thisJoinPointStaticPart.getSignature().getModifiers());
			getController().addSintaticStructure(sintaticStructure);
		}
		
		JoinPointContextInformation contextInfo = getContextInformation(thisJoinPointStaticPart);
		AbstractJoinPoint abstractJoinPoint = new StaticInitialization(instanceThis,instanceTarget,sintaticStructure,threadFlow.lastElementFromStack(),threadFlow,thisJoinPoint.getArgs(),contextInfo); 
		abstractJoinPoint.setEventBefore(eventBefore);

		threadFlow.addToStack(abstractJoinPoint);
		getController().abstractJoinPointAdded(abstractJoinPoint);
		
		Object returnValue;
		setCallDepth(getCallDepth() + 1);		
		returnValue = proceed();
		setCallDepth(getCallDepth() - 1);

		Event eventAfter = constructEvent(thisJoinPoint,instanceThis, instanceTarget);
		abstractJoinPoint.setEventAfter(eventAfter);	

		threadFlow.popFromStack();
		if(threadFlow.isAbstrcatJoinPointHead())
			threadFlow.addAbstractJoinPointHead(abstractJoinPoint);
		else
			threadFlow.lastElementFromStack().addChild(abstractJoinPoint);

		print("after classInitialization ",thisJoinPoint,thisJoinPoint.getThis(),thisJoinPoint.getTarget());
		return returnValue;
	}
	
	/**
	 * This method is necessary to wait until the Profiler application is not started
	 * because the Profiler is executed in a separate thread, we must ensure that all the
	 * application was loaded. Then we can proceed with the Main Thread
	 */	
	private void waitUntilUserInterfaceIsNotCreated(){	
		while(getController()==null){
			//wait
		}
		return;
	}
	
	/**
	 * Starts the user interface in a different thread
	 */
	private void startUserInterface(){
		//this code starts the UI in a different thread, this way the UI don't block the code to run normally
		Thread threadUi = new Thread(){
			public void run(){
				IModel model = new Model(); 
				_controller = new Controller(model);				
				_controller.showUserInterface();
			}
		};
		threadUi.start();
	}
	
	/**
	 * This method is used to slow down the application speed, this way we can see the events appear on the screen 
	 */
	private void slowDown()
	{
		while(getController().getSatus().equals(IModel.Status.Paused))
		{
			sleep(1000);
		}
		int timeToWait = getController().getWaitTime();
		if(timeToWait != 0)		
			sleep(timeToWait*1000);		
	}
	
	/**
	 * Utility function that puts the current thread to sleep
	 * @param time - number of milliseconds that the thread will sleep
	 */
	private void sleep(int time)
	{
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function is used for debug purposes
	 * it prints on the console all information about a given joinpoint
	 * @param prefix - identify the pointcut that called this function
	 * @param joinpoint
	 * @param calle
	 * @param target
	 */
	private void print(String prefix,JoinPoint joinpoint,Object calle,Object target)
	{
		/*
		for (int i = 0, spaces = getCallDepth()*2; i < spaces; i++) {
			out.print(" ");
		}
		out.println(prefix+" : "+joinpoint.getSignature()+"; caller: "+calle+"; target: "+target+" args:"+Arrays.toString(joinpoint.getArgs())+" thread:"+Thread.currentThread().getName());
		*/
	}
	
	private String getCurrentThreadName()
	{
		return Thread.currentThread().getName();
	}
	
	/**
	 * Function that returns the name of the thread in meaningfully words
	 * if the Thread has a toString method defined by the user, it will be the Friendly Name
	 * @return 
	 */
	private String getCurrentThreadFriendlyName()
	{
		return Thread.currentThread().toString();
	}
	
	public Snapshot getSnapshot(Instance instance, AbstractJoinPoint abstractJoinPoint)
	{		
		Object thiz = instance.getObject();
		Snapshot response = null;
		if(thiz!=null)
		{
			response = new Snapshot(thiz.getClass().getName(),instance,abstractJoinPoint);
			response.setTimestamp(new Timestamp(System.currentTimeMillis()));
			response.setNanoSeconds(System.nanoTime());
			Field[] fields = UtilReflect.getClassFields(thiz.getClass());
			pt.iscte.dcti.instrumentation.model.Field fieldOfObject;
			for (Field field : fields) {
				Object object = UtilReflect.getInstanceFieldValue(field, thiz);				
				if(object!=null)
				{
					MyWeakReference refOfObject = MyWeakReference.createMyWeakReference(object);
					Instance instanceOfObject = null;
					if(getController().existInstance(refOfObject))
						instanceOfObject = getController().getInstance(refOfObject);
					fieldOfObject = new pt.iscte.dcti.instrumentation.model.Field(field.getName(),object.toString(), field.getGenericType().toString(), field.getModifiers(), instanceOfObject);
					response.addField(fieldOfObject);
				}
			}
		}
		return response;
	}
	
	public JoinPointContextInformation getContextInformation(JoinPoint.StaticPart joinPointInfo)
	{
		return  new JoinPointContextInformation(joinPointInfo.getSourceLocation().getFileName(), joinPointInfo.getSourceLocation().getColumn(), joinPointInfo.getSourceLocation().getLine(), joinPointInfo.getSignature().toString(), joinPointInfo.getKind());
	}	
	
	private Event constructEvent(JoinPoint joinPointInfo, Instance instanceThis, Instance instanceTarget)
	{	
		Snapshot snapShotTarget = null;
		Snapshot snapShotThis = null;
//		if(instanceThis!=null)		
//			snapShotThis = instanceThis.getSnapshotsCount() > 0 ? instanceThis.getLastSnapShot() : null;
//		if(instanceTarget!=null)
//			snapShotTarget = instanceTarget.getSnapshotsCount() > 0 ? instanceTarget.getLastSnapShot() : null;		
		if(instanceThis!=null)
			snapShotThis = getSnapshot(instanceThis,null);
		if(instanceTarget!=null)
			snapShotTarget = getSnapshot(instanceTarget,null);
		return new Event(snapShotTarget,snapShotThis, new Timestamp(System.currentTimeMillis()), System.nanoTime());
	}

	private void setController(IController _controller) {
		this._controller = _controller;
	}

	private IController getController() {
		return _controller;
	}

	private void setCallDepth(int _callDepth) {
		this._callDepth = _callDepth;
	}

	private int getCallDepth() {
		return _callDepth;
	}

	private void setFirstExecution(boolean _firstExecution) {
		this._firstExecution = _firstExecution;
	}

	private boolean isFirstExecution() {
		return _firstExecution;
	}

}