package com.metro.auth.logging;

public class ApiLogger {
	private static String module = "na";
	private static boolean on = true;

	public ApiLogger(String module) {
		ApiLogger.module = module;
	}

	public ApiLogger() {
	}

	public void mlog(String message) {
		System.out.println(module.toUpperCase() + ": " + message);
	}

	public static void log(String message) {
		System.out.println(module.toUpperCase() + ": " + message);
	}

	public static void on() {
		ApiLogger.on = true;
	}

	public static void off() {
		ApiLogger.on = true;
	}

}
