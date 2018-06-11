package com.purpletalk;

import org.springframework.batch.item.ItemProcessor;

public class EmpProcessor implements ItemProcessor<EmpDTO, EmpDTO>{

	@Override
	public EmpDTO process(EmpDTO empDTO) throws Exception {
		final String id=empDTO.getId();
		final String firstname=empDTO.getFirstname();
		final String lastname=empDTO.getLastname();
		final String salary=empDTO.getSalary();
		
		final EmpDTO transformedEmpDTO=new EmpDTO(id,firstname,lastname,salary);
		
		System.out.println("Converting (" + empDTO + ") into (" + transformedEmpDTO + ")");
		return transformedEmpDTO;
	}

}
