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

import java.util.HashMap;
import java.util.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import pt.iscte.dcti.instrumentation.model.AbstractJoinPoint;
import pt.iscte.dcti.instrumentation.model.Argument;
import pt.iscte.dcti.visual_tracer.controller.IController;


public class TraceView extends Composite {

	private Tree _treeOfJoinPoints;
	private IController _controller;
	private DetailTraceView _detailThis;
	private DetailTraceView _detailTarget;
	private	Table _tableArguments;
	private Table _tableProperties;
	private HashMap<AbstractJoinPoint, TreeItem> _hashAbstractJoinPoints = new HashMap<AbstractJoinPoint, TreeItem>();
	
	public TraceView(Composite parent, int style,IController controller) {
		super(parent, style);
		setController(controller);
		initComponents();		
	}
	
	private enum DataKeys
	{
		AbstractJoinPoint
	}
	
	private void initComponents()
	{
		this.setLayout(new GridLayout(3,false)); 
		//Tree Area (select classes and methods)
		_treeOfJoinPoints = new Tree(this,SWT.BORDER);
		GridData gridTreeClassesAndMethods  = new GridData(GridData.VERTICAL_ALIGN_FILL);
		gridTreeClassesAndMethods.grabExcessVerticalSpace = true;
		gridTreeClassesAndMethods.widthHint=300;
		gridTreeClassesAndMethods.verticalSpan=2;
		gridTreeClassesAndMethods.horizontalSpan=1;
		_treeOfJoinPoints.setLayoutData(gridTreeClassesAndMethods);
		
		_treeOfJoinPoints.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent evt) {
				TreeItem treeItem = (TreeItem)evt.item;
				AbstractJoinPoint abstractJoinPoint = (AbstractJoinPoint)treeItem.getData(DataKeys.AbstractJoinPoint.name());
				
				getController().setGeneralJoinPointInformation(abstractJoinPoint);
				
				_detailTarget.setBeforeSnapshot(abstractJoinPoint.getEventBefore().getSnapshotTarget());
				
				_detailThis.setBeforeSnapshot(abstractJoinPoint.getEventBefore().getSnapshotThis());
				
				if(abstractJoinPoint.getEventAfter() != null)
				{
					_detailTarget.setAfterSnapshot(abstractJoinPoint.getEventAfter().getSnapshotTarget());
					_detailThis.setAfterSnapshot(abstractJoinPoint.getEventAfter().getSnapshotThis());
				}
				else
				{
					_detailTarget.setWaitingForAfterSnapshot();
					_detailThis.setWaitingForAfterSnapshot();
				}
				
				setArguments(abstractJoinPoint.getArguments());
				
				setProperties(ImageUtils.getImage(ImageUtils.ImageName.Clock.name()), "Before Timestamp", abstractJoinPoint.getEventBefore().getTimeStamp().toString());
				if(abstractJoinPoint.getEventAfter() != null)
					addProperties(ImageUtils.getImage(ImageUtils.ImageName.Clock.name()), "After Timestamp", abstractJoinPoint.getEventAfter().getTimeStamp().toString());
				addProperties(ImageUtils.getImage(ImageUtils.ImageName.Page.name()), "File", abstractJoinPoint.getJoinPointContextInformation().getFileName());
				addProperties(ImageUtils.getImage(ImageUtils.ImageName.PagePosition.name()), "Line position in file", abstractJoinPoint.getJoinPointContextInformation().getLineNumber()+"");
				addProperties(ImageUtils.getImage(ImageUtils.ImageName.Blue.name()), "Signature", abstractJoinPoint.getJoinPointContextInformation().getSignature());
				addProperties(ImageUtils.getImage(ImageUtils.ImageName.Blue.name()), "Kind", abstractJoinPoint.getJoinPointContextInformation().getKind());											
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		GridData gridComposites = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);		
		GridData gridTables = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
		
		Composite cmpArgumentsAndProperties = new Composite(this,SWT.NULL);
		cmpArgumentsAndProperties.setLayout(new GridLayout(1,false));
		GridData gridCompositeArgumentsAndProperties = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		gridCompositeArgumentsAndProperties.verticalSpan=2;
		gridCompositeArgumentsAndProperties.horizontalSpan=1;
		gridCompositeArgumentsAndProperties.widthHint = 300;
		cmpArgumentsAndProperties.setLayoutData(gridCompositeArgumentsAndProperties);
		
		Group cmpArguments = new Group(cmpArgumentsAndProperties, SWT.NULL);
		cmpArguments.setText("Arguments");
		cmpArguments.setLayout(new GridLayout(1,true));

		_tableArguments = new Table(cmpArguments, SWT.BORDER);

