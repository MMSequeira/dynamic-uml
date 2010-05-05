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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import pt.iscte.dcti.instrumentation.model.Field;
import pt.iscte.dcti.instrumentation.model.MyWeakReference;
import pt.iscte.dcti.instrumentation.model.Snapshot;
import pt.iscte.dcti.visual_tracer.controller.IController;
//import pt.iscte.dcti.visual_tracer.view.ImageUtils.ImageName;

public class DetailTraceView extends Composite {

	private IController _controller;
	private Tree _treeBeforeSnapshot;
	private Tree _treeAfterSnapshot;
	private Label _lblTitle;
	private Group _group = new Group(this, SWT.NULL);
	
	protected DetailTraceView(Composite parent, int style,IController controller) 
	{
		super(parent, style);
		initComponents();
		setController(controller);
	}
	
	private enum TreeDataKeys
	{
		MyWeakReference,
		NanoSeconds
	}
	
	private void initComponents()
	{
		this.setLayout(new GridLayout(1,false));
		_group.setLayout(new GridLayout(2,false));
		
		_lblTitle = new Label(_group, SWT.NULL);		
		GridData gridTitle  = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		gridTitle.horizontalSpan=3;
		_lblTitle.setLayoutData(gridTitle);
		
		GridData gridComposites = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);		
		GridData gridTables = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
		
		//COMPOSITE FOR BEFORE SNAPSHOT		
		Composite cmpBeforeSnapshot = new Composite(_group, SWT.NULL);
		cmpBeforeSnapshot.setLayout(new GridLayout(1,true));	
		Label lblTitleBeforeSnapshot = new Label(cmpBeforeSnapshot, SWT.NULL);
		lblTitleBeforeSnapshot.setText("Snapshot Before...");
		_treeBeforeSnapshot = new Tree(cmpBeforeSnapshot, SWT.BORDER);
		_treeBeforeSnapshot.setLayoutData(gridTables);		
		_treeBeforeSnapshot.addSelectionListener(new SelectionListener() {		
			@Override
			public void widgetSelected(SelectionEvent evt) {
				TreeItem treeItem = (TreeItem)evt.item;
				if(treeItem!=null)
				{
					MyWeakReference ref = (MyWeakReference)treeItem.getData(TreeDataKeys.MyWeakReference.name());
					long nanoSecondsOfSnapshot = (Long)treeItem.getData(TreeDataKeys.NanoSeconds.name());
					getController().viewInstanceDetail(ref,nanoSecondsOfSnapshot);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent evt){}
		});	
		cmpBeforeSnapshot.setLayoutData(gridComposites);
		
		//COMPOSITE FOR AFTER SNAPSHOT
		Composite cmpAfterSnapshot = new Composite(_group, SWT.NULL);
		cmpAfterSnapshot.setLayout(new GridLayout(1,true));
		Label lblTitleAfterSnapshot = new Label(cmpAfterSnapshot, SWT.NULL);
		lblTitleAfterSnapshot.setText("Snapshot After...");
		_treeAfterSnapshot = new Tree(cmpAfterSnapshot, SWT.BORDER);
		_treeAfterSnapshot.setLayoutData(gridTables);
		_treeAfterSnapshot.addSelectionListener(new SelectionListener() {		
			@Override
			public void widgetSelected(SelectionEvent evt) {
				TreeItem treeItem = (TreeItem)evt.item;
				if(treeItem!=null)
				{
					MyWeakReference ref = (MyWeakReference)treeItem.getData(TreeDataKeys.MyWeakReference.name());
					long nanoSecondsOfSnapshot = (Long)treeItem.getData(TreeDataKeys.NanoSeconds.name());
					getController().viewInstanceDetail(ref,nanoSecondsOfSnapshot);
				}					
			}					
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});	
		cmpAfterSnapshot.setLayoutData(gridComposites);

		_group.setLayoutData(gridComposites);
	}
	
	private void clearTree(Tree tree)
	{
		for (TreeItem item : tree.getItems())
			item.dispose();				
	}	
	
	private void setController(IController _controller) 
	{
		this._controller = _controller;
	}

	private IController getController() {
		return _controller;
	}
	
	protected void setTitle(String title)
	{
		_group.setText(title);
	}
	
	protected void setAfterSnapshot(Snapshot snapshot)
	{
		setSnapShot(snapshot, _treeAfterSnapshot);
	}
	
	protected void setBeforeSnapshot(Snapshot snapshot)
	{
		setSnapShot(snapshot, _treeBeforeSnapshot);
	}
	
	protected void setWaitingForAfterSnapshot()
	{
		clearTree(_treeAfterSnapshot);
		TreeItem treeWaiting = new TreeItem(_treeAfterSnapshot, SWT.NULL);
		treeWaiting.setImage(ImageUtils.getImage(ImageUtils.ImageName.Clock.name()));
		treeWaiting.setText("Waiting...");
	}
	
	private void setSnapShot(Snapshot snapshot, Tree tree)
	{
		clearTree(tree);
		//create the class name
		TreeItem treeClass = new TreeItem(tree, SWT.NULL);
		treeClass.setImage(ImageUtils.getImage(ImageUtils.ImageName.PublicClass.name()));
		
		if(snapshot!=null)
		{
			if(snapshot.getInstance()!=null)
				treeClass.setData(TreeDataKeys.MyWeakReference.name(),snapshot.getInstance().getMyWeakReferenceObject());					
			else
				treeClass.setData(TreeDataKeys.MyWeakReference.name(),null);
			//the nanoseconds on the item is used to know at what time this snapshot was tacked
			treeClass.setData(TreeDataKeys.NanoSeconds.name(), snapshot.getNanoSeconds());
			
			treeClass.setText(snapshot.getClassName());
			for (Field field : snapshot.getFields())
			{
				TreeItem treeItem = new TreeItem(treeClass, SWT.NULL);
				treeItem.setText(field.toString());	
				if(field.getInstance()!=null)
					treeItem.setData(TreeDataKeys.MyWeakReference.name(),field.getInstance().getMyWeakReferenceObject());					
				else
					treeItem.setData(TreeDataKeys.MyWeakReference.name(),null);
				//the nanoseconds on the item is used to know at what time this snapshot was tacked
				treeItem.setData(TreeDataKeys.NanoSeconds.name(), snapshot.getNanoSeconds());
				treeItem.setImage(ImageUtils.getImage(field.getImageName()));
			}
			tree.setEnabled(true);
		}
		else
		{
			treeClass.setText("N\\A");
			tree.setEnabled(false);			
		}
		treeClass.setExpanded(true);
	}
}
