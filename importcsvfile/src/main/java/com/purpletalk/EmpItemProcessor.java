package com.purpletalk;

import org.springframework.batch.item.ItemProcessor;

import com.purpletalk.entities.TestEmp;

public class EmpItemProcessor  implements ItemProcessor<TestEmp, TestEmp> {

	@Override
	public TestEmp process(TestEmp testEmp) throws Exception {
		final String firstName=testEmp.getFirstName().toUpperCase();
		final String lastName=testEmp.getLastName().toUpperCase();
		final TestEmp obj=new TestEmp(firstName,lastName);
		return obj;
	}

}
