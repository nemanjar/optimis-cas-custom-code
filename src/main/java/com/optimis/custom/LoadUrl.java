package com.optimis.custom;

import org.springframework.webflow.execution.RequestContext;

/**
 * Created by nex on 13.3.16..
 */
public class LoadUrl {

    private String url;

    public void loadUrlsFromWebflow(RequestContext context) {
        context.getFlowScope().put("defaultUrl", this.url);
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
