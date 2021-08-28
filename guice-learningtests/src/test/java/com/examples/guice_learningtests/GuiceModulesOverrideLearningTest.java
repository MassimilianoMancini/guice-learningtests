package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;

class GuiceModulesOverrideLearningTest {
	
	private static interface IMyService{}
	private static class MyService implements IMyService{}
	
	private static class MyClient {
		MyService service;
		
		@Inject
		public MyClient(MyService service) {
			this.service = service;
		}
	}
	
	@Test
	void modulesOverride() {
		Module defaultModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		
		Injector injector = Guice.createInjector(defaultModule);
		MyClient client1 = injector.getInstance(MyClient.class);
		MyClient client2 = injector.getInstance(MyClient.class);
		// not singleton
		assertThat(client1.service).isNotSameAs(client2.service);
		
		Module customModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(MyService.class).in(Singleton.class);
			}
		};
		injector = Guice.createInjector(Modules.override(defaultModule).with(customModule));
		client1 = injector.getInstance(MyClient.class);
		client2 = injector.getInstance(MyClient.class);
		assertThat(client1.service).isNotNull().isSameAs(client2.service);
	}

}
