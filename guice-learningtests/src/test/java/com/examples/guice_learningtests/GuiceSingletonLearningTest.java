package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

class GuiceSingletonLearningTest {
	
	private static interface IMyService{}
	private static class MyService implements IMyService{} 
	
	private static class MyClient {
		MyService service;
		
		@Inject
		public MyClient(MyService service) {
			this.service = service;
		}
	}
	
	@Provides @Singleton
	Module provideModule() {
		Module singletonModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		return singletonModule;
	}
	
	@Test
	void bindToSingleton() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
				bind(MyService.class).in(Singleton.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClient client1 = injector.getInstance(MyClient.class);
		MyClient client2 = injector.getInstance(MyClient.class);
		assertThat(client1.service).isNotNull().isSameAs(client2.service);
		
	}
	
	@Test
	void singletonPerInjector() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class).in(Singleton.class);
			}
		};
		assertThat(Guice.createInjector(module).getInstance(MyClient.class).service)
			.isNotSameAs(Guice.createInjector(module).getInstance(MyClient.class).service);
	}
	
	@Test
	void singletonPerInjectorWithAnnotation() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
			
			@Provides @Singleton
			protected MyService providesService() {
				return new MyService();
			}
		};
		
		Injector injector = Guice.createInjector(module);
		MyClient client1 = injector.getInstance(MyClient.class);
		MyClient client2 = injector.getInstance(MyClient.class);
		assertThat(client1.service).isNotNull().isSameAs(client2.service);
	}
}
