package com.fr.design.report;import com.fr.write.ReportWriteAttrProvider;import com.fr.write.ValueVerifierProvider;import com.fr.write.WClassVerifierProvider;import com.fr.general.NameObject;import com.fr.data.Verifier;import com.fr.design.beans.BasicBeanPane;import com.fr.design.gui.controlpane.NameObjectCreator;import com.fr.design.gui.controlpane.NameableCreator;import com.fr.design.gui.controlpane.ObjectJControlPane;import com.fr.design.gui.ilist.JNameEdList;import com.fr.design.gui.ilist.ListModelElement;import com.fr.design.layout.FRGUIPaneFactory;import com.fr.design.mainframe.ElementCasePane;import com.fr.general.Inter;import com.fr.stable.Nameable;import com.fr.stable.bridge.StableFactory;import javax.swing.*;import java.awt.*;import java.util.*;/** * Author : Shockway * Date: 13-7-29 * Time: 下午5:11 */public class VerifierListPane extends ObjectJControlPane {	public VerifierListPane(ElementCasePane ePane) {		super(ePane);	}	/**	 * 创建选项	 * @return 选项	 */	public NameableCreator[] createNameableCreators() {		return new NameableCreator[] {				new NameObjectCreator(Inter.getLocText(new String[]{"BuildIn", "Verify"}),				"/com/fr/web/images/reportlet.png",//				ValueVerifier.class,//				ReportWriteAttr.class,				StableFactory.getRegisteredClass(ReportWriteAttrProvider.XML_TAG),				BuildInVerifierPane.class),				new NameObjectCreator(				Inter.getLocText(new String[]{"Custom", "Verify"}),				"/com/fr/web/images/reportlet.png",//				WClassVerifier.class,				StableFactory.getRegisteredClass(WClassVerifierProvider.TAG),				CustomVerifierPane.class)		};	}	@Override	protected String title4PopupWindow() {		return null;	}	public void populate(ReportWriteAttrProvider reportWriteAttr) {		if (reportWriteAttr == null) {			return;		}		java.util.List<NameObject> nameObjectList = new ArrayList<NameObject>();		int verifierCount = reportWriteAttr.getVerifierCount();		boolean addedVVObject = false;		for (int i = 0; i < verifierCount; i++) {			Verifier verifier = reportWriteAttr.getVerifier(i);			String name = reportWriteAttr.getVerifierNameList(i);			// 内置的校验ValueVerifier只要加在一个面板中			if (verifier instanceof ValueVerifierProvider) {				if (!addedVVObject) {					nameObjectList.add(new NameObject(name, reportWriteAttr));					addedVVObject = true;				}			} else {				nameObjectList.add(new NameObject(name, verifier));			}		}		this.populate(nameObjectList.toArray(new NameObject[nameObjectList.size()]));	}	private static String valueVerifyName = Inter.getLocText("Verify-Data_Verify");	/**	 * 更新报表填报属性	 * @param reportWriteAttr 报表填报属性	 */	public void updateReportWriteAttr(ReportWriteAttrProvider reportWriteAttr) {		JNameEdList nameEdList = VerifierListPane.this.nameableList;		DefaultListModel model = (DefaultListModel) nameEdList.getModel();		for (int i=0; i<model.size(); i++) {			NameObject no = (NameObject)((ListModelElement)model.get(i)).wrapper;			if (no.getObject() instanceof ReportWriteAttrProvider) {				valueVerifyName = no.getName();				break;			}		}		Nameable[] res = this.update();		NameObject[] res_array = new NameObject[res.length];		java.util.Arrays.asList(res).toArray(res_array);		reportWriteAttr.clearVerifiers();		for (int i = 0; i < res_array.length; i++) {			NameObject nameObject = res_array[i];			if(nameObject.getObject() instanceof Verifier) {				reportWriteAttr.addVerifier(nameObject.getName(), (Verifier) nameObject.getObject());			} else if(nameObject.getObject() instanceof ReportWriteAttrProvider) {				ReportWriteAttrProvider ra = (ReportWriteAttrProvider) nameObject.getObject();				for (int k=0; k<ra.getValueVerifierCount(); k++) {					reportWriteAttr.addVerifier(nameObject.getName(), ra.getVerifier(k));				}			}		}	}	/**	 * 添加	 * @param nameable 添加的Nameable	 * @param index 序号	 */	public void addNameable(Nameable nameable, int index) {		JNameEdList nameEdList = VerifierListPane.this.nameableList;		DefaultListModel model = (DefaultListModel) nameEdList.getModel();		// 内置的数据校验大框架只加一个		if (((NameObject)nameable).getObject() instanceof ReportWriteAttrProvider) {			setToolbarDefEnable(0, 0, false);			for (int i=0; i<model.size(); i++) {				if (isBuildInVerifier(((NameObject)((ListModelElement)model.get(i)).wrapper).getObject())) {					nameableList.setSelectedIndex(i);				}			}		}		ListModelElement el = new ListModelElement(nameable);		model.add(index, el);		nameableList.setSelectedIndex(index);		nameableList.ensureIndexIsVisible(index);		nameEdList.repaint();	}	/**	 * 检查btn的状态	 */	public void checkButtonEnabled() {		super.checkButtonEnabled();		if (!hasBuildInVerifier()) {			setToolbarDefEnable(0, 0, true);		}	}	private boolean hasBuildInVerifier() {		JNameEdList nameEdList = VerifierListPane.this.nameableList;		DefaultListModel model = (DefaultListModel) nameEdList.getModel();		for (int i=0; i<model.size(); i++) {			if (isBuildInVerifier(((NameObject)((ListModelElement)model.get(i)).wrapper).getObject())) {				return true;			}		}		return false;	}	public static class BuildInVerifierPane extends BasicBeanPane<ReportWriteAttrProvider> {		private ValueVerifierEditPane valueVerifierEditPane;		public BuildInVerifierPane() {			this.setLayout(FRGUIPaneFactory.createBorderLayout());			valueVerifierEditPane = new ValueVerifierEditPane();			this.add(valueVerifierEditPane, BorderLayout.CENTER);		}		@Override		public void populateBean(ReportWriteAttrProvider ob) {			valueVerifierEditPane.populate(ob);		}		@Override		public ReportWriteAttrProvider updateBean() {			ReportWriteAttrProvider ra =  StableFactory.getMarkedInstanceObjectFromClass(ReportWriteAttrProvider.XML_TAG, ReportWriteAttrProvider.class);			valueVerifierEditPane.update(ra, VerifierListPane.valueVerifyName);			return ra;		}		@Override		protected String title4PopupWindow() {			return null;		}	}	public static class CustomVerifierPane extends BasicBeanPane<WClassVerifierProvider> {		private CustomVerifyJobPane pane;		public CustomVerifierPane() {			this.setLayout(FRGUIPaneFactory.createBorderLayout());			pane = new CustomVerifyJobPane();			this.add(pane, BorderLayout.CENTER);		}		@Override		public void populateBean(WClassVerifierProvider ob) {			this.pane.populateBean(ob.getClassVerifyJob());		}		@Override		public WClassVerifierProvider updateBean() {			WClassVerifierProvider cs = StableFactory.getMarkedInstanceObjectFromClass(WClassVerifierProvider.TAG, WClassVerifierProvider.class);			cs.setClassVerifyJob(this.pane.updateBean());			return cs;		}		@Override		protected String title4PopupWindow() {			return "custom";		}	}	private boolean isBuildInVerifier(Object obj) {		return obj instanceof ReportWriteAttrProvider;	}}