package com.voodoodyne.gwizard.services.example;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.voodoodyne.gwizard.services.Services;
import com.voodoodyne.gwizard.services.ServicesModule;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;

/**
 * Self-contained example of using the ServicesModule by itself.
 */
@Slf4j
public class ServicesModuleExample {

	@Singleton
	@Slf4j
	public static class ExampleServiceListener extends Service.Listener {
		@Inject
		public ExampleServiceListener(Services services) {
			services.add(this);
		}

		@Override
		public void failed(Service.State from, Throwable failure) {
			log.info("failed ({})", from, failure);
		}

		@Override
		public void terminated(Service.State from) {
			log.info("terminated ({})", from);
		}

		@Override
		public void stopping(Service.State from) {
			log.info("stopping ({})", from);
		}

		@Override
		public void running() {
			log.info("running");
		}

		@Override
		public void starting() {
			log.info("starting");
		}

	}

	/**
	 * an example service that has minimal app startup/shutdown requirements
	 */
	@Singleton
	@Slf4j
	public static class ExampleService extends AbstractIdleService {
		@Inject
		public ExampleService(Services services) {
			services.add(this);
		}

		@Override
		protected void startUp() throws Exception {
			log.info("This is where my service does something at startup");
		}

		@Override
		protected void shutDown() throws Exception {
			log.info("This is where my service does something at shutdown");
		}
	}

	public static class ExampleModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(ExampleService.class).asEagerSingleton();
			bind(ExampleServiceListener.class).asEagerSingleton();
		}
	}

	public static void main(String[] args) throws Exception {
		final Injector injector = Guice.createInjector(
				new ServicesModule(),
				new ExampleModule());

		// start services
		injector.getInstance(ServiceManager.class).startAsync().awaitHealthy();


		// here's how you'd stop services
		// injector.getInstance(ServiceManager.class).stopAsync().awaitStopped(5, TimeUnit.SECONDS);

		// or, you can just exit, and the services will get a shutdown signal
		//System.exit(0);

		// or, just leave things running and wait for cntrl-C or other exit to kill the application
	}
}