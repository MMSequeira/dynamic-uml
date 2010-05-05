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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import pt.iscte.dcti.instrumentation.model.Field;
import pt.iscte.dcti.instrumentation.model.Instance;
import pt.iscte.dcti.instrumentation.model.MyWeakReference;
import pt.iscte.dcti.instrumentation.model.Snapshot;
import pt.iscte.dcti.visual_tracer.controller.IController;

public class InstanceDetailView extends Composite {

	private IController _controller;
	private Group _group = new Group(this, SWT.NULL);
	private Tree _treeSnaposhots;
	private Tree _treeSnaposhotDetail;
	private Scale _scale;
	private Instance _instance;
	private Action _actionViewJoinpoint;
	
	//data keys for component that shows all snapshots
	private enum TreeSnapshotDataKeys
	{
		Snapshot
	}
	
	//data keys for component that shows the snapshot detail
	private enum TreeSnapshotDetailDataKeys
	{
		MyWeakReference,
		NanoSecondsOfSnapshot
	}
	
	protected InstanceDetailView(Composite parent, int style,IController controller, Instance instance, long nanoSecondsOfSnapshot) 
	{
		super(parent, style);
		setInstance(instance);
		setController(controller);
		defineActions();
		initComponents();
		fillComponents(nanoSecondsOfSnapshot);
	}
	
