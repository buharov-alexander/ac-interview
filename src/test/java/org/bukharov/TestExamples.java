package org.bukharov;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;


public class TestExamples {

	@Test
	void mockTest() {
		Solution mock = mock(Solution.class);
		when(mock.toString()).thenReturn("Mocked value");

		System.out.println(mock.toString());

	}

	@Test
	void assertjTest() {
		List<String> list1 = List.of("banana", "apple");
		List<String> list2 = List.of("apple", "banana");

		assertThat(list1).hasSameElementsAs(list2);
	}
}
