package br.com.poc.poc_gateway;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@RibbonClients(defaultConfiguration = LoadBalancer.class)
public class PocGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocGatewayApplication.class, args);
	}

}

@RestController
class TodoController {

	@GetMapping("/")
	Object getTodos() {
		return Arrays.asList("Prepare talk..." + Instant.now());
	}
}

class LoadBalancer{

	@Autowired
	IClientConfig ribbonClientConfig;

	@Bean
	public IPing ribbonPing() {

		return new PingUrl(false,getRoute() + "/ping");
	}

	private String getRoute() {
		return RequestContext.getCurrentContext().getRequest().getServletPath();
	}
}