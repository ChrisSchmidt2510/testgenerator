package de.nvg.app;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Handler implements InvocationHandler, Greeter{

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return greet((String)args[0]);
	}

	@Override
	public String greet(String name) {
		return "Hello "+name;
	}

}
