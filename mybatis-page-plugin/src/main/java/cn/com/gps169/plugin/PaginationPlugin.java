package cn.com.gps169.plugin;

import java.util.Iterator;
import java.util.List;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellRunner;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class PaginationPlugin extends PluginAdapter {
	public PaginationPlugin() {
	}

	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		addLimit(topLevelClass, introspectedTable, "limitStart");
		addLimit(topLevelClass, introspectedTable, "limitEnd");
		return super.modelExampleClassGenerated(topLevelClass,
				introspectedTable);
	}

	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		XmlElement isNotNullElement = new XmlElement("if");
		isNotNullElement.addAttribute(new Attribute("test",
				"limitStart != null and limitStart>-1"));
		isNotNullElement.addElement(new TextElement(
				"limit ${limitStart} , ${limitEnd}"));
		element.addElement(isNotNullElement);
		return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
				introspectedTable);
	}

	public boolean sqlMapInsertElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		IntrospectedColumn columnIden;
		label0: {
			List columns = introspectedTable.getPrimaryKeyColumns();
			columnIden = null;
			if (columns == null || columns.size() == 0)
				break label0;
			Iterator i$ = columns.iterator();
			IntrospectedColumn column;
			do {
				if (!i$.hasNext())
					break label0;
				column = (IntrospectedColumn) i$.next();
			} while (!"id".equals(column.getJavaProperty())
					|| !"INTEGER".equals(column.getJdbcTypeName()));
			columnIden = column;
		}
		if (columnIden != null) {
			element.addAttribute(new Attribute("useGeneratedKeys", "true"));
			element.addAttribute(new Attribute("keyProperty", columnIden
					.getJavaProperty()));
		}
		return super.sqlMapInsertElementGenerated(element, introspectedTable);
	}

	private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) { 
		CommentGenerator commentGenerator = context.getCommentGenerator(); 
		Field field = new Field(); 
		field.setVisibility(JavaVisibility.PROTECTED); 
		field.setType(FullyQualifiedJavaType.getIntInstance()); 
		field.setName(name); field.setInitializationString("-1"); 
		commentGenerator.addFieldComment(field, introspectedTable); 
		topLevelClass.addField(field); 
		char c = name.charAt(0); 
		String camel = (new StringBuilder()).append(Character.toUpperCase(c)).append(name.substring(1)).toString(); 
		Method method = new Method(); 
		method.setVisibility(JavaVisibility.PUBLIC); 
		method.setName((new StringBuilder()).append("set").append(camel).toString()); 
		method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name)); 
		method.addBodyLine((new StringBuilder()).append("this.").append(name).append("=").append(name).append(";").toString()); 
		commentGenerator.addGeneralMethodComment(method, introspectedTable); 
		topLevelClass.addMethod(method); method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC); 
		method.setReturnType(FullyQualifiedJavaType.getIntInstance()); 
		method.setName((new StringBuilder()).append("get").append(camel).toString()); 
		method.addBodyLine((new StringBuilder()).append("return ").append(name).append(";").toString()); 
		commentGenerator.addGeneralMethodComment(method, introspectedTable); topLevelClass.addMethod(method); 
	}	
	
	public boolean validate(List warnings) {
		return true;
	}

	public static void generate() {
		String config = PaginationPlugin.class.getClassLoader().getResource("mybatisConfig.xml").getFile();
		String arg[] = { "-configfile", config, "-overwrite" };
		ShellRunner.main(arg);
	}

	public static void main(String args[]) {
		generate();
	}
}
