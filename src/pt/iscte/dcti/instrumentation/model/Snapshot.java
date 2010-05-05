package pt.iscte.dcti.instrumentation.model;

import java.sql.Timestamp;
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
 * @created 06-Nov-2009 15:52:07
 */
public class Snapshot {

	private Vector<Field> _fields = new Vector<Field>();
	private String _className;
	private Timestamp _timestamp;
	private long _nanoSeconds;
	private Instance _instance;
	//the snapshot must know what joinpoint made the snapshot
	private AbstractJoinPoint _abstractJoinPoint;
	
	public Snapshot(String className,Instance instance){
		setClassName(className);
		setInstance(instance);
	}
	
	public Snapshot(String className,Instance instance, AbstractJoinPoint abstractJoinPoint){
		setClassName(className);
		setInstance(instance);
		setAbstractJoinPoint(abstractJoinPoint);
	}

	public Vector<Field> getFields() {
		return _fields;
	}
	
	public void addField(Field field)
	{
		getFields().add(field);
	}

	public void setClassName(String _className) {
		this._className = _className;
	}

	public String getClassName() {
		return _className;
	}

	public void setNanoSeconds(long _nanoSeconds) {
		this._nanoSeconds = _nanoSeconds;
	}

	public long getNanoSeconds() {
		return _nanoSeconds;
	}

	public void setTimestamp(Timestamp _timestamp) {
		this._timestamp = _timestamp;
	}

	public Timestamp getTimestamp() {
		return _timestamp;
	}
	
	/**
	 * Returns true when each field of a snapshot is equals to another field in the same position
	 */
	public boolean equals(Object object)
	{
		if(object instanceof Snapshot)
		{
			Snapshot snapshot = (Snapshot) object;
			if (this.getFields().size()!= snapshot.getFields().size())
				return false;
			for (int index = 0; index < this.getFields().size(); index++) 
			{				
				if(!this.getFields().get(index).equals(snapshot.getFields().get(index)))
					return false;
			}
		}
		else
			return false;
		return true;
	}

	public void setInstance(Instance instance) {
		this._instance = instance;
	}

	public Instance getInstance() {
		return _instance;
	}

	public void setAbstractJoinPoint(AbstractJoinPoint _abstractJoinPoint) {
		this._abstractJoinPoint = _abstractJoinPoint;
	}

	public AbstractJoinPoint getAbstractJoinPoint() {
		return _abstractJoinPoint;
	}
}