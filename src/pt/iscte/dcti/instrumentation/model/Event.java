package pt.iscte.dcti.instrumentation.model;

import java.sql.Timestamp;

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
 * Represents an event occurred.
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:06
 */
public class Event {

	private Snapshot _snapshotThis;
	private Snapshot _snapshotTarget;
	private Timestamp _timeStamp;
	private long _nanoTime; 

	public Event(Snapshot snapshotTarget,Snapshot snapshotThis, Timestamp timeStamp, long nanoTime)	
	{
		setNanoTime(nanoTime);
		setTimeStamp(timeStamp);
		setSnapshotTarget(snapshotTarget);
		setSnapshotThis(snapshotThis);
	}

	public void setSnapshotTarget(Snapshot _snapshot) {
		this._snapshotTarget = _snapshot;
	}

	public Snapshot getSnapshotTarget() {
		return _snapshotTarget;
	}
	
	public void setSnapshotThis(Snapshot _snapshot) {
		this._snapshotThis = _snapshot;
	}

	public Snapshot getSnapshotThis() {
		return _snapshotThis;
	}

	public void setTimeStamp(Timestamp _timeStamp) {
		this._timeStamp = _timeStamp;
	}

	public Timestamp getTimeStamp() {
		return _timeStamp;
	}

	public void setNanoTime(long _nanoTime) {
		this._nanoTime = _nanoTime;
	}

	public long getNanoTime() {
		return _nanoTime;
	}
}