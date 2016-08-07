package com.optimis.custom;

import org.springframework.webflow.execution.RequestContext;

/**
 * Created by nex on 13.3.16..
 */
public class LoadUrl {

    private String url;
    private String passwordChangeUrl;

    public void loadUrlsFromWebflow(RequestContext context) {
        context.getFlowScope().put("defaultUrl", this.url);
        context.getFlowScope().put("passwordChangeUrl", this.passwordChangeUrl);
    }

    public String getPasswordChangeUrl() {
        return passwordChangeUrl;
    }

    public void setPasswordChangeUrl(String passwordChangeUrl) {
        this.passwordChangeUrl = passwordChangeUrl;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
