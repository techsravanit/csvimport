package com.purpletalk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("hiii");
			List<EmpDTO> results = jdbcTemplate.query("SELECT id, firstname, lastname, salary FROM test_emp", new RowMapper<EmpDTO>() {
				@Override
				public EmpDTO mapRow(ResultSet rs, int row) throws SQLException {
					return new EmpDTO(rs.getString(1), rs.getString(2), rs.getString(3),rs.getString(4));
				}
			});

			for (EmpDTO EmpDTO : results) {
				System.out.println("Discovered <" + EmpDTO + "> in the database.");
			}

		}
	}
	
}
