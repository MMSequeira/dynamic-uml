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

import java.io.IOException;
import java.util.HashMap;
import java.io.File;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import pt.iscte.dcti.instrumentation.model.AbstractJoinPoint;
import pt.iscte.dcti.instrumentation.model.IModel;
import pt.iscte.dcti.instrumentation.model.Instance;
import pt.iscte.dcti.instrumentation.model.Snapshot;
import pt.iscte.dcti.instrumentation.model.ThreadFlow;
import pt.iscte.dcti.instrumentation.model.IModel.Status;
import pt.iscte.dcti.visual_tracer.controller.IController;
import pt.iscte.dcti.visual_tracer.patterns.IObserver;

public class PrincipalView  implements IView, IObserver{

	private Shell _shell;
	private Display _display;
	//private Tree treeClassesAndMethods;	
	private CTabFolder _tabFolder;
	private CTabFolder _tabGeneralContents;
	private CTabItem _tabGeneralInfo;
	private String _apllicationName = "Visual Tracer";
	private IController _controller;
	private HashMap<ThreadFlow, TraceView> _hashtTreadFlow = new HashMap<ThreadFlow, TraceView>();
	private Action _actionExit;
	private Action _actionPlay;
	private Action _actionPause;
	private Action _actionHelpContent;
	private Action _actionDeveloperHelpContent;
	private Action _actionAbout;
	private Action _actionViewGPL;
	private Label _lblTime;
	
	private enum TabFolderData
	{
		Object
	}
	
	
	public PrincipalView(IController controller)
	{
		setController(controller);
		initComponents();		
	}
	
