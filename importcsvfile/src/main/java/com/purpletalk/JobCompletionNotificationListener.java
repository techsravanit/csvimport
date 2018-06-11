package com.purpletalk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.purpletalk.entities.TestEmp;

public class JobCompletionNotificationListener  extends JobExecutionListenerSupport {
	 
	  private final JdbcTemplate jdbcTemplate;
	 
	  public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
	    this.jdbcTemplate = jdbcTemplate;
	  }
	 
	  @Override
	  public void afterJob(JobExecution jobExecution) {
	    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
	 
	      List<TestEmp> results = jdbcTemplate
	          .query("SELECT firstname, lastname FROM test_emp", new RowMapper<TestEmp>() {
	            @Override
	            public TestEmp mapRow(ResultSet rs, int row) throws SQLException {
	              return new TestEmp(rs.getString(1), rs.getString(2));
	            }
	          });
	 
	      for (TestEmp person : results) {
	    	  System.out.println("emp found: "+person + "> in the database.");
	      }
	 
	    }
	  }

}
