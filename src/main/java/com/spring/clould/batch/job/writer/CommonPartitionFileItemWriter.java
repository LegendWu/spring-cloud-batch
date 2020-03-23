package com.spring.clould.batch.job.writer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

public class CommonPartitionFileItemWriter<T> extends FlatFileItemWriter<T> {

    private FileSystemResource fileSystemResource;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(Class clz) {
        BeanWrapperFieldExtractor beanWrapperFieldExtractor = new BeanWrapperFieldExtractor();
        Field[] fields = clz.getDeclaredFields();
        List<String> list = new ArrayList<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                list.add(field.getName());
            }
        }
        String[] names = new String[list.size()];
        beanWrapperFieldExtractor.setNames(list.toArray(names));
        beanWrapperFieldExtractor.afterPropertiesSet();
        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
        setLineAggregator(lineAggregator);
        setName(clz.getSimpleName());
        setEncoding("UTF-8");
    }

    @SuppressWarnings("rawtypes")
	public CommonPartitionFileItemWriter(Class clz,String startId, String endId) {
        init(clz);
        System.out.println("写文件，startId="+startId+", endId="+endId);
        fileSystemResource = new FileSystemResource("D:\\test\\source\\"+ clz.getSimpleName()+"-"+startId + "-" + endId  + ".csv");
        setResource(fileSystemResource);
    }
}
