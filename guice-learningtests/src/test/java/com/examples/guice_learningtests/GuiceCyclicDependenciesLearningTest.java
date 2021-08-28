package com.examples.guice_learningtests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

class GuiceCyclicDependenciesLearningTest {
	
	private static interface IMyView {}
	
	private static interface IMyController {}
	
	private static interface IMyRepository {}
	
	private static class MyRepository implements IMyRepository {}
	
	private static class MyView implements IMyView {
		IMyController controller;
		
		public void setController(IMyController controller) {
			this.controller = controller;
		}
	}
	
	private static class MyController implements IMyController {
		IMyView view;
		IMyRepository repository;
		
		@Inject
		public MyController(@Assisted IMyView view, IMyRepository repository) {
			this.view = view;
			this.repository = repository;
		}
	}
	
	private static interface MyControllerFactory {
		IMyController create(IMyView view);
	}
	
	private static class MyViewProvider implements Provider<MyView> {
		@Inject
		private MyControllerFactory controllerFactory;
		
		@Override
		public MyView get() {
			// manually solve the cycle
			MyView view = new MyView();
			view.setController(controllerFactory.create(view));
			return view;
		}
	}
	
	@Test
	void cyclicDependencies() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyRepository.class).to(MyRepository.class);
				bind(MyView.class).toProvider(MyViewProvider.class);
				install(new FactoryModuleBuilder()
					.implement(IMyController.class, MyController.class)
					.build(MyControllerFactory.class));
			}
		};
		
		Injector injector = Guice.createInjector(module);
		MyView view = injector.getInstance(MyView.class);
		assertThat(view).isSameAs(((MyController) view.controller).view);
		assertThat(((MyController) view.controller).repository).isNotNull();
	}
	
	@Test
	void providesMethod() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyRepository.class).to(MyRepository.class);
				install(new FactoryModuleBuilder()
					.implement(IMyController.class, MyController.class)
					.build(MyControllerFactory.class));
			}
			
			@Provides
			MyView view(MyControllerFactory controllerFactory) {
				MyView view = new MyView();
				view.setController(controllerFactory.create(view));
				return view;
			}
		};
		Injector injector = Guice.createInjector(module);
		MyView view = injector.getInstance(MyView.class);
		assertThat(view).isSameAs(((MyController) view.controller).view);
		assertThat(((MyController) view.controller).repository).isNotNull();
	}

}