		gridComposites.widthHint=300;
		_tableArguments.setLayoutData(gridTables);
		_tableArguments.setHeaderVisible(true);				
		TableColumn tableColumnDataType = new TableColumn(_tableArguments, SWT.NULL);
		tableColumnDataType.setText("DataType");
		tableColumnDataType.setWidth(130);
		TableColumn tableColumnValue = new TableColumn(_tableArguments, SWT.NULL);
		tableColumnValue.setText("Value");
		tableColumnValue.setWidth(155);
		cmpArguments.setLayoutData(gridComposites);		
		
		Group cmpProperties = new Group(cmpArgumentsAndProperties, SWT.NULL);
		cmpProperties.setText("Properties");
		cmpProperties.setLayout(new GridLayout(1,true));
		//Label lblTitleProperties = new Label(cmpProperties, SWT.NULL);
		//lblTitleProperties.setText("Properties");
		_tableProperties = new Table(cmpProperties, SWT.BORDER);
		//gridTables.widthHint=40;
		gridComposites.widthHint=300;
		_tableProperties.setLayoutData(gridTables);
		_tableProperties.setHeaderVisible(true);				
		TableColumn tableColumnIcon = new TableColumn(_tableProperties, SWT.NULL);		
		tableColumnIcon.setWidth(20);
		TableColumn tableColumnPropertieName = new TableColumn(_tableProperties, SWT.NULL);
		tableColumnPropertieName.setText("Name");
		tableColumnPropertieName.setWidth(110);
		TableColumn tableColumnPropertieValue = new TableColumn(_tableProperties, SWT.NULL);
		tableColumnPropertieValue.setText("Value");
		tableColumnPropertieValue.setWidth(155);
		cmpProperties.setLayoutData(gridComposites);
				
		_detailThis = new DetailTraceView(this, SWT.NULL, getController());
		GridData gridDetail = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		_detailThis.setLayoutData(gridDetail);
		_detailThis.setTitle("Information about This");
		
		_detailTarget = new DetailTraceView(this, SWT.NULL, getController());
		_detailTarget.setLayoutData(gridDetail);
		_detailTarget.setTitle("Information about Target");
	}
	
	public void addAbstractJoinPoint(final AbstractJoinPoint abstractJoinPoint)
	{
		getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				//we will see if this JoinPoint has a parent, if it has a parent, we will put the item above the parent
				TreeItem treeParent = getHashAbstractJoinPoints().get(abstractJoinPoint.getParent());
				TreeItem child;
				if(treeParent!=null)
				{		
					child = new TreeItem(treeParent, SWT.NULL);
					treeParent.setExpanded(true);
				}
				else					
					child = new TreeItem(_treeOfJoinPoints, SWT.NULL);

				child.setText(abstractJoinPoint.getSintaticStructure().getFriendlySignature());
				child.setData(DataKeys.AbstractJoinPoint.name(), abstractJoinPoint);
				Image image = ImageUtils.getImage(abstractJoinPoint.getImageName());
				if(image!=null)
					child.setImage(image);
				getHashAbstractJoinPoints().put(abstractJoinPoint, child);
			}
		});
	}

	private void setController(IController _controller) {
		this._controller = _controller;
	}

	private IController getController() {
		return _controller;
	}

    // TODO Check why this method was defined here. --mms
//	private void setHashAbstractJoinPoints(HashMap<AbstractJoinPoint, TreeItem> _hashAbstractJoinPoints) {
//		this._hashAbstractJoinPoints = _hashAbstractJoinPoints;
//	}

	private HashMap<AbstractJoinPoint, TreeItem> getHashAbstractJoinPoints() {
		return _hashAbstractJoinPoints;
	}
	
	public void selectAbstractJoinPoint(AbstractJoinPoint abstractJoinPoint)
	{
		TreeItem item = getHashAbstractJoinPoints().get(abstractJoinPoint);
		if(item!=null)
		{
			_treeOfJoinPoints.select(item);
		}
	}
	
	private void setArguments(Vector<Argument> arguments)
	{
		cleanTable(_tableArguments);
		TableItem row;
		for (Argument argument : arguments) {
			row = new TableItem(_tableArguments,SWT.NULL);
			row.setText(0,argument.getType());
			row.setText(1,argument.getValue());			
		}
	}
	
	protected void setProperties(Image image, String name, String value)
	{
		cleanTable(_tableProperties);
		addProperties(image, name, value);
	}
	
	protected void addProperties(Image image, String name, String value)
	{
		TableItem row = new TableItem(_tableProperties, SWT.NULL);
		row.setImage(0, image);
		row.setText(1,name);
		row.setText(2,value);
	}
	
	private void cleanTable(Table table)
	{
		for (TableItem item : table.getItems())
			item.dispose();		
	}
}
