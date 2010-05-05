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
 * Represents a composite join point.
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:06
 */
public class CompositeJoinPoint extends AbstractJoinPoint {

	private Vector<AbstractJoinPoint> _abstractJoinPoints = new Vector<AbstractJoinPoint>();
	
	public CompositeJoinPoint(Instance instanceThis, Instance instanceTarget,
			SintaticStructure sintaticStructure, AbstractJoinPoint parent,ThreadFlow threadFlow,Object[] arguments,JoinPointContextInformation joinPointContextInformation) {
		super(instanceThis, instanceTarget, sintaticStructure,parent,threadFlow,arguments,joinPointContextInformation);
	}
	
	public void addChild(AbstractJoinPoint abstractJoinPoint)
	{
		getAbstractJoinPoints().add(abstractJoinPoint);
	}

	private Vector<AbstractJoinPoint> getAbstractJoinPoints() {
		return _abstractJoinPoints;
	}

	@Override
	public Vector<AbstractJoinPoint> getChilds() {
		return (Vector<AbstractJoinPoint>) _abstractJoinPoints.clone();
	}

	@Override
	public String getKind() {
		return "";
	}
	
	@Override
	public String getImageName() {
		return ImageUtils.ImageName.GenericEvent.name();
	}

}