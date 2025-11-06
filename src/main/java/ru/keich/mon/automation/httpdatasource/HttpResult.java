package ru.keich.mon.automation.httpdatasource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpResult {

	private int status = 0;
	private String errMessage = "";
	private String data = "";

}