	private void defineActions()
	{
		_actionExit = new Action("&Exit",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.DoorOut.name()))) {
			public void run() 
			{
				MessageBox messagebox = new MessageBox(getShell(),SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messagebox.setMessage("Are you sure?");
				messagebox.setText("Exit apllication");
				int option = messagebox.open();
				if(option == SWT.YES)
				{
					//when the user shutdown the UI, the program that was inspected keep going normally
					getController().setStatus(Status.UserExit);
					getShell().dispose();
				}
			}
		};
		
		_actionPlay = new Action("&Play@Ctrl+P",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.ControlPlay.name()))) {
			public void run() 
			{
				getController().setStatus(IModel.Status.Playing);				
			}
		};
		
		_actionPause = new Action("&Pause",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.ControlPause.name()))) {
			public void run() 
			{
				getController().setStatus(IModel.Status.Paused);
			}
		};
			
		_actionHelpContent = new Action("&Help Content",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.Help.name()))) {
			public void run() 
			{
				File currentDir = new File("");								
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+currentDir.getAbsolutePath()+"\\Files\\ManualUtilizacao.pdf");
				} catch (IOException e) 
				{
					System.out.println(e.getMessage());
				}
			}		
		};
		
		_actionDeveloperHelpContent = new Action("&Developer Help Content",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.Help.name()))) {
			public void run() 
			{
				File currentDir = new File("");								
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+currentDir.getAbsolutePath()+"\\Files\\Visual Tracer javadoc\\index.html");
				} catch (IOException e) 
				{
					System.out.println(e.getMessage());
				}
			}		
		};
		
		_actionAbout = new Action("&About",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.Group.name()))) {
			public void run() 
			{
				new AboutView(_shell, SWT.NULL);
			}
		};
			
		_actionViewGPL = new Action("&GNU General Public License",ImageDescriptor.createFromImage(ImageUtils.getImage(ImageUtils.ImageName.Gnu.name()))) {
			public void run() 
			{
				new GnuView(_shell, SWT.NULL);
			}
		};
	}
	
	private void defineMenus()
	{
		// menu manager
		MenuManager menuManager = new MenuManager();

		// Menu Files 
		MenuManager fileMenuManager = new MenuManager("&File");
		
		//Menu Help
		MenuManager helpMenuManager = new MenuManager("&Help");
		
		// adding menus
		menuManager.add(fileMenuManager);
		menuManager.add(helpMenuManager);
		
		fileMenuManager.add(new Separator());
		fileMenuManager.add(_actionExit);
		
		helpMenuManager.add(_actionHelpContent);
		helpMenuManager.add(_actionDeveloperHelpContent);
		helpMenuManager.add(_actionAbout);
		helpMenuManager.add(_actionViewGPL);
		
		menuManager.updateAll(true);
		getShell().setMenuBar(menuManager.createMenuBar((Decorations)getShell()));
	}
	
	private void defineToolBar()
	{		
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginRight=0; rowLayout.marginWidth=0; rowLayout.marginTop=0;
		rowLayout.marginBottom=0; rowLayout.marginLeft=0; rowLayout.marginHeight=0;

		CoolBar coolBar = new CoolBar(getShell(), SWT.NONE);
		GridData gridDataCoolBar = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridDataCoolBar.horizontalSpan=1;
		gridDataCoolBar.grabExcessHorizontalSpace=true;
		coolBar.setLayoutData(gridDataCoolBar);
		coolBar.addListener(SWT.MouseUp,new Listener(){public void handleEvent(Event evt) {getShell().layout();}});
		
		Composite cmp = new Composite(coolBar, SWT.NULL);
		cmp.setLayout(new GridLayout(1, true));
		new Label(cmp, SWT.NULL);		

		//--create a coolbar item for "File Action's"
		CoolItem fileItem = new CoolItem(coolBar,  SWT.NONE);
		
		Composite fileComposite = new Composite(coolBar, SWT.NONE);
		fileComposite.setLayout(rowLayout);		
		
		ToolBar toolBarFile = new ToolBar(fileComposite, SWT.FLAT);
		ToolBarManager toolBarMngFile = new ToolBarManager(toolBarFile);
		toolBarMngFile.add(_actionPlay);
		toolBarMngFile.add(_actionPause);

		toolBarMngFile.update(true);
		final Scale scale = new Scale(fileComposite, SWT.HORIZONTAL);
		scale.setMaximum(10);
		scale.setMinimum(0);
		scale.setIncrement(1);
		scale.setPageIncrement(1);
		scale.setSelection(1);
		getController().setWaitTime(scale.getSelection());
		//scale.setSize(30, 15);
		
		new Label(fileComposite,SWT.NULL).setText("Status:");
		
		_lblTime = new Label(fileComposite, SWT.NULL);
		_lblTime.setText("playing at "+scale.getSelection()+"  s           ");
		
		scale.addListener(SWT.Selection, new Listener() {			
			@Override
			public void handleEvent(Event evt) {				
				int waitTime = scale.getSelection();
				getController().setWaitTime(waitTime);
				if(getController().getSatus().equals(IModel.Status.Paused))
					getController().setStatus(IModel.Status.Playing);
			}
		});
		
		_actionPlay.setChecked(true);

		fileComposite.pack();
		Point size = fileComposite .getSize();
		fileItem.setControl(fileComposite );
		fileItem.setSize(fileItem.computeSize(size.x, size.y));			
	}
	
	private void initComponents()
	{		
		//Creating necessary components to create the window
		setDisplay(new Display());
		setShell(new Shell(getDisplay()));
		getShell().setText(_apllicationName);
		getShell().setLayout(new GridLayout(1, true));
		getShell().addDisposeListener(new DisposeListener() {			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				//when the user shutdown the UI, the program that was inspected keep going normally
				getController().setStatus(Status.UserExit);				
			}
		});			
		defineActions();		
		defineMenus();
		defineToolBar();
		
		//TabFolders
		setContainerTabs(new CTabFolder(getShell(), SWT.NULL));
		
		//Layout TabFolders
		GridData gridTabFolder = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);		
		gridTabFolder.grabExcessVerticalSpace = true;
		gridTabFolder.grabExcessHorizontalSpace = true;
		getContainerTabs().setLayoutData(gridTabFolder);
		
		setTabGeneralContents(new CTabFolder(getShell(), SWT.NULL));
		GridData gridTabGeneralInfo = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gridTabGeneralInfo.heightHint = 20;
		getTabGeneralContents().setSimple(false);
		_tabFolder.setBorderVisible(true);
		_tabFolder.setMRUVisible(true);
		getTabGeneralContents().setLayoutData(gridTabGeneralInfo);
		getTabGeneralContents().setMinimizeVisible(true);
		getTabGeneralContents().addCTabFolder2Listener(new CTabFolder2Listener() {			
			@Override
			public void showList(CTabFolderEvent arg0) {}
			
			@Override
			public void restore(CTabFolderEvent arg0) {
				getTabGeneralContents().setMaximized(true);
				GridData gridTabGeneralInfo = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
				gridTabGeneralInfo.heightHint = 20;
				getTabGeneralContents().setLayoutData(gridTabGeneralInfo);
				getShell().layout(true);
			}
			
			@Override
			public void minimize(CTabFolderEvent evt) {
				getTabGeneralContents().setMinimized(true);
				GridData gridTabGeneralInfo = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
				gridTabGeneralInfo.heightHint = 0;
				getTabGeneralContents().setLayoutData(gridTabGeneralInfo);
				getShell().layout(true);
			}			
			@Override
			public void maximize(CTabFolderEvent arg0) {}			
			@Override
			public void close(CTabFolderEvent arg0) {}
		});
		
		setTabGeneralInfo(new CTabItem(getTabGeneralContents(), SWT.NULL));
		getTabGeneralInfo().setText("General Info");
	}
	
	public void showUserInterface(){
		getShell().open();
		// Set up the event loop.
		while (!getShell().isDisposed()) {
			if (!getDisplay().readAndDispatch()) {
				// If no more entries in event queue
				getDisplay().sleep();
			}
		}
		getDisplay().dispose();
	}

	@Override
	public void threadFlowAdded(final ThreadFlow threadFlow) {
		getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				CTabItem tabItem = new CTabItem(getContainerTabs(), SWT.NULL);
				tabItem.setImage(ImageUtils.getImage(ImageUtils.ImageName.Thread.name()));
				tabItem.setText(threadFlow.toString());
				tabItem.setData(threadFlow);
				TraceView traceView = new TraceView(getContainerTabs(), SWT.NULL,getController());
				tabItem.setControl(traceView);
				getHashtTreadFlow().put(threadFlow, traceView);
				
				if(getContainerTabs().getItemCount()==1)
					getContainerTabs().setSelection(tabItem);
			}			
		});		
	}

	void setContainerTabs(CTabFolder _tabFolder) {
		_tabFolder.setSimple(false);
		_tabFolder.setBorderVisible(true);
		_tabFolder.setMRUVisible(true);
		this._tabFolder = _tabFolder;
	}

	CTabFolder getContainerTabs() {
		return _tabFolder;
	}

	private void setDisplay(Display _display) {
		this._display = _display;
	}

	private Display getDisplay() {
		return _display;
	}

	private void setShell(Shell _shell) {
		this._shell = _shell;
	}

	private Shell getShell() {
		return _shell;
	}

	private void setController(IController _controller) {
		this._controller = _controller;
	}

	private IController getController() {
		return _controller;
	}

	@Override
	public void abstractJoinPointAdded(AbstractJoinPoint abstractJoinPoint) {
		TraceView traceView = getHashtTreadFlow().get(abstractJoinPoint.getThreadFlow());		
		traceView.addAbstractJoinPoint(abstractJoinPoint);		
	}
	
	@Override
	public void timeChanged(int time)
	{
		int waitTime = time;
		String waitDescriptor = "playing at ";
		if(waitTime == 0)
			waitDescriptor += "real time";
		else
			waitDescriptor += waitTime +" s"; 
		_lblTime.setText(waitDescriptor);	
	}
	
	@Override
	public void statusChanged(Status status) {
		if(status.equals(IModel.Status.Playing))
		{
			int waitTime = getController().getWaitTime();
			if(waitTime==0)
				_lblTime.setText("playing at real time");
			else
				_lblTime.setText("playing at "+waitTime+"s      ");
		}
		else if(status.equals(IModel.Status.Paused))
			_lblTime.setText("paused");
	}

	// TODO Check why this method was defined here. --mms
