package com.ericsson.eiffel.remrem.generate.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.eiffel.remrem.generate.cli.CLIOptions;
import com.ericsson.eiffel.remrem.generate.listener.DirectoryWatchService;
import com.ericsson.eiffel.remrem.generate.listener.SimpleJarDirectoryWatchService;

@Component("jarHelper") public class RemremJarHelper {

	@Value("${jar.path}") private String jarPath;

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}
	
	@PostConstruct public void init() {
		try{
			if(CLIOptions.getCommandLine()==null ){
				addJarsToClassPath(jarPath);
				lookupForJarFileChanges();
			}
			else if(!CLIOptions.getCommandLine().hasOption("jarPath")){
				addJarsToClassPath(jarPath);
			}
		}catch(IOException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
	
	private void lookupForJarFileChanges() throws IOException {
		System.out.println("Listening to the Jar Location");
        final DirectoryWatchService jarPathListener = new SimpleJarDirectoryWatchService();
        String jarPathLocation = getJarPath();
        if (jarPathLocation != null) {
        	System.out.println("Creating path listener");
            jarPathListener.register(new DirectoryWatchService.OnFileChangeListener() {
                @Override
                public final void onFileCreate(final String filePath) {
                    try{
                    	System.out.println("File created :: " + filePath);
                	    addJarsToClassPath(filePath);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public final void onFileModify(final String filePath) {
                    try{
                	    addJarsToClassPath(filePath);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public final void onFileDelete(final String filePath) {
                    try{
                	    addJarsToClassPath(filePath);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }, jarPathLocation);
            jarPathListener.start();
        }
    }
	
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public static void addJarsToClassPath(String jarsDirectoryPath) throws Exception {
		System.out.println("Listening to changes in :: " + jarsDirectoryPath);
	    File f = new File(jarsDirectoryPath);
	    URL u = f.toURL();
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{u});
	}
}