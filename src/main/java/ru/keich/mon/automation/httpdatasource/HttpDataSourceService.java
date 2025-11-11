package ru.keich.mon.automation.httpdatasource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.vaadin.flow.data.provider.Query;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Log
public class HttpDataSourceService {

	private final HttpDataSourceRepository httpDataSourceRepository;
	
	private final Map<String, WebClient> cache = new ConcurrentHashMap<>();
	
	private final WebClient baseClient;
	
	public HttpDataSourceService(HttpDataSourceRepository httpDataSourceRepository) throws SSLException {
		this.httpDataSourceRepository = httpDataSourceRepository;
		
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(2621440)).build();
		var sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		var httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
		baseClient = WebClient
				.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.exchangeStrategies(strategies).build();
	}
	
	public Stream<HttpDataSource> getAll(Query<HttpDataSource, Void> q) {
		return httpDataSourceRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}
	
	public int getCount(Query<HttpDataSource, Void> q) {
		return Math.toIntExact(httpDataSourceRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit()).count());
	}
	
	public void save(HttpDataSource dataSource) {
		httpDataSourceRepository.save(dataSource);
		cache.remove(dataSource.getName());
	}
	
	public void delete(HttpDataSource dataSource) {
		httpDataSourceRepository.delete(dataSource);
		cache.remove(dataSource.getName());
	}
	
	private WebClient getWebClient(HttpDataSource dataSource) {
		return baseClient.mutate().baseUrl(dataSource.getBaseUrl()).build();
	}
	
	private WebClient getWebClient(String name) {
		return cache.compute(name, (key, ds) -> {
			if (ds == null) {
				try {
					ds = httpDataSourceRepository.findById(name).map(this::getWebClient).orElse(null);
				} catch (RuntimeException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
				}
			}
			return ds;
		});
	}

	public HttpResult getRequest(String name, String path, Map<String, List<String>> params, Map<String, String> headers) {
		return getWebClient(name)
				.get()
				.uri(uriBuilder -> {
					var uri = uriBuilder.path(path);
					params.entrySet().forEach(e -> {
						uri.queryParam(e.getKey(), e.getValue());
					});
					return uri.build();
				})
				.headers(ch -> {
					headers.entrySet().forEach(e -> {
						ch.add(e.getKey(), e.getValue());
					});
				})
				.exchangeToMono(response -> {
					var result = new HttpResult();
					result.setStatus(response.statusCode().value());
					if (response.statusCode().is2xxSuccessful()) {
						return response.bodyToMono(String.class).map(data -> {
							result.setData(data);
							return result;
						});		
					}
					return Mono.just(result);
				})
				.onErrorResume(e -> {
					var result = new HttpResult();
					result.setStatus(0);
					result.setErrMessage(e.getMessage());
					return Mono.just(result);
				})
				.block();
	}

	public HttpResult postRequest(String name, String path, Map<String, List<String>> params, Map<String, String> headers,
			String data) {
		return getWebClient(name)
				.post()
				.uri(uriBuilder -> {
					var uri = uriBuilder.path(path);
					params.entrySet().forEach(e -> {
						uri.queryParam(e.getKey(), e.getValue());
					});
					return uri.build();
				})
				.headers(ch -> {
					headers.entrySet().forEach(e -> {
						ch.add(e.getKey(), e.getValue());
					});
				})
				.bodyValue(data)
				.exchangeToMono(response -> {
					var result = new HttpResult();
					result.setStatus(response.statusCode().value());
					if (response.statusCode().is2xxSuccessful()) {
						return response.bodyToMono(String.class).map(body -> {
							result.setData(body);
							return result;
						});		
					}
					return Mono.just(result);
				})
				.onErrorResume(e -> {
					var result = new HttpResult();
					result.setStatus(0);
					result.setErrMessage(e.getMessage());
					return Mono.just(result);
				})
				.block();
	}
	
	public HttpResult delRequest(String name, String path, Map<String, List<String>> params, Map<String, String> headers) {
		return getWebClient(name)
				.delete()
				.uri(uriBuilder -> {
					var uri = uriBuilder.path(path);
					params.entrySet().forEach(e -> {
						uri.queryParam(e.getKey(), e.getValue());
					});
					return uri.build();
				})
				.headers(ch -> {
					headers.entrySet().forEach(e -> {
						ch.add(e.getKey(), e.getValue());
					});
				})
				.exchangeToMono(response -> {
					var result = new HttpResult();
					result.setStatus(response.statusCode().value());
					if (response.statusCode().is2xxSuccessful()) {
						return response.bodyToMono(String.class).map(data -> {
							result.setData(data);
							return result;
						});		
					}
					return Mono.just(result);
				})
				.onErrorResume(e -> {
					var result = new HttpResult();
					result.setStatus(0);
					result.setErrMessage(e.getMessage());
					return Mono.just(result);
				})
				.block();
	}
	
}
