package com.bsecure.apha.callbacks;

public interface JsonHandler {

	public void onResponse(Object results, int requestType);

	public void onFailure(String errorCode, int requestType);

}