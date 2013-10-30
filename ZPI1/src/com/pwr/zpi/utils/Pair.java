package com.pwr.zpi.utils;


@SuppressWarnings("serial")
public class Pair<T, E> {
	
	public Pair(T first, E second) {
		this.first = first;
		this.second = second;
	}
	
	public T first;
	public E second;
}
