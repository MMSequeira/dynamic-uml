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

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pt.iscte.dcti.visual_tracer.utils.FileUtils;
//import pt.iscte.dcti.visual_tracer.view.ImageUtils.ImageName;


public class GnuView extends Dialog {
	
	private static String _gnuPublicLicenseText = null;

	protected GnuView(Shell parent, int style) {
		super(parent, style);
		initComponents();
	}
	
	private void initComponents()
	{
		Display display = getParent().getDisplay();
		final Shell shell = new Shell(getParent(),SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
		shell.setText("GNU General Public License");
		shell.setLayout(new GridLayout(3,false));
		shell.setImage(ImageUtils.getImage(ImageUtils.ImageName.Gnu.name()));
		
		Browser browser = new Browser(shell, SWT.NULL);
		GridData gridForBrowser = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		gridForBrowser.horizontalIndent=0;
		gridForBrowser.verticalIndent=0;
		browser.setLayoutData(gridForBrowser);
		
		if(gnuPublicLincenseTextLoaded())
			browser.setText(getGnuPublicLicenseText());
		else
		{
			File file = new File("Templates/GPL.txt");
			if(file.exists())
				try {
					String gnuPublicLicense = FileUtils.getTextFile(file);
					setGnuPublicLicenseText(gnuPublicLicense);
					browser.setText(getGnuPublicLicenseText());					
				} catch (IOException e) {
					browser.setText("Error reading about information");
				}		
		}
		
		shell.setSize(700, 670);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void setGnuPublicLicenseText(String _gnuPublicLicenseText) {
		GnuView._gnuPublicLicenseText = _gnuPublicLicenseText;
	}

	private static String getGnuPublicLicenseText() {
		return _gnuPublicLicenseText;
	}
	
	private static boolean gnuPublicLincenseTextLoaded()
	{
		return getGnuPublicLicenseText()!=null;
	}
}
