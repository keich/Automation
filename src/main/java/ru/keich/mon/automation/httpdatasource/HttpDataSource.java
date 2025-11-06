package ru.keich.mon.automation.httpdatasource;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class HttpDataSource {

	@Id
	private String name = "";
	
	private String baseUrl = "";
	
	public boolean isValid() {
		return !"".equals(name) && !"".equals(baseUrl) ;
	}
	
	public HttpDataSource setName(String name) {
		this.name = name;
		return this;
	}
	
	public HttpDataSource setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}
	
}
