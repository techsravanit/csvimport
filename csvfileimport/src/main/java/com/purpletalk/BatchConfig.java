package com.purpletalk;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableBatchProcessing
@Configuration
public class BatchConfig {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public FlatFileItemReader<EmpDTO> csvEmpReader(){
		System.out.println("------------------------------------------");
		FlatFileItemReader<EmpDTO> reader = new FlatFileItemReader<EmpDTO>();
		reader.setResource(new ClassPathResource("test_emp.csv"));
		System.out.println("------------------------------------*");
		reader.setLinesToSkip(1);
		reader.setLineMapper(new DefaultLineMapper<EmpDTO>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] { "id", "firstname", "lastname","salary" });
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<EmpDTO>() {{
				setTargetType(EmpDTO.class);
			}});
		}});
		return reader;
	}

	@Bean
	ItemProcessor<EmpDTO, EmpDTO> csvEmpProcessor() {
		return new EmpProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<EmpDTO> csvEmpWriter() {
		JdbcBatchItemWriter<EmpDTO> csvEmpWriter = new JdbcBatchItemWriter<EmpDTO>();
		csvEmpWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<EmpDTO>());
		csvEmpWriter.setSql("INSERT INTO test_emp (id, firstname, lastname, salary) VALUES (:id, :firstname, :lastname, :salary)");
		csvEmpWriter.setDataSource(dataSource);
		return csvEmpWriter;
	}

	@Bean
	public Step csvFileToDatabaseStep() {
		return stepBuilderFactory.get("csvFileToDatabaseStep")
				.<EmpDTO, EmpDTO>chunk(1)
				.reader(csvEmpReader())
				.processor(csvEmpProcessor())
				.writer(csvEmpWriter())
				.build();
	}

	@Bean
	Job csvFileToDatabaseJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("csvFileToDatabaseJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(csvFileToDatabaseStep())
				.end()
				.build();
	}

}
