package pt.iscte.dcti.instrumentation.model;

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
 * Represents the execution of a constructor.
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:06
 */
public class ConstructorExecution extends CompositeJoinPoint {

	public ConstructorExecution(Instance instanceThis, Instance instanceTarget,
			SintaticStructure sintaticStructure, AbstractJoinPoint parent,ThreadFlow threadFlow,Object[] arguments,JoinPointContextInformation joinPointContextInformation) {
		super( instanceThis, instanceTarget, sintaticStructure, parent, threadFlow,arguments,joinPointContextInformation);
	}

	@Override
	public String getKind() {
		return "constructor-execution";
	}
	
	@Override
	public String getImageName() {
		return ImageUtils.ImageName.ConstructorExecution.name();
	}

}