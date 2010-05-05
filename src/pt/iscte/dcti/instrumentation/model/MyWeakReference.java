package pt.iscte.dcti.instrumentation.model;

import java.lang.ref.WeakReference;
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
 *
 * @author Carlos Correia
 * @author Rute Oliveira
 */
public class MyWeakReference {

    private static int _keyGenCode = 0;
    private int _keyCode;
    private WeakReference<Object> _weakReference;   
    private static Vector<MyWeakReference> _historyOfMyWeakReferences = new Vector<MyWeakReference>();

    public static MyWeakReference createMyWeakReference(Object object)
    {
        incrementKeyGen();
        MyWeakReference myWeakReference = new MyWeakReference(object,getKeyGenCode());
        int indexOfObject = _historyOfMyWeakReferences.indexOf(myWeakReference);
        //the object is not in the _historyOfMyWeakReferences
        if(indexOfObject==-1)
        {
            _historyOfMyWeakReferences.add(myWeakReference);
            return myWeakReference;
        }
        else
        {
        	//TODO Check whether it is necessary to create MyWeakReference for null objects
            return _historyOfMyWeakReferences.get(indexOfObject);
        }
    }

    private MyWeakReference(Object object, int keycode)
    {
        setWeakReference(object);
        setKeyCode(keycode);
    }

    private static void incrementKeyGen()
    {
        _keyGenCode++;
    }

    public boolean equals(Object object)
    {
        if(object== null)
            return false;
        if(object instanceof MyWeakReference)
        {
            MyWeakReference otherWeakReference = (MyWeakReference)object;
            if(this.getObject()==null || otherWeakReference.getObject()==null)
                return this.getKeyCode() == otherWeakReference.getKeyCode();
            else
                return this.getObject().equals(otherWeakReference.getObject());
        }
        else
            return false;
    }

    public int hashCode()
    {
    	return this.getKeyCode();
    }

    /**
     * @return the _weakReference
     */
    public Object getObject() {
        return _weakReference.get();
    }

    /**
     * @param weakReference the _weakReference to set
     */
    public void setWeakReference(Object object) {
        this._weakReference = new WeakReference<Object>(object);
    }

    /**
     * @return the _keyCode
     */
    public int getKeyCode() {
        return _keyCode;
    }

     /**
     * @return the _keyGenCode
     */
    private static int getKeyGenCode() {
        return _keyGenCode;
    }

    /**
     * @param keyCode the _keyCode to set
     */
    private void setKeyCode(int keyCode) {
        this._keyCode = keyCode;
    }

    public static void debud()
    {
        for(MyWeakReference weak : _historyOfMyWeakReferences)
        {
            System.out.println(weak.getKeyCode() + " - " +weak.getObject());
        }
    }
}