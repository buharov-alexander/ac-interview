package org.bukharov.page_loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PageLoader {
	private final ExecutorService executor =
			Executors.newFixedThreadPool(4);

	public List<String> loadAll(List<String> urls) throws InterruptedException, ExecutionException {

		List<Future<String>> futures = new ArrayList<>();

		for (String url : urls) {
			Future<String> future = executor.submit(() -> fetch(url));
			futures.add(future);
		}

		List<String> results = new ArrayList<>();
		for (Future<String> f : futures) {
			results.add(f.get());
		}

		return results;
	}

	public List<String> loadAllWithInvoke(List<String> urls) throws InterruptedException, ExecutionException {

		Collection<Callable<String>> collection = urls.stream()
				.map(url -> (Callable<String>) () -> fetch(url))
				.toList();
		List<Future<String>> futures = executor.invokeAll(collection);
		List<String> results = new ArrayList<>();
		for (Future<String> f : futures) {
			results.add(f.get());
		}

		return results;
	}

	private String fetch(String url) throws InterruptedException {
		// имитация запроса
		Thread.sleep(1000);
		return "content of " + url;
	}
}
