package com.mfgeek.gb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.mfgeek.gb.ProtoGenerator;

public class ProtoGeneratorTest {

	@Test
	public void testGetIgnores() {
		ProtoGenerator pg = ProtoGenerator.newInstance();
		List<String> is = pg.getIgnores(); 
		assertNotNull(is);
		assertTrue(is.size() == 2);
		
	}

}
