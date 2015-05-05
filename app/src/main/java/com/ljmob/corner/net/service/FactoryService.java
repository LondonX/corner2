package com.ljmob.corner.net.service;

import android.content.Context;

public class FactoryService {
	public static ServiceImpl getIService(Context context) {
		return new ServiceImpl(context);
	}
}
