package com.asemicanalytics.config.parser.yaml;

import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.MaterializedColumnRepository;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityLogicalTableParser {
  private static final String CONFIG_FILE = "config.yml";
  private final YamlFileLoader yamlFileLoader;

  public EntityLogicalTableParser(YamlFileLoader yamlFileLoader) {
    this.yamlFileLoader = yamlFileLoader;
  }

  private List<EntityPropertiesDto> loadProperties(Path path) {
    List<EntityPropertiesDto> columns = new ArrayList<>();

    for (File file : new File(path.toUri()).listFiles()) {
      try {
        columns.add(
            yamlFileLoader.load(Files.readString(file.toPath()), EntityPropertiesDto.class));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return columns;
  }

  private List<EntityKpisDto> loadKpis(Path path) {
    List<EntityKpisDto> kpis = new ArrayList<>();

    for (File file : new File(path.toUri()).listFiles()) {
      try {
        kpis.add(yamlFileLoader.load(Files.readString(file.toPath()), EntityKpisDto.class));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return kpis;
  }

  public Optional<EntityDto> parse(
      Path propertiesPath, Path kpisPath, Map<String, ActionLogicalTable> actionLogicalTables) {

    if (!propertiesPath.toFile().exists()) {
      return Optional.empty();
    }

    var configPath = propertiesPath.getParent().resolve(CONFIG_FILE);
    if (!configPath.toFile().exists() || configPath.toFile().isDirectory()) {
      throw new IllegalArgumentException("entity requires config.yml file");
    }
    EntityConfigDto configDto = null;
    try {
      configDto = yamlFileLoader.load(Files.readString(configPath), EntityConfigDto.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (!kpisPath.toFile().exists() || kpisPath.toFile().isFile()) {
      throw new IllegalArgumentException("UserWide requires kpis directory");
    }

    return Optional.of(new EntityDto(
        configDto,
        loadProperties(propertiesPath),
        loadKpis(kpisPath),
        actionLogicalTables
    ));
  }

}
