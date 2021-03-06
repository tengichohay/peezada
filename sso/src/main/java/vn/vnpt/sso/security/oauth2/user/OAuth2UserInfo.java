package vn.vnpt.sso.security.oauth2.user;

import java.util.Map;

public abstract class OAuth2UserInfo {
    /**
     * List chứa thông tin các thuộc tính của user
     */
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public OAuth2UserInfo() {
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
