package com.mfgeek.gb;

import java.util.ArrayList;
import java.util.List;

public class ProtoFile {
	private static final String INDENT = "\t";
	private String name;
	private String javaOuterClassName = null;
	private String packageName;
	private int index = 1;
	
	private List<ProtoField> fields = new ArrayList<ProtoField>();
	
	
	public void appendField(ProtoField field){
		fields.add(field);
	}
	

	public void appendField(boolean required, String type, String name, String def){
		fields.add(new ProtoField(required, type, name, def, index++));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavaOuterClassName() {
		return javaOuterClassName;
	}

	public void setJavaOuterClassName(String javaOuterClassName) {
		this.javaOuterClassName = javaOuterClassName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		appendLine(buf, "package " + packageName);
		appendLine(buf, "option java_outer_classname = \"" + javaOuterClassName + "\"");
		appendOpenQuote(buf, "message " + name);
		for(ProtoField f: fields)
			appendLine(buf, INDENT + f.toString());
		appendClosedQuote(buf);
		return buf.toString();
	}
	
	void appendLine(StringBuffer buf, String str){
		buf.append(str + ";\n");
	}
	
	void appendOpenQuote(StringBuffer buf, String str){
		buf.append(str + " {\n");
	}
	
	void appendClosedQuote(StringBuffer buf){
		buf.append("}\n");
	}
	

	class ProtoField{
		boolean required = true;
		private String type;
		private String name;
		private String defaultValue;
		private int index;
		ProtoField(boolean required, String type, String name, String defaultValue, int in){
			this.required = required;
			this.type = type;
			this.name = name;
			this.defaultValue = defaultValue;
			index = in;
		}
		public String toString(){
			return (required?"required":"optional") + " " + type + " " 
					+ name + " = " + index + " " 
					+ (defaultValue == null?"":("[ "+defaultValue+" ]"));
		}
	}
	
	
}
