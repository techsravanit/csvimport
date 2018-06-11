package com.purpletalk;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.purpletalk.entities.TestEmp;

public class BatchConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	 public DataSource dataSource() {
	  final DriverManagerDataSource dataSource = new DriverManagerDataSource();
	  dataSource.setDriverClassName("org.postgresql.Driver");
	  dataSource.setUrl("jdbc:postgresql://192.168.7.45:5432/postgres");
	  dataSource.setUsername("postgres");
	  dataSource.setPassword("postgres");
	  
	  return dataSource;
	 }
	
	@Bean
	public FlatFileItemReader<TestEmp> reader() {

		FlatFileItemReader<TestEmp> reader = new FlatFileItemReader<>();
		System.out.println("hiiiiiiiiiiiiiiiiiiii");
		reader.setResource(new ClassPathResource("test_emp.csv"));
		reader.setLinesToSkip(1);
		reader.setLineMapper(new DefaultLineMapper<TestEmp>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[]{"id","firstName", "lastName","salary"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<TestEmp>() {{
				setTargetType(TestEmp.class);
			}});
		}});
		System.out.println("endddddddddddd");
		return reader;
	}

	@Bean
	  public EmpItemProcessor processor() {
	    return new EmpItemProcessor();
	  }

	@Bean
	public JdbcBatchItemWriter<TestEmp> writer() {
		JdbcBatchItemWriter<TestEmp> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<TestEmp>());
		writer.setSql("INSERT INTO test_emp (id, first_name, last_name, salary) VALUES (:id, :firstName, :lastName, :salary)");
		writer.setDataSource(dataSource);
		return writer;
	}
	
	 @Bean
	  public Step csvFileToDatabaseStep() {
	    return stepBuilderFactory.get("csvFileToDatabaseStep").<TestEmp, TestEmp>chunk(10).reader(reader())
	        .processor(processor()).writer(writer()).build();
	  }
	 
	  @Bean
	  public Job importUserJob(JobCompletionNotificationListener listener) {
	    return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
	        .listener(listener).flow(csvFileToDatabaseStep()).end().build();
	  }
}
