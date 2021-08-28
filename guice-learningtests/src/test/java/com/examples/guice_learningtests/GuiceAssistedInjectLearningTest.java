package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.inject.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

class GuiceAssistedInjectLearningTest {

	private static interface IMyView {
	}

	private static class MyView implements IMyView {
	}

	private static interface IMyRepository {
	}

	private static class MyRepository implements IMyRepository {
	}

	private static interface IMyController {
	}

	private static class MyController implements IMyController {
		IMyView view;
		IMyRepository repository;

		@Inject
		public MyController(@Assisted IMyView view, IMyRepository repository) { 
			this.view = view; // from the instance's creator
			this.repository = repository; // from Injector
		}
	}
	
	private static interface MyControllerFactory {
		IMyController create(IMyView view);
	}
	
	@Test
	void assistedInject() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyRepository.class).to(MyRepository.class);
				install(new FactoryModuleBuilder()
					.implement(IMyController.class, MyController.class)
					.build(MyControllerFactory.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyControllerFactory controllerFactory = injector.getInstance(MyControllerFactory.class);
		MyController controller = (MyController) controllerFactory.create(new MyView());
		assertThat(controller.view).isNotNull();
		assertThat(controller.repository).isNotNull();
	}
	
}
