package pt.iscte.dcti.visual_tracer.patterns;

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

import pt.iscte.dcti.instrumentation.model.AbstractJoinPoint;
import pt.iscte.dcti.instrumentation.model.IModel;
import pt.iscte.dcti.instrumentation.model.ThreadFlow;

public interface ISubject {
	public void registerObserver(IObserver observer);
	public void removeObsserver(IObserver observer);
	public void notifyThreadFlowAdded(ThreadFlow threadFlow);
	public void notifyAbstractJoinPointAdded(AbstractJoinPoint abstractJoinPoint);
	public void notifyTimeChanged(int time);
	public void notifyStatusChanged(IModel.Status status);
}
