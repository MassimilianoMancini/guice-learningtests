package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

class GuiceBindingAnnotationTest {

	private static class MyFileWrapper {
		File file;
		
		@Inject
		public MyFileWrapper(@Named("PATH") String path, @Named("NAME") String name) {
			file = new File(path, name);
		}
	}
	
	@Test
	void bindingAnnotations() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(Names.named("PATH")).toInstance("src/test/resources");
				bind(String.class).annotatedWith(Names.named("NAME")).toInstance("afile.txt");
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper fileWrapper = injector.getInstance(MyFileWrapper.class);
		assertThat(fileWrapper.file).exists();			
	}
	
	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	private static @interface FilePath {}
	
	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	private static @interface FileName {}
	
	private static class MyFileWrapper2 {
		File file;
		
		@Inject
		public MyFileWrapper2(@FilePath String path, @FileName String name) {
			file = new File(path, name);
		}
	}
	
	@Test
	void customBindingAnnotations() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(FilePath.class).toInstance("src/test/resources");
				bind(String.class).annotatedWith(FileName.class).toInstance("afile.txt");
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper2 fileWrapper = injector.getInstance(MyFileWrapper2.class);
		assertThat(fileWrapper.file).exists();			
	}
}
