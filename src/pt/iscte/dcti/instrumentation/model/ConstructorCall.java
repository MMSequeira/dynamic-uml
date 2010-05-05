package pt.iscte.dcti.instrumentation.model;

import java.util.Vector;

import pt.iscte.dcti.visual_tracer.view.ImageUtils;


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
 * Represents a call to a constructor.
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:06
 */
public class ConstructorCall extends AbstractJoinPoint {

	
	public ConstructorCall(	Instance instanceThis, Instance instanceTarget,
			SintaticStructure sintaticStructure, AbstractJoinPoint parent,ThreadFlow threadFlow,Object[] arguments,JoinPointContextInformation joinPointContextInformation) {
		super(instanceThis, instanceTarget, sintaticStructure,parent,threadFlow,arguments,joinPointContextInformation);
	}

	private ConstructorExecution _constructorExecution;
	private StaticInitialization _staticInitialization;


	@Override
	public void addChild(AbstractJoinPoint abstractJoinPoint) {		
		//callConstructor could have staticInitialization or constructorExecution
		if(abstractJoinPoint instanceof ConstructorExecution)
			setConstructorExecution((ConstructorExecution) abstractJoinPoint);
		else if(abstractJoinPoint instanceof StaticInitialization)
			setStaticInitialization((StaticInitialization)abstractJoinPoint);
	}


	@Override
	public Vector<AbstractJoinPoint> getChilds() {
		Vector<AbstractJoinPoint> result = new Vector<AbstractJoinPoint>();
		if(getStaticInitialization()!=null)
			result.add(getStaticInitialization());
		if(getConstructorExecution()!=null)		
		result.add(getConstructorExecution());
		return result;
	}

	public void setConstructorExecution(ConstructorExecution _constructorExecution) {
		this._constructorExecution = _constructorExecution;
	}

	public ConstructorExecution getConstructorExecution() {
		return _constructorExecution;
	}

	@Override
	public String getKind() {
		return "constructor-call";
	}

	private void setStaticInitialization(StaticInitialization _staticInitialization) {
		this._staticInitialization = _staticInitialization;
	}

	private StaticInitialization getStaticInitialization() {
		return _staticInitialization;
	}
	
	@Override
	public String getImageName() {
		return ImageUtils.ImageName.ConstructorCall.name();
	}
}