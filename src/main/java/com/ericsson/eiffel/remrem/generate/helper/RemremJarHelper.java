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
            else if(!CLIOptions.getCommandLine().hasOption("jp")){
                addJarsToClassPath(jarPath);
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    private void lookupForJarFileChanges() throws IOException {
	    System.out.println("Listening to the Jar Path");
        final DirectoryWatchService jarPathListener = new SimpleJarDirectoryWatchService();
        String jarPath = getJarPath();
        if (jarPath != null) {
            System.out.println("Creating path listener");
            jarPathListener.register(new DirectoryWatchService.OnFileChangeListener() {
                @Override
                public final void onFileCreate(final String filePath) {
                    addJarsToClassPath(filePath);
                }

                @Override
                public final void onFileModify(final String filePath) {
                    addJarsToClassPath(filePath);
                }

                @Override
                public final void onFileDelete(final String filePath) {
                    addJarsToClassPath(filePath);
                }
            }, jarPath);
            jarPathListener.start();
        }
    }
	
    public static void addJarsToClassPath(String jarPath){
        System.out.println("Listening to changes in :: " + jarPath);
        if(jarPath!=null ){
            File f = new File(jarPath);
            try{
                URL u = f.toURL();
                URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class urlClass = URLClassLoader.class;
                Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(urlClassLoader, new Object[]{u});
            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
