/***************       BEGIN-STANDARD-COPYRIGHT      ***************

 Copyright (c) 2009-2026, Spirent Communications.

 All rights reserved. Proprietary and confidential information of Spirent Communications.
 ***************        END-STANDARD-COPYRIGHT       ***************/
package org.bukharov;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextBufferTest {

	@Test
	void appendTest() {
		TextBuffer tb = new TextBuffer();

		tb.append("abc");
		Assertions.assertEquals("abc", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertEquals('c', tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void deleteTest() {
		TextBuffer tb = new TextBuffer();

		tb.append("abcd");
		tb.delete(2);
		Assertions.assertEquals("ab", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertEquals('b', tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void deleteTest_bigArg() {
		TextBuffer tb = new TextBuffer();

		tb.append("abcd");
		tb.delete(6);
		Assertions.assertEquals("", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertEquals(null, tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void appendTest_nullArg() {
		TextBuffer tb = new TextBuffer();

		var ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tb.append(null));
		Assertions.assertEquals("Cannot append null", ex.getMessage());
	}

	@Test
	void deleteTest_negativeArg() {
		TextBuffer tb = new TextBuffer();

		var ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tb.delete(-1));
		Assertions.assertEquals("Cannot delete < 1 chars", ex.getMessage());
	}

	@Test
	void deleteTest_zeroArg() {
		TextBuffer tb = new TextBuffer();

		var ex = Assertions.assertThrows(IllegalArgumentException.class, () -> tb.delete(0));
		Assertions.assertEquals("Cannot delete < 1 chars", ex.getMessage());
	}

	@Test
	void undoAppendTest() {
		TextBuffer tb = new TextBuffer();

		tb.append("ab");
		tb.append("cd");
		tb.undo();
		Assertions.assertEquals("ab", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertEquals('b', tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void undoDeleteTest() {
		TextBuffer tb = new TextBuffer();

		tb.append("abcd");
		tb.delete(2);
		tb.undo();
		Assertions.assertEquals("abcd", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertEquals('d', tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void multipleUndoTest() {
		TextBuffer tb = new TextBuffer();

		tb.append("abcd");
		tb.delete(2);
		tb.append("ef");
		Assertions.assertEquals("abef", tb.getText(), "Unexpected text in the buffer");
		tb.undo();
		tb.undo();
		Assertions.assertEquals("abcd", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertEquals('d', tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void emptyBuffer_getLast() {
		TextBuffer tb = new TextBuffer();

		Assertions.assertNull(tb.getLast(), "Unexpected last character in the buffer");
	}

	@Test
	void emptyBuffer_undo() {
		TextBuffer tb = new TextBuffer();

		tb.undo();
		Assertions.assertEquals("", tb.getText(), "Unexpected text in the buffer");
		Assertions.assertNull(tb.getLast(), "Unexpected last character in the buffer");
	}
}
