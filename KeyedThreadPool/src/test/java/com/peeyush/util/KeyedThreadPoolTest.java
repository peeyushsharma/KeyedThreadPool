package com.peeyush.util;

import static org.junit.Assert.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Test;

public class KeyedThreadPoolTest {

	@Test
	public void test() {
		
		KeyedThreadPoolExecutor<String> executor = new KeyedThreadPoolExecutor<String>(4, Executors.defaultThreadFactory());
		
		executor.execute("A", new Runnable() {
			public void run() {				
			}
		});
		
		executor.execute("B", new Runnable() {
			public void run() {				
			}
		});
		
		executor.execute("C", new Runnable() {
			public void run() {				
			}
		});
		
		executor.execute("D", new Runnable() {
			public void run() {				
			}
		});
		
		
		executor.execute("A1", new Runnable() {
			public void run() {				
			}
		});
		
		executor.execute("B2", new Runnable() {
			public void run() {				
			}
		});
		
		executor.execute("C2", new Runnable() {
			public void run() {				
			}
		});
		
		executor.execute("D2", new Runnable() {
			public void run() {				
			}
		});
		
		assertEquals( 8 , executor.keyExecutorMap.size() );
		
		for(Executor t : executor.executors){
			int c = 0;
			for(Executor t1 : executor.keyExecutorMap.values()){
				if( t == t1 )
					c++;
			}
			assertEquals(2, c);
		}
		
	}

}
