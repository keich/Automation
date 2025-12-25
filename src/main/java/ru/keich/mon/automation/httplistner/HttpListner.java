package ru.keich.mon.automation.httplistner;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class HttpListner {

	public static enum ContentType {
		HTML, JSON;
	}
	
	@Id
	private String path;

	private String scriptName;

	private boolean enable = false;
	
	private boolean permitAllAccess = false;
	
	private ContentType contentType;

	public boolean isValid() {
		return scriptName != null && !"".equals(scriptName) && path != null && !"".equals(path) && contentType != null;
	}

	public HttpListner setScriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}

	public HttpListner setPath(String path) {
		this.path = path;
		return this;
	}

	public HttpListner setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}
	
	public HttpListner setPermitAllAccess(boolean permitAllAccess) {
		this.permitAllAccess = permitAllAccess;
		return this;
	}

	public HttpListner setContentType(ContentType contentType) {
		this.contentType = contentType;
		return this;
	}

}
