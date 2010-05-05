package pt.iscte.dcti.instrumentation.model;

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
 * Representation of an instance.
 * @author Carlos Correia
 * @author Rute Oliveira
 * @version 1.0
 * @created 06-Nov-2009 15:52:06
 */
public class Instance {

	private Vector<Snapshot> _snapshot = new Vector<Snapshot>();
	private MyWeakReference _myWeakReferenceObject;
	private String _instanceName="";
	private String _className="";
	

	public Instance(MyWeakReference ref){
		setWeakReference(ref);
		if(ref.getObject()!=null)
		{
			setInstanceName(ref.getObject().toString());
			setClassName(ref.getObject().getClass().getName());
		}
	}

	public Snapshot getLastSnapShot()
	{
		return getSnapshotsCount() > 0 ? getSnapshots().lastElement() : null;
	}

	public Vector<Snapshot> getSnapshots() {
		return (Vector<Snapshot>)_snapshot.clone();
	}
	
	public void addSnapshot(Snapshot snapshot)
	{
		_snapshot.add(snapshot);
	}
	
	public int getSnapshotsCount()
	{
		return getSnapshots().size();
	}

	private void setWeakReference(MyWeakReference ref) {
		this._myWeakReferenceObject = ref;
	}

	public Object getObject() {
		return getMyWeakReferenceObject().getObject();
	}

	public MyWeakReference getMyWeakReferenceObject() {
		return _myWeakReferenceObject;
	}

	private void setInstanceName(String _instanceName) {
		this._instanceName = _instanceName;
	}

	public String getInstanceName() {
		return _instanceName;
	}

	private void setClassName(String _className) {
		this._className = _className;
	}

	public String getClassName() {
		return _className;
	}
	
	public boolean equals(Object object)
	{
		if(object instanceof Instance)
		{
			Instance instance = (Instance)object;
			return this.getMyWeakReferenceObject().equals(instance.getMyWeakReferenceObject());
		}
		else
			return false;
		
		
	}

}