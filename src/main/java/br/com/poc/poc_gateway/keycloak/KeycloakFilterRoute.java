package br.com.poc.poc_gateway.keycloak;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakFilterRoute extends ZuulFilter {
    private static final String AUTHORIZATION_HEADER = "authorization";

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getHeader(AUTHORIZATION_HEADER) == null) {
            addKeycloakTokenToHeader(ctx);
        }
        return null;
    }

    private void addKeycloakTokenToHeader(RequestContext ctx) {
        RefreshableKeycloakSecurityContext securityContext = getRefreshableKeycloakSecurityContext(ctx);
        if (securityContext != null) {
            ctx.addZuulRequestHeader(AUTHORIZATION_HEADER, buildBearerToken(securityContext));
        }
    }

    private RefreshableKeycloakSecurityContext getRefreshableKeycloakSecurityContext(RequestContext ctx) {
        if (ctx.getRequest().getUserPrincipal() instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) ctx.getRequest().getUserPrincipal();
            return (RefreshableKeycloakSecurityContext) token.getCredentials();
        }
        return null;
    }

    private String buildBearerToken(RefreshableKeycloakSecurityContext securityContext) {
        return "Bearer " + securityContext.getTokenString();
    }

}
