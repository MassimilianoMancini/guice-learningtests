package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

class GuiceFieldAndMethodInjectonLearningTest {

	private static interface IMyService {
	}

	private static class MyService implements IMyService {
	}

	private static class MyClientWithInjectedField {
		@Inject
		IMyService service;
	}

	private static class MyClientWithInjectedMethod {
		IMyService service;

		@Inject
		public void init(IMyService service) {
			this.service = service;
		}
	}

	@Test
	void fieldAndMethodInjection() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedField client1 = injector.getInstance(MyClientWithInjectedField.class);
		MyClientWithInjectedMethod client2 = injector.getInstance(MyClientWithInjectedMethod.class);
		assertThat(client1.service).isNotNull();
		assertThat(client2.service).isNotNull();
	}
	
	@Test
	void injectMembers() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedField client1 = new MyClientWithInjectedField();
		injector.injectMembers(client1);
		MyClientWithInjectedMethod client2 = new MyClientWithInjectedMethod();
		injector.injectMembers(client2);
		assertThat(client1).isNotNull();
		assertThat(client2).isNotNull();
	}

}
