package com.fr.design.mainframe.widget.editors;/** * Author : Shockway * Date: 13-9-29 * Time: ����9:46 */public class SpinnerMaxNumberEditor extends SpinnerNumberEditor {	public SpinnerMaxNumberEditor(Object o) {		super(o);	}	protected Double getDefaultLimit() {		return Double.MAX_VALUE;	}	protected Double getLimitValue() {		return this.widget.getMaxValue();	}}