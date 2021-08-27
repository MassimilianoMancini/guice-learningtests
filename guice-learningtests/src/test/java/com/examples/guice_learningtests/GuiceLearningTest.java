package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

class GuiceLearningTest 
{
	private static interface IMyService{}
	private static class MyService implements IMyService{} 
	
	private static class MyClient {
		MyService service;
		
		@Inject
		public MyClient(MyService service) {
			this.service = service;
		}
	}
	
	private static class MyGenericClient {
		IMyService service;
		
		@Inject
		public MyGenericClient(IMyService service) {
			this.service = service;
		}
	}
	
	@Test
	void canInstantiateConcreteClassesWithoutConfiguration() {
		Module module = new AbstractModule() {};
		Injector injector = Guice.createInjector(module);
		MyClient client = injector.getInstance(MyClient.class);
		assertThat(client.service).isNotNull();
	}
	
	@Test
	void injectAbstractType() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client = injector.getInstance(MyGenericClient.class);
		assertThat(client.service).isNotNull();
		assertThat(MyService.class).isEqualTo(client.service.getClass());
	}
	
	@Test
	void bindToInstance() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).toInstance(new MyService());
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client1 = injector.getInstance(MyGenericClient.class);
		MyGenericClient client2 = injector.getInstance(MyGenericClient.class);
		assertThat(client1.service).isNotNull().isEqualTo(client2.service);
	}

}
