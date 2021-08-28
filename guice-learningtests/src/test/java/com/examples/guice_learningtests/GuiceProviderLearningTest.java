package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

class GuiceProviderLearningTest {
	private static interface IMyService {}
	
	private static class MyService implements IMyService {}
	
	private static class MyClientWithInjectedProvider {
		@Inject
		Provider<IMyService> serviceProvider;
		
		IMyService getService() {
			return serviceProvider.get();
		}
		
	}
	
	private static class MyFileWrapper {
		@Inject
		File file;
	}
	
	@Test
	void injectProviderExample() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedProvider client = injector.getInstance(MyClientWithInjectedProvider.class);
		assertThat(client.getService()).isNotNull();
	}
	
	@Test
	void providerBindings() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(File.class).toProvider(() -> new File("src/test/resources/afile.txt"));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper fileWrapper = injector.getInstance(MyFileWrapper.class);
		assertThat(fileWrapper.file).exists();
	}

}
