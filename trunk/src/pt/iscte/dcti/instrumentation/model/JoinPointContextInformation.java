package pt.iscte.dcti.instrumentation.model;

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
 */
public class JoinPointContextInformation {

	private String _fileName;
	private int _columnNumber;
	private int _lineNumber;
	private String _signature;
	private String _kind;
	
	public JoinPointContextInformation(String fileName, int column, int line, String signature, String kind)
	{
		setColumnNumber(column);
		setFileName(fileName);
		setKind(kind);
		setLineNumber(line);
		setSignature(signature);
	}
	
	public void setFileName(String _fileName) {
		this._fileName = _fileName;
	}
	
	public String getFileName() {
		return _fileName;
	}
	
	public void setColumnNumber(int _columnNumber) {
		this._columnNumber = _columnNumber;
	}
	
	public int getColumnNumber() {
		return _columnNumber;
	}
	
	public void setLineNumber(int _lineNumber) {
		this._lineNumber = _lineNumber;
	}
	
	public int getLineNumber() {
		return _lineNumber;
	}
	
	public void setSignature(String _signature) {
		this._signature = _signature;
	}
	
	public String getSignature() {
	
		return _signature;
	}
	
	public void setKind(String _kind) {
		this._kind = _kind;
	}
	
	public String getKind() {
		return _kind;
	}
}
