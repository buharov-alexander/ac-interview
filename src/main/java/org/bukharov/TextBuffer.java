/**
 * Буфер текстового редактора с поддержкой отмены операций.
 * append добавляет текст, delete удаляет n последних символов, undo отменяет последнюю операцию.
 * Внутри — String + Stack истории операций.
 */
package org.bukharov;

import java.util.LinkedList;
import java.util.SequencedCollection;

public class TextBuffer {

	private String text;
	private SequencedCollection<Node> history;

	public TextBuffer() {
		this.text = "";
		this.history = new LinkedList<>();
	}

	public void append(String str) {
		if (str == null) {
			throw new IllegalArgumentException("Cannot append null");
		}
		text = text + str;
		history.addLast(new Node(OperationType.APPEND, str));
	}

	public void delete(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("Cannot delete < 1 chars");
		}
		int left = Math.max(0, text.length() - n);
		String removed = text.substring(left);
		text = text.substring(0, left);
		history.addLast(new Node(OperationType.DELETE, removed));
	}

	public void undo() {
		if (history.isEmpty()) return;
		Node last = history.getLast();
		revert(last);
		history.removeLast();
	}

	public String getText() {
		return text;
	}

	public Character getLast() {
		if (text.isEmpty()) return null;
		return text.charAt(text.length() - 1);
	}

	private void revert(Node node) {
		switch (node.type) {
			case APPEND -> revertAppend(node.str);
			case DELETE -> revertDelete(node.str);
			default -> throw new IllegalArgumentException("Unexpected node type " + node.type);
		}
	}

	private void revertDelete(String str) {
		text = text + str;
	}

	private void revertAppend(String str) {
		text = text.substring(0, text.length() - str.length());
	}

	private class Node {
		private OperationType type;
		private String str;

		public Node(OperationType type, String str) {
			this.type = type;
			this.str = str;
		}
	}

	private enum OperationType {
		APPEND,
		DELETE
	}
}