	public void defineActions()
	{
		_actionViewJoinpoint = new Action("View Joinpoint that generate the selected snapshot",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.Eye.name()))) {
			public void run() 
			{
				if(_treeSnaposhots.getSelectionCount()==1)
				{
					TreeItem itemSelected = _treeSnaposhots.getSelection()[0];
					Snapshot snapshot = (Snapshot) itemSelected.getData(TreeSnapshotDataKeys.Snapshot.name());
					getController().showJoinPoint(snapshot.getAbstractJoinPoint());
				}
				else
				{
					MessageBox message = new MessageBox(getShell());
					message.setMessage("Item unselected");
					message.setText("Please select one snapshot of the list");
					message.open();
				}						
			}
		};
	}
	
	private void initComponents() {
		this.setLayout(new GridLayout(1,false));
		_group.setLayout(new GridLayout(3,false));
		
		Composite cmpImages = new Composite(_group, SWT.NULL);
		cmpImages.setLayout(new GridLayout(1,false));
		GridData gridImages = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		gridImages.horizontalSpan = 3;
		gridImages.horizontalAlignment = SWT.CENTER;
		cmpImages.setLayoutData(gridImages);
		
		Button btnViewJoinPoint = new Button(cmpImages, SWT.PUSH);
		btnViewJoinPoint.addListener(SWT.Selection, new Listener() {			
			@Override
			public void handleEvent(Event evt) {
				_actionViewJoinpoint.run();				
			}
		});	
		btnViewJoinPoint.setImage(ImageUtils.getImage(ImageUtils.ImageName.Eye.name()));
		btnViewJoinPoint.setToolTipText("View Joinpoint that generated this snapshot");
		
		GridData gridGroup = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
		GridData gridSnapshotDetail = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
		GridData gridSnapshot = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		gridSnapshot.widthHint = 350;
		GridData gridScale = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		
		setScale(new Scale(_group, SWT.VERTICAL));
		getScale().setLayoutData(gridScale);
		getScale().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent evt){
				TreeItem treeItem = _treeSnaposhots.getItem(getScale().getSelection());								
				_treeSnaposhots.setSelection(treeItem);
				_treeSnaposhots.forceFocus();				
				
				Snapshot snapshot = (Snapshot) treeItem.getData(TreeSnapshotDataKeys.Snapshot.name());
				fillSnapshotDetail(snapshot);				
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent evt) {}
		});
				
		
		_treeSnaposhots = new Tree(_group, SWT.BORDER);
		_treeSnaposhots.setLayoutData(gridSnapshot);
		_treeSnaposhots.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent evt) {				
				TreeItem treeItem = (TreeItem)evt.item;
				Snapshot snapshot = (Snapshot) treeItem.getData(TreeSnapshotDataKeys.Snapshot.name());
				fillSnapshotDetail(snapshot);
				getController().setGeneralDataInformation(snapshot);
				
				int indexOfItem = _treeSnaposhots.indexOf(treeItem);
				getScale().setSelection(indexOfItem);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent evt) {}
		});
		
		_treeSnaposhotDetail = new Tree(_group, SWT.BORDER);
		_treeSnaposhotDetail.setLayoutData(gridSnapshotDetail);
		_treeSnaposhotDetail.addSelectionListener(new SelectionListener() {		
			@Override
			public void widgetSelected(SelectionEvent evt) {
				TreeItem treeItem = (TreeItem)evt.item;						
				if(treeItem!=null)
				{
					MyWeakReference ref = (MyWeakReference)treeItem.getData(TreeSnapshotDetailDataKeys.MyWeakReference.name());					
					if(ref!=null)
					{
						long nanoSecondsOfSnapshot = Long.parseLong(treeItem.getData(TreeSnapshotDetailDataKeys.NanoSecondsOfSnapshot.name()).toString());
						getController().viewInstanceDetail(ref,nanoSecondsOfSnapshot);
					}						
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent evt){}
		});
		
		_group.setLayoutData(gridGroup);
	}

	private void fillComponents(long nanoSecondsOfSnapshot) {
		TreeItem selectedItem = null;
		for (Snapshot snapshot : getInstance().getSnapshots()) {
			TreeItem snapshotItem = new TreeItem(_treeSnaposhots, SWT.NULL);
			snapshotItem.setText("Timestamp: "+snapshot.getTimestamp().toString()+"  Nano: "+snapshot.getNanoSeconds());
			snapshotItem.setData(TreeSnapshotDataKeys.Snapshot.name(), snapshot);				
			
			
			if(snapshot.getNanoSeconds() <= nanoSecondsOfSnapshot)
			{
				selectedItem = snapshotItem;
			}
		}
		
		if(selectedItem!=null)
		{
			_treeSnaposhots.setSelection(selectedItem);
			_treeSnaposhots.forceFocus();
			//set general information about this snapshot that is selected
			Snapshot snapshot = (Snapshot)_treeSnaposhots.getSelection()[0].getData(TreeSnapshotDataKeys.Snapshot.name());
			getController().setGeneralDataInformation(snapshot);
		}
				
		getScale().setMaximum(getInstance().getSnapshotsCount()-1);
		getScale().setMinimum(0);
		getScale().setPageIncrement(1);
		getScale().setIncrement(1);
		//if we have only one snapshot to show, the scale is not needed
		if(getInstance().getSnapshotsCount()==1)
			getScale().setVisible(false);
		
		if(_treeSnaposhots.getSelectionCount()==1)
		{
			TreeItem treeItem = _treeSnaposhots.getSelection()[0];
			Snapshot snapshot = (Snapshot) treeItem.getData(TreeSnapshotDataKeys.Snapshot.name());
			fillSnapshotDetail(snapshot);
			
			int indexOfItem = _treeSnaposhots.indexOf(treeItem);
			getScale().setSelection(indexOfItem);
		}
	}
	
	public void selectSnapshotThatIsNearByTime(long time)
	{
		Snapshot snapshot = null;
		TreeItem selectedItem = null;
		for(TreeItem item : _treeSnaposhots.getItems())
		{
			snapshot = (Snapshot)item.getData(TreeSnapshotDataKeys.Snapshot.name());
			if(snapshot.getNanoSeconds() <= time)
			{
				selectedItem = item;
			}			
		}
		_treeSnaposhots.setSelection(selectedItem);
	}
		
	
	private void fillSnapshotDetail(Snapshot snapshot)
	{
		clearTree(_treeSnaposhotDetail);
		//create the class name
		TreeItem treeClass = new TreeItem(_treeSnaposhotDetail, SWT.NULL);
		treeClass.setImage(ImageUtils.getImage(ImageUtils.ImageName.PublicClass.name()));
		
		if(snapshot!=null)
		{
			treeClass.setText(snapshot.getClassName());
			for (Field field : snapshot.getFields())
			{
				TreeItem treeItem = new TreeItem(treeClass, SWT.NULL);
				treeItem.setText(field.toString());	
				if(field.getInstance()!=null)
					treeItem.setData(TreeSnapshotDetailDataKeys.MyWeakReference.name(),field.getInstance().getMyWeakReferenceObject());					
				else
					treeItem.setData(TreeSnapshotDetailDataKeys.MyWeakReference.name(),null);
				treeItem.setData(TreeSnapshotDetailDataKeys.NanoSecondsOfSnapshot.name(), snapshot.getNanoSeconds());
				treeItem.setImage(ImageUtils.getImage(field.getImageName()));
			}			
		}
		else
		{
			treeClass.setText("N\\A");
			treeClass.setGrayed(true);
		}
		treeClass.setExpanded(true);
	}

	private void clearTree(Tree tree) {		
		for (TreeItem item : tree.getItems())
			item.dispose();		
	}

	private void setController(IController controller) {
		_controller = controller;		
	}
	
	private IController getController() {
		return _controller;
	}

	public void setInstance(Instance _instance) {
		this._instance = _instance;
	}

	public Instance getInstance() {
		return _instance;
	}

	private void setScale(Scale _scale) {
		this._scale = _scale;
	}

	private Scale getScale() {
		return _scale;
	}
}
