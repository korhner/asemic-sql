package com.asemicanalytics.config.enrichment;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.Enrichment;
import com.asemicanalytics.core.logicaltable.EnrichmentColumnPair;
import com.asemicanalytics.core.logicaltable.LogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.List;
import java.util.Map;

public class EnrichmentResolver {
  public static void resolve(Map<String, ? extends LogicalTable<Column>> logicalTables,
                             EntityLogicalTable entityLogicalTable,
                             List<EnrichmentDefinition> enrichmentDefinitions) {
    enrichmentDefinitions.forEach(e -> {
      var source = logicalTables.get(e.sourceLogicalTable());
      var target = logicalTables.get(e.targetLogicalTable());
      source.addEnrichment(new Enrichment(target, e.enrichmentColumnPairs()));
    });

    logicalTables.values().stream()
        .filter(d -> d instanceof EventLogicalTable)
        .map(d -> (EventLogicalTable) d)
        .forEach(d -> d.addEnrichment(
            new Enrichment(entityLogicalTable, List.of(
                new EnrichmentColumnPair(d.getDateColumn().getId(),
                    entityLogicalTable.getDateColumn().getId()),
                new EnrichmentColumnPair(d.entityIdColumn().getId(),
                    entityLogicalTable.entityIdColumn().getId())
            ))
        ));

  }

}
