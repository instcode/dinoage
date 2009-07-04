package org.ddth.dinoage.eclipse.ui.model;

import java.io.File;

import org.ddth.blogging.Blog;

public class ExportModel {

	private File outputFile;
	private Blog blog;

	public ExportModel(Blog blog, File outputFile) {
		this.blog = blog;
		this.outputFile = outputFile;
	}
	
	public Blog getBlog() {
		return blog;
	}
	
	public void setBlog(Blog blog) {
		this.blog = blog;
	}
	
	public File getOutputFile() {
		return outputFile;
	}
	
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
}
