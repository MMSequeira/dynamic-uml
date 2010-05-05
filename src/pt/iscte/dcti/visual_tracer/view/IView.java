package pt.iscte.dcti.visual_tracer.view;

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
import pt.iscte.dcti.instrumentation.model.Instance;
import pt.iscte.dcti.instrumentation.model.Snapshot;

public interface IView {

	/**
	 * Shows the interface to the user.
	 */
	public void showUserInterface();
	
	/**
	 * Opens the tab of the selected instance and shows its details.
	 * @param instance selected instance
	 * @param nanoSecondsOfSnapshot correspondent selected snapshot--------
	 */
	public void viewInstanceDetail(Instance instance, long nanoSecondsOfSnapshot);

	/**
	 * Shows the information of the join point.
	 * @param abstractJoinPoint join point with the information to show
	 */
	public void setGeneralJoinPointInformation(AbstractJoinPoint abstractJoinPoint);
	
	/**
	 * Shows the general information about a snapshot.
	 * @param snapshot snapshot with the information to show
	 */
	public void setGeneralDataInformation(Snapshot snapshot);
	
	/**
	 * Shows a specific join point in its respective thread flow.
	 * @param abstractJoinPoint join point to show
	 */
	public void showJoinPoint(AbstractJoinPoint abstractJoinPoint);
	
}
