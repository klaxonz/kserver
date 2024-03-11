package com.klaxon.kserver.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class CodeGenerator {

    public static void generate(String moduleName, String tableName) throws IOException {

        String modulePackage = "com.klaxon.kserver.module." + moduleName;
        String entityPackage = "model.entity";
        String mapperPackage = "mapper";

        String author = getGitUsername();

        // 数据源配置
        FastAutoGenerator.create(buildDataSource())
                .globalConfig(builder -> {
                    builder.author(author)// 设置作者
                            .disableOpenDir()       // 禁止打开输出目录 默认值:true
                            .commentDate("yyyy-MM-dd") // 注释日期
                            .dateType(DateType.ONLY_DATE)   // 定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })

                .packageConfig(builder -> {
                    builder.parent(modulePackage) // 父包模块名
                            .entity(entityPackage)// Entity 包名 默认值:entity
                            .mapper(mapperPackage); // Mapper 包名
                })

                .strategyConfig(builder -> {
                    builder.addInclude(tableName) // 设置需要生成的表名 可边长参数“user”, “user1”
                            .entityBuilder()// 实体类策略配置
                            .idType(IdType.ASSIGN_ID)// 主键策略  雪花算法自动生成的id
                            .addTableFills(new Column("create_time", FieldFill.INSERT)) // 自动填充配置
                            .addTableFills(new Property("update_time", FieldFill.INSERT_UPDATE))
                            .logicDeleteColumnName("deleted")// 说明逻辑删除是哪个字段
                            .enableFileOverride()
                            .enableLombok()
                            .enableTableFieldAnnotation();// 属性加上注解说明
                })

                // 只生成 entity
                .templateConfig(builder -> {
                    builder.disable(
                            TemplateType.XML,
                            TemplateType.SERVICE,
                            TemplateType.SERVICE_IMPL,
                            TemplateType.CONTROLLER);
                })

                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

    }

    private static DataSourceConfig.Builder buildDataSource() throws FileNotFoundException {
        String profile = System.getProperty("user.dir") + "/src/main/resources/application-dev.yml";
        Map<String, Object> yamlData = YamlConfigReader.readYaml(profile);
        Map<String, Object> datasource = (Map<String, Object>) ((Map<String, Object>) yamlData.get("spring")).get("datasource");

        String url = (String) datasource.get("url");
        String username = (String) datasource.get("username");
        String password = (String) datasource.get("password");

        return new DataSourceConfig.Builder(url, username, password);
    }

    private static String getGitUsername() throws IOException {
        Process process = Runtime.getRuntime().exec("git config user.name");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String gitUsername = reader.readLine();
        reader.close();
        return gitUsername;
    }

}