//	private void setHashtTreadFlow(HashMap<ThreadFlow, TraceView> _hashtTreadFlow) {
//		this._hashtTreadFlow = _hashtTreadFlow;
//	}

	private HashMap<ThreadFlow, TraceView> getHashtTreadFlow() {
		return _hashtTreadFlow;
	}

	@Override
	public void viewInstanceDetail(Instance instance, long nanoSecondsOfSnapshot) {
		CTabItem tabItem = getCTabItemByName(instance.getInstanceName());
		if(tabItem==null)
		{
			tabItem = new CTabItem(getContainerTabs(), SWT.NULL);
			tabItem.setImage(ImageUtils.getImage(ImageUtils.ImageName.InstanceDetail.name()));
			tabItem.setShowClose(true);
			tabItem.setText(instance.getInstanceName());
			InstanceDetailView instanceDetailView = new InstanceDetailView(getContainerTabs(), SWT.NULL, getController(),instance, nanoSecondsOfSnapshot); 
			tabItem.setData(TabFolderData.Object.name(),instanceDetailView);
			tabItem.setControl(instanceDetailView);
		}
		else
		{
			InstanceDetailView instanceDetailView = (InstanceDetailView)tabItem.getData(TabFolderData.Object.name());
			instanceDetailView.selectSnapshotThatIsNearByTime(nanoSecondsOfSnapshot);
		}
		getContainerTabs().setSelection(tabItem);
	}
	
	private CTabItem getCTabItemByName(String tabName)
	{
		for (CTabItem item : getContainerTabs().getItems()) {
			if(item.getText().equals(tabName))
				return item;
		}
		return null;
	}

	@Override
	public void setGeneralDataInformation(Snapshot snapshot) {
		Composite composite = new Composite(getTabGeneralContents(), SWT.NULL);
		composite.setLayout(new GridLayout(3,false));
		GridData gridComposite = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
		composite.setLayoutData(gridComposite);
		
		Label lblBeforeText = new Label(composite,SWT.NULL);			
		lblBeforeText.setText("Snapshot timestamp:");
		lblBeforeText.setFont(new Font(getDisplay(), "Arial", 8, SWT.BOLD));
		
		Label lblImageBefore = new Label(composite,SWT.NULL);
		lblImageBefore.setImage(ImageUtils.getImage(ImageUtils.ImageName.Clock.name()));
		lblImageBefore.setToolTipText("Snapshot timestamp");
						
		new Label(composite,SWT.NULL).setText(snapshot.getTimestamp().toString()+" - "+snapshot.getNanoSeconds()+" ns");
		
		getTabGeneralInfo().setControl(composite);
		getTabGeneralContents().setSelection(getTabGeneralInfo());
	}

	@Override
	public void setGeneralJoinPointInformation(final AbstractJoinPoint abstractJoinPoint) {
		getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				Composite composite = new Composite(getTabGeneralContents(), SWT.NULL);
				composite.setLayout(new GridLayout(6,false));
				GridData gridComposite = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
				composite.setLayoutData(gridComposite);
				
				Label lblBeforeText = new Label(composite,SWT.NULL);			
				lblBeforeText.setText("Before:");
				lblBeforeText.setFont(new Font(getDisplay(), "Arial", 8, SWT.BOLD));
				
				Label lblImageBefore = new Label(composite,SWT.NULL);
				lblImageBefore.setImage(ImageUtils.getImage(ImageUtils.ImageName.Clock.name()));
				lblImageBefore.setToolTipText("Before Timestamp with nanoseconds");
								
				new Label(composite,SWT.NULL).setText(abstractJoinPoint.getEventBefore().getTimeStamp().toString()+" - "+abstractJoinPoint.getEventBefore().getNanoTime()+" ns");
								
				Label lblAfterText = new Label(composite,SWT.NULL);			
				lblAfterText.setText("After:");
				lblAfterText.setFont(new Font(getDisplay(), "Arial", 8, SWT.BOLD));
				
				Label lblImageAfter = new Label(composite,SWT.NULL);
				lblImageAfter.setImage(ImageUtils.getImage(ImageUtils.ImageName.Clock.name()));
				lblImageAfter.setToolTipText("After Timestamp with nanoseconds");
				
				if(abstractJoinPoint.getEventAfter()!=null)
					new Label(composite,SWT.NULL).setText(abstractJoinPoint.getEventAfter().getTimeStamp().toString()+" - "+abstractJoinPoint.getEventAfter().getNanoTime()+" ns");
				else
					new Label(composite,SWT.NULL).setText("waiting...");
				
				getTabGeneralInfo().setControl(composite);
				getTabGeneralContents().setSelection(getTabGeneralInfo());
			}
		});
	}

	public void setTabGeneralInfo(CTabItem _tabTimeInfo) {
		this._tabGeneralInfo = _tabTimeInfo;
	}

	public CTabItem getTabGeneralInfo() {
		return _tabGeneralInfo;
	}

	private void setTabGeneralContents(CTabFolder _tabGeneralContents) {
		this._tabGeneralContents = _tabGeneralContents;
	}

	private CTabFolder getTabGeneralContents() {
		return _tabGeneralContents;
	}

	@Override
	public void showJoinPoint(AbstractJoinPoint abstractJoinPoint) {
		//retrieve the tab that contains all the information about the flow control of this thread
		CTabItem threadItem = getCTabItemByName(abstractJoinPoint.getThreadFlow().toString());
		if(threadItem!=null)
		{
			getContainerTabs().setSelection(threadItem);
			TraceView traceview = getHashtTreadFlow().get(abstractJoinPoint.getThreadFlow());
			if(traceview!=null)
			{
				traceview.selectAbstractJoinPoint(abstractJoinPoint);
			}
		}
	}
}
